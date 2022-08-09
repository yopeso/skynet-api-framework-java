package api.service.utils;

import org.json.JSONObject;
import utils.Logger;


import java.util.Map;

public class RequestParser {
    public Map<String, String> headers;
    public String endpoint;
    public String body = "";

    @SafeVarargs
    public RequestParser(Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        Map<String, Map<String, String>> serviceDetails = getSearchServiceDetails(additionalHeaders, body);
        parseValues(serviceDetails);
        endpoint = serviceDetails.get("endpoints").get("endpoint");
    }

    private void parseValues(Map<String, Map<String, String>> lctrs) throws Exception {
        if (lctrs == null) {
            throw new Exception("No info was provided for this request!");
        }

        lctrs.forEach((key, value) -> {
            switch(key) {
                case "headers" -> {
                    headers = value;
                }
                case "body" -> {
                    body = new JSONObject(value).toString();
                }
                case "endpoints" -> {
                    endpoint = value.toString();
                }
                default -> {
                    Logger.error("Case not implemented yet for: " + key);
                }
            }
        });
    }

    @SafeVarargs
    private Map<String, Map<String, String>> getSearchServiceDetails(Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        return new RequestDetails(additionalHeaders, body).getServiceDetails();
    }
}
