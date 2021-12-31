package com.norriszhang.lisa.api.lambda.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.norriszhang.lisa.api.lambda.datamodels.Clazz;
import com.norriszhang.lisa.api.lambda.datamodels.Session;
import com.norriszhang.lisa.api.lambda.datamodels.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesService {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final DynamoDBMapper dbMapper;

    public ClassesService() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.dbMapper = new DynamoDBMapper(client);
    }

    public List<Clazz> getClasses(String sessionId) {
        Session session = this.dbMapper.load(Session.class, sessionId);
        if (session == null) {
            throw new RuntimeException("Session missing.");
        }
        String userInfo = session.getUserInfo();
        User user = gson.fromJson(userInfo, User.class);
        if ("TEACHER".equals(user.getRole())) {

            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":v_tenantUserId", new AttributeValue().withS(user.getId()));

            DynamoDBQueryExpression<Clazz> queryExpression = new DynamoDBQueryExpression<Clazz>()
                    .withIndexName("tenantUserId-index")
                    .withConsistentRead(false)
                    .withKeyConditionExpression("tenantUserId = :v_tenantUserId")
                    .withExpressionAttributeValues(eav);

            List<Clazz> items = dbMapper.query(Clazz.class, queryExpression);

            return items;
        } else {
            throw new RuntimeException("Not Implemented for roles other than TEACHER!");
        }
    }
}
