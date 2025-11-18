package Utility;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    public static Properties loadProperties() {
        Properties prop = new Properties();

        try {
            FileInputStream fis = new FileInputStream("./Resources/config.properties");
            prop.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prop;
    }
}
