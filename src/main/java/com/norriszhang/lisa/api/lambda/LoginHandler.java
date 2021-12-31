package com.norriszhang.lisa.api.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.norriszhang.lisa.api.lambda.helpers.ResponseHelper;
import com.norriszhang.lisa.api.lambda.services.LoginService;

import java.util.HashMap;
import java.util.Map;

public class LoginHandler extends BaseHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {
    private final LoginService loginService = new LoginService();
    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling login...\n");

        String body = event.getBody();
        if (body == null) {
            return bodyNotFound();
        }

        HashMap bodyMap = gson.fromJson(body, HashMap.class);
        String username = (String)bodyMap.get("username");
        String password = (String)bodyMap.get("password");

        try {
            String sessionId = loginService.login(username, password);
            return successful(sessionId);
        } catch (Exception e) {
            logger.log("Exception: \n");
            logger.log(e.getMessage());
            logger.log("\n");
            return unsuccessful(401, e.getMessage());
        }

    }

    private APIGatewayV2ProxyResponseEvent successful(String sessionId) {
        APIGatewayV2ProxyResponseEvent response = ResponseHelper.getResponseTemplate(200);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Successful");
        result.put("sessionId", sessionId);
        response.setBody(gson.toJson(result));
        return response;
    }
}
