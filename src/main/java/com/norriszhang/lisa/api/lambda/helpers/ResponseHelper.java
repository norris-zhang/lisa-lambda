package com.norriszhang.lisa.api.lambda.helpers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
    public static APIGatewayV2ProxyResponseEvent getResponseTemplate(int httpStatus) {
        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setIsBase64Encoded(false);
        response.setStatusCode(httpStatus);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        return response;
    }
}
