package com.norriszhang.lisa.api.lambda.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.norriszhang.lisa.api.lambda.datamodels.DebugLog;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DebugLogsService {
    private final DynamoDBMapper dbMapper;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DebugLogsService() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.dbMapper = new DynamoDBMapper(client);
    }

    public void logEventContext(APIGatewayV2ProxyRequestEvent event, Context context) {
        Map<String, Object> result = new HashMap<>();
        result.put("event", event);
        result.put("context", context);
        DebugLog debugLog = new DebugLog();
        debugLog.setId(UUID.randomUUID().toString());
        debugLog.setTimestamp(ZonedDateTime.now());
        debugLog.setMessage(gson.toJson(result));
        dbMapper.save(debugLog);
    }
}
