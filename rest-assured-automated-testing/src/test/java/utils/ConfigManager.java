package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {


    private static ConfigManager instance;
    private static final Properties properties = new Properties();


    private ConfigManager() throws IOException {

        InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);

    }

      public static ConfigManager getInstance() {

        try {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return instance;
    }


    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
