package com.ardoq.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    Properties config = new Properties();

    public Config(String filename) throws IOException {
        FileReader reader = new FileReader(filename);
        config.load(reader);
        reader.close();
    }

    public String getArdoqHost() {
        return config.getProperty("host", "https://app.ardoq.com");
    }

    public String getArdoqToken() {
        return config.getProperty("token");
    }

    public String getOrganization() {
        return config.getProperty("organization");
    }

    public String getSlackToken() {
        return config.getProperty("slacktoken");
    }

}
