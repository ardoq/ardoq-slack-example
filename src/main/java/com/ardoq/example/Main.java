package com.ardoq.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ardoq.ArdoqClient;
import com.ardoq.model.Component;
import com.ardoq.model.Field;
import com.ardoq.model.FieldType;
import com.ardoq.model.Model;
import com.ardoq.model.Reference;
import com.ardoq.model.Workspace;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;


public class Main {
    private static final String DEFAULT_CONFIG_FILE_NAME = "src/main/resources/ardoq.properties";

    public static void main(String[] args) {
        String configFile = DEFAULT_CONFIG_FILE_NAME;
        if (args.length == 1) {
            configFile = args[0];
        }

        try {
            System.out.println("Loading config: " + configFile);
            new Main(new Config(configFile));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArdoqClient getClient(Config config) {
        System.out.println("importing to " + config.getArdoqHost());
        ArdoqClient client = new ArdoqClient(config.getArdoqHost(), config.getArdoqToken());
        client.setOrganization(config.getOrganization());
        return client;
    }

    public Main(Config config) throws Exception {
        ArdoqClient client = getClient(config);


        // Setting up Slack listener
        SlackSession session = SlackSessionFactory.
                  createWebSocketSlackSession("authenticationtoken");
                session.connect();


        // creating workspace
        Model template = client.model().getTemplateByName("ArchiMate Physical Layer");
        Workspace ws = client.workspace().createWorkspaceFromTemplate("field test", template.getId(), "Blank ArchiMate Physical Layer WS");

        Model model = client.model().getModelById(ws.getComponentModel());
        String typeId = model.getComponentTypeByName("Physical Facility");

        Component comp1 = client.component().createComponent(new Component("c1", ws.getId(), "", typeId));
        Component comp2 = client.component().createComponent(new Component("c2", ws.getId(), "", typeId));

        Reference ref1 = client.reference().createReference(new Reference(ws.getId(), "ref 1-2", comp1.getId(), comp2.getId(), 3));

        Field field1 = new Field("f1", "f1", model.getId(), FieldType.TEXT);
        field1 = client.field().createField(field1);

        List<String> componentTypes = new ArrayList<String>();
        componentTypes.add(model.getComponentTypeByName("Application"));
        field1.setGlobalref(true);

        Map<String,Object> comp1fields = new HashMap<String,Object>();
        comp1fields.put("f1", "Component 1, Field 1");
        comp1.setFields(comp1fields);
        comp1 = client.component().updateComponent(comp1.getId(), comp1);
        comp1.setFields(comp1fields);

        System.out.println(comp1.getFields().get("f1"));

        comp1fields.put("c1f1", null);
        comp1.setFields(comp1fields);

        comp1 = client.component().updateComponent(comp1.getId(), comp1);

        System.out.println("Should be null: " + comp1.getFields().get("c1f1"));

        // reference field
        Map<String,Object> ref1fields = new HashMap<String,Object>();
        ref1fields.put("r1f1", "R1F1-VALUE");
        ref1.setFields(ref1fields);
        ref1 = client.reference().updateReference(ref1.getId(), ref1);
        System.out.println(ref1.getFields().get("r1f1"));

        ref1fields.put("r1f1", null);
        ref1.setFields(ref1fields);
        ref1 = client.reference().updateReference(ref1.getId(), ref1);

        System.out.println("Should be null: " + ref1.getFields().get("r1f1"));

        System.out.println("Done!");
    }



}
