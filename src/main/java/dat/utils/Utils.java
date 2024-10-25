package dat.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.security.exceptions.ApiException;
import io.javalin.http.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {
    public static void main(String[] args) {
        System.out.println(getPropertyValue("db.name", "properties-from-pom.properties"));
    }

    public static String getPropertyValue(String propName, String resourceName) {
        // HUSK AT BUILDE MED MAVEN FØRST. Læs ejendomsfilen, hvis ikke deployed (ellers læs systemvariable i stedet)
        // Læs fra ressourcer/config.properties eller fra pom.xml afhængigt af resourceName
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null) {
                return value.trim();  // Fjern evt. mellemrum
            } else {
                throw new ApiException(500, String.format("Egenskab %s blev ikke fundet i %s", propName, resourceName));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ApiException(500, String.format("Kunne ikke læse egenskaben %s. Huskede du at bygge projektet med MAVEN?", propName));
        }
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignorer ukendte egenskaber i JSON
        objectMapper.registerModule(new JavaTimeModule()); // Serialiser og deserialiser java.time objekter
        objectMapper.writer(new DefaultPrettyPrinter());
        return objectMapper;
    }

    public static String convertToJsonMessage(Context ctx, String property, String message) {
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put(property, message);  // Indsæt beskeden i mappen
        msgMap.put("status", String.valueOf(ctx.status()));  // Indsæt status i mappen
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(msgMap);  // Konverter mappen til JSON
        } catch (Exception e) {
            return "{\"error\": \"Kunne ikke konvertere besked til JSON\"}";
        }
    }
}
