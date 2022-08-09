package api.service;

import api.interfaces.IRoutes;
import api.service.utils.RequestParser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;


public final class Service {

    private RequestParser requestParser;
    private RequestSpecBuilder requestSpecBuilder;
    private RequestSpecification requestSpecification;
    private static Response response;

    private Class<?> declaringClass;
    private Object[] enumFields;
    private static IRoutes _route;

    public static Service init() {
        return new Service();
    }

    private <E extends Enum<E>> void initializeRoute(Enum<E> route) throws Exception {
        if(_route == null || declaringClass != route.getDeclaringClass()) {
            declaringClass = route.getDeclaringClass();
            enumFields = declaringClass.getEnumConstants();
        }

        getEnumField(route);
        initRequestSpecs();
    }

    @SuppressWarnings(value = "all")
    private <E extends Enum<E>> void getEnumField(Enum<E> route) throws Exception {
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

    private void initRequestSpecs() {
        EncoderConfig encoderconfig = new EncoderConfig();
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(_route.service_url());
        requestSpecBuilder.setBasePath(_route.endpoint_path());
        requestSpecBuilder.setUrlEncodingEnabled(false);
        requestSpecBuilder.setConfig(RestAssured.config().encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
    }

    public Service headers(Map<String, String> headers) {
        requestSpecBuilder.addHeaders(headers);
        return this;
    }

    public Service cookie(Cookie cookie) {
        requestSpecBuilder.addCookie(cookie);
        return this;
    }

    public Service cookies(Map<String, String> cookies) {
        requestSpecBuilder.addCookies(cookies);
        return this;
    }

    public Service cookies(Cookies cookies) {
        requestSpecBuilder.addCookies(cookies);
        return this;
    }

    public Service queryParam(String key, String value) {
        requestSpecBuilder.addQueryParam(key, value);
        return this;
    }

    public Service queryParams(Map<String, ?> queryParams) {
        requestSpecBuilder.addQueryParams(queryParams);
        return this;
    }

    public Service pathParam(String key, String value) {
        requestSpecBuilder.addPathParam(key, value);
        return this;
    }

    public Service pathParams(Map<String, ?> pathParams) {
        requestSpecBuilder.addPathParams(pathParams);
        return this;
    }

    public Service body(Object body) {
        requestSpecBuilder.setBody(body);
        return this;
    }

    public Service path(String path) {
        requestSpecBuilder.setBasePath(path);
        return this;
    }

    public <E extends Enum<E>> Service get(Enum<E> route, Map<String, String> additionalHeaders) throws Exception {
        initializeRoute(route);

        requestParser = getRequest(additionalHeaders);
        setRequestSpecifications();

        // RestAssured.useRelaxedHTTPSValidation();
        if (containsIllegals(_route.service_url())) {
            response = given().log().all().spec(requestSpecification).when().get(requestParser.endpoint).then().extract().response();
            return this;
        }

        response = given().log().all().spec(requestSpecification).when().get().then().extract().response();
        return this;
    }

    @SafeVarargs
    public final <E extends Enum<E>> Service post(Enum<E> route, Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        initializeRoute(route);

        requestParser = getRequest(additionalHeaders, body);
        setRequestSpecifications();

        if (containsIllegals(_route.service_url())) {
            response = given().log().all().spec(requestSpecification).when().post(requestParser.endpoint).then().extract().response();
            return this;
        }

        response = given().log().all().spec(requestSpecification).when().post().then().extract().response();
        return this;
    }

    @SafeVarargs
    public final <E extends Enum<E>> Service put(Enum<E> route, Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        initializeRoute(route);

        requestParser = getRequest(additionalHeaders, body);
        setRequestSpecifications();

        if (containsIllegals(_route.service_url())) {
            response = given().log().all().spec(requestSpecification).when().put(requestParser.endpoint).then().extract().response();
            return this;
        }

        response = given().log().all().spec(requestSpecification).when().put().then().extract().response();
        return this;
    }

    @SafeVarargs
    public final <E extends Enum<E>> Service patch(Enum<E> route, Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        initializeRoute(route);

        requestParser = getRequest(additionalHeaders, body);
        setRequestSpecifications();

        if (containsIllegals(_route.service_url())) {
            response = given().log().all().spec(requestSpecification).when().patch(requestParser.endpoint).then().extract().response();
            return this;
        }

        response = given().log().all().spec(requestSpecification).when().patch().then().extract().response();
        return this;
    }

    public <E extends Enum<E>> Service delete(Enum<E> route, Map<String, String> additionalHeaders) throws Exception {
        initializeRoute(route);

        requestParser = getRequest(additionalHeaders);

        setRequestSpecifications();

        if (containsIllegals(_route.service_url())) {
            response = given().log().all().spec(requestSpecification).when().delete(requestParser.endpoint).then().extract().response();
            return this;
        }

        response = given().log().all().spec(requestSpecification).when().delete().then().extract().response();
        return this;
    }

    public <T> T responseToPojo(Class<T> type) throws Exception {
        try {
            return new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(response.asString(), type);
        } catch(IOException ioException) {
            throw new Exception("Response Received did not match the expected Response Format POJO: " + type.getName() + ioException);
        }
    }

    @SafeVarargs
    private static RequestParser getRequest(Map<String, String> additionalHeaders, Map<Object, Object>... body) throws Exception {
        return new RequestParser(additionalHeaders, body);
    }

    public static IRoutes getRoute() {
        return _route;
    }

    public static Response getResponse() {
        return response;
    }

    private boolean containsIllegals(String toExamine) {
        Pattern pattern = Pattern.compile("[~@*?<>^]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    private void setRequestSpecifications() {
        requestSpecBuilder.addHeaders(requestParser.headers);
        requestSpecBuilder.setBody(requestParser.body);

        requestSpecification = requestSpecBuilder.build();
    }
}
