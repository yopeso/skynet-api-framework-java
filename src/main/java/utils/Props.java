package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public final class Props {
    private final static String TEST_PROPS = "./src/test/resources/test.properties";

    private static Properties properties;

    private static void getInstance(String... filePath) throws Exception {
        if (properties == null) {
            properties = new Properties();
            System.out.println("Loading Properties...");

            try {
                FileInputStream inputStream = new FileInputStream(filePath != null && filePath.length > 0 ? filePath[0] : TEST_PROPS);
                properties.load(inputStream);
            } catch (FileNotFoundException ex) {
                throw new FileNotFoundException(ex.getMessage());
            } catch (IOException e) {
                throw new IOException(MessageFormat.format("{0}\n{1}", e.getCause(), e.getMessage()));
            }
        }
    }

    public static String getProperty(String propertyName) throws Exception {
        Object value = properties.get(propertyName);
        if (value != null) {
            return properties.get(propertyName).toString();
        } else {
            String errorLog = MessageFormat.format("Error occurred while getting {0} Property from MlsTestProperties. This could be due to no such property available in MlsTestProperties.properties file.", propertyName);
            throw new Exception(errorLog);
        }
    }

    public static String getProperty(String filePath, String propertyName) throws Exception {
        getInstance(filePath);
        Object value = properties.get(propertyName);
        if (value != null) {
            return properties.get(propertyName).toString();
        } else {
            String errorLog = MessageFormat.format("Error occurred while getting {0} Property from MlsTestProperties. This could be due to no such property available in MlsTestProperties.properties file.", propertyName);
            throw new Exception(errorLog);
        }
    }
}
