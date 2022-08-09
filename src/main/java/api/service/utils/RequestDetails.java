package api.service.utils;

import api.service.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestDetails {
    private final Map<String, Map<String, String>> serviceDetails = new LinkedHashMap<>();

    @SafeVarargs
    public RequestDetails(Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        serviceDetails(additionalHeaders, body);
    }

    @SuppressWarnings("all")
    private void serviceDetails(Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        setHeaders(additionalHeaders);

        serviceDetails.put("endpoints", new HashMap<String, String>() {{
            put("endpoint", Service.getRoute().absolute_url());
        }});

        if(body == null || body.length < 1) return;

        serviceDetails.put("body", new ObjectMapper().readValue(bodyDetails(body[0]), LinkedHashMap.class));
    }

    private void setHeaders(Map<String, String> additionalHeaders) {
        serviceDetails.put("headers", new HashMap<>() {{
            put("Accept", "application/json");
            put("Accept-Encoding", "gzip, deflate");
            put("Content-Type", "application/json");
        }});

        if(additionalHeaders != null) {
            serviceDetails.get("headers").putAll(additionalHeaders);
        }
    }

    private <E extends Enum<E>> String bodyDetails(Map<Object, Object> body) throws Exception {
        JSONObject requestParams = new JSONObject();

        body.forEach((k, v) -> {
            requestParams.put(k.toString(), v);
        });

        return requestParams.toString();
    }

    public Map<String, Map<String, String>> getServiceDetails() {
        return serviceDetails;
    }
}
