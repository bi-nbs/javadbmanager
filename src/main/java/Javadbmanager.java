import Properties.PropertiesShop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Javadbmanager {




    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info(PropertiesShop.getProperties().getProperty("mysqlusername"));
    }

}
