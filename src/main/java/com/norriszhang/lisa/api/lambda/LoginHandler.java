package com.norriszhang.lisa.api.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.norriszhang.lisa.api.lambda.helpers.ResponseHelper;
import com.norriszhang.lisa.api.lambda.services.DebugLogsService;
import com.norriszhang.lisa.api.lambda.services.LoginService;

import java.util.HashMap;
import java.util.Map;

public class LoginHandler extends BaseHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {
    private final LoginService loginService = new LoginService();
    private final DebugLogsService debugLogsService = new DebugLogsService();
    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling login...\n");

        debugLogsService.logEventContext(event, context);

        String routeKey = event.getRequestContext().getRouteKey();
        String[] routeParts = routeKey.split(" ");
        if ("/checkLogin".equals(routeParts[1])) {
            return checkLogin(event, context);
        }


        String body = event.getBody();
        if (body == null) {
            return parametersNotFound();
        }

        HashMap bodyMap = gson.fromJson(body, HashMap.class);

        String username = (String) bodyMap.get("username");
        String password = (String) bodyMap.get("password");

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

    private APIGatewayV2ProxyResponseEvent checkLogin(APIGatewayV2ProxyRequestEvent event, Context context) {
        try {
            Map<String, String> headers = event.getHeaders();
            String sessionId = headers.get("sessionid");
            loginService.checkLogin(sessionId);
            APIGatewayV2ProxyResponseEvent response = ResponseHelper.getResponseTemplate(200);
            Map<String, String> result = new HashMap<>();
            result.put("status", "OK");
            response.setBody(gson.toJson(result));
            return response;
        } catch (Exception e) {
            return unsuccessful(401, "Not logged in.");
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
