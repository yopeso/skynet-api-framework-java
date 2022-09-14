package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
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

    public static String getProperty(String propertyName, boolean... throwException) throws Exception {
        Object value = properties.get(propertyName);
        if (value != null) {
            return properties.get(propertyName).toString();
        }

        String errorLog = MessageFormat.format("Error occurred while getting {0} property from {1}. This could be due to no such property available in {1} " +
                "file!", propertyName, TEST_PROPS);
        Logger.error(errorLog);

        if((throwException.length > 0 && throwException[0]) || throwException.length == 0) {
            throw new Exception(errorLog);
        }
        return null;
    }

    public static String getProperty(String filePath, String propertyName, boolean... throwException) throws Exception {
        getInstance(filePath);
        Object value = properties.get(propertyName);
        if (value != null) {
            return properties.get(propertyName).toString();
        }

        String errorLog = MessageFormat.format("Error occurred while getting {0} property from {1}. This could be due to no such property available in {1} " +
                "file!", propertyName, TEST_PROPS);
        Logger.error(errorLog);

        if((throwException.length > 0 && throwException[0]) || throwException.length == 0) {
            throw new Exception(errorLog);
        }
        return null;
    }

    public static String getSystemProperty(String propertyName, boolean... throwException) throws Exception {
        Optional<Map.Entry<Object, Object>> obj = System.getProperties().entrySet().stream().filter(x -> x.getKey().equals(propertyName)).findFirst();

        if(obj.isEmpty() && ((throwException.length > 0 && throwException[0]) || throwException.length == 0)) {
            String errorLog = MessageFormat.format("Error occurred while getting {0} property from System Properties. This could be due to no such property available in System Properties!", propertyName);
            throw new Exception(errorLog);

        }

        return obj.isEmpty() ? null : obj.get().getValue().toString();
    }
}
