package com.norriszhang.lisa.api.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.norriszhang.lisa.api.lambda.datamodels.Clazz;
import com.norriszhang.lisa.api.lambda.helpers.ResponseHelper;
import com.norriszhang.lisa.api.lambda.services.ClassesService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetClassesHandler extends BaseHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {
    private final ClassesService classesService = new ClassesService();
    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent event, Context context) {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("Handling GET /classes...\n");
            Map<String, String> headers = event.getHeaders();
            logger.log(gson.toJson(headers));
            if (headers == null || headers.isEmpty()) {
                return unsuccessful(401, "No header");
            }
            String sessionId = headers.get("sessionid");
            if (sessionId == null || sessionId.trim().equals("")) {
                return unsuccessful(401, "No login info found in header.");
            }

            List<Clazz> classes = classesService.getClasses(sessionId);

            return successful(classes);
        } catch (Exception e) {
            return unsuccessful(500, e.getMessage());
        }
    }

    private APIGatewayV2ProxyResponseEvent successful(List<Clazz> classes) {
        APIGatewayV2ProxyResponseEvent response = ResponseHelper.getResponseTemplate(200);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Successful");
        result.put("classes", classes);
        response.setBody(gson.toJson(result));
        return response;
    }
}
