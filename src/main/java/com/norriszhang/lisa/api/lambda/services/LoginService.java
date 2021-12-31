package com.norriszhang.lisa.api.lambda.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.norriszhang.lisa.api.lambda.datamodels.Session;
import com.norriszhang.lisa.api.lambda.datamodels.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoginService {
    private final DynamoDBMapper dbMapper;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LoginService() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.dbMapper = new DynamoDBMapper(client);
    }

    public String login(String username, String password) {
        User user = this.retrieveUser(username, password);
        if (user != null) {
            return this.createSession(user);
        }
        throw new RuntimeException("Invalid username or password.");
    }

    private String createSession(User user) {
        Session session = new Session();
        UUID uuid = UUID.randomUUID();
        session.setId(uuid.toString());
        session.setUserInfo(gson.toJson(user));
        session.setLastUpdated(ZonedDateTime.now());
        dbMapper.save(session);
        return session.getId();
    }

    private User retrieveUser(String username, String password) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v_username", new AttributeValue().withS(username));
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("username-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("username = :v_username")
                .withExpressionAttributeValues(eav);

        List<User> items = dbMapper.query(User.class, queryExpression);

        if (items == null || items.isEmpty()) {
            return null;
        }

        User user = items.get(0);

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        boolean matches = encoder.matches(password, user.getPassword());
        if (matches) {
            return user;
        }

        return null;
    }
}
