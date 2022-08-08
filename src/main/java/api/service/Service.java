package api.service;

import api.interfaces.IRoutes;
import api.service.utils.RequestParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.Logger;

import java.util.*;

import static io.restassured.RestAssured.given;


public final class Service {
    private static Class<?> declaringClass;
    private static Object[] enumFields;
    private static IRoutes _route;

    private static <E extends Enum<E>> void initializeRoute(Enum<E> route) throws Exception {
        if(_route == null || declaringClass != route.getDeclaringClass()) {
            declaringClass = route.getDeclaringClass();
            enumFields = declaringClass.getEnumConstants();
        }

        getEnumField(route);
    }

    @SuppressWarnings(value = "all")
    private static <E extends Enum<E>> void getEnumField(Enum<E> route) throws Exception {
        if(enumFields == null) {
            Logger.exception("Enum has no declared fields!");
        }

        for(Object c : enumFields) {
            if(route.name().equals(c.getClass().getDeclaredMethod("field_name").invoke(c))) {
                _route = (IRoutes) c;
                break;
            }
            _route = null;
        }

        if(_route == null) {
            Logger.exception("Case not implemented yet for services: " + route);
        }
    }

    public static <E extends Enum<E>> Response get(Enum<E> route, Map<String, String> additionalHeaders) throws Exception {
        initializeRoute(route);

        RequestParser rp = getRequest(additionalHeaders);
        //        RestAssured.useRelaxedHTTPSValidation();
        Logger.debug(String.format("Getting the following info: \nHeaders: %s \nBody: %s \nto Endpoint: %s", rp.headers, _route.absolute_url(), rp.endpoint));
        return given().headers(rp.headers).get(rp.endpoint).andReturn();
    }

    @SafeVarargs
    public static <E extends Enum<E>> Response post(Enum<E> route, Map<String, String> additionalHeaders, Map<Object, Object>... params) throws Exception {
        initializeRoute(route);

        RequestParser rp = getRequest(additionalHeaders, params);
        Logger.debug(String.format("Getting the following info: \nHeaders: %s \nBody: %s \nto Endpoint: %s", rp.headers, route, rp.endpoint));
        return given().headers(rp.headers).body(rp.body).post(rp.endpoint).andReturn();
    }

    public static <E extends Enum<E>> Response delete(Enum<E> route, Map<String, String> additionalHeaders) throws Exception {
        initializeRoute(route);
        RequestParser rp = getRequest(additionalHeaders);

        RestAssured.useRelaxedHTTPSValidation();
        Logger.debug(String.format("Getting the following info: \nHeaders: %s \nBody: %s \nto Endpoint: %s", rp.headers, route, rp.endpoint));
        return given().headers(rp.headers).delete(rp.endpoint).andReturn();
    }

    @SafeVarargs
    private static RequestParser getRequest(Map<String, String> additionalHeaders, Map<Object, Object>... params) throws Exception {
        return new RequestParser(additionalHeaders, params);
    }

    public static IRoutes getRoute() {
        return _route;
    }
}
