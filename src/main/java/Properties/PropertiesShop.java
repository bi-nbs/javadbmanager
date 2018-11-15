package Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class PropertiesShop {

    private static Logger logger = LogManager.getLogger();
    private static Properties properties = loadProps();

    private static Properties loadProps(){

        Properties defaultProps = new Properties();
        Properties customProps = new Properties();

        File defaultFile = new File("cfg/default.properties");
        File customFile = new File("cfg/custom.properties");

        try {
            InputStream defaultReader = new FileInputStream(defaultFile);
            InputStream customReader = new FileInputStream(customFile);

            defaultProps.load(defaultReader);
            customProps = new Properties(defaultProps);
            customProps.load(customReader);
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

        return customProps;
    }

    public static Properties getProperties() {
        return properties;
    }
}
