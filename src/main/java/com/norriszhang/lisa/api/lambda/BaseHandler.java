package com.norriszhang.lisa.api.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.norriszhang.lisa.api.lambda.helpers.ResponseHelper;

import java.util.HashMap;
import java.util.Map;

public class BaseHandler {
    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected APIGatewayV2ProxyResponseEvent unsuccessful(int httpStatus, String message) {
        APIGatewayV2ProxyResponseEvent response = ResponseHelper.getResponseTemplate(httpStatus);
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        response.setBody(gson.toJson(result));
        return response;
    }

    protected APIGatewayV2ProxyResponseEvent parametersNotFound() {
        APIGatewayV2ProxyResponseEvent response = ResponseHelper.getResponseTemplate(401);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Request parameters not found");
        response.setBody(gson.toJson(result));
        return response;
    }
}
