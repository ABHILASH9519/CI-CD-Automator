package com.stalin.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@RestController
public class RepositoryDetailsController {

    @Autowired
    private Environment env;

    @RequestMapping("/")
    public String getRepos() {
        return "This is the sample DevOps Project";
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<String, String>> getTwitterTrends(
            @RequestParam("placeid") String trendPlace,
            @RequestParam("count") String trendCount) {
        Map<String, String> response = new HashMap<>();

        final int placeId;
        final int count;
        try {
            placeId = Integer.parseInt(trendPlace);
            count = Integer.parseInt(trendCount);
        } catch (NumberFormatException ex) {
            response.put("error", "placeid and count must be integers");
            return ResponseEntity.badRequest().body(response);
        }

        if (placeId <= 0 || count < 1 || count > 20) {
            response.put("error", "placeid must be positive and count must be between 1 and 20");
            return ResponseEntity.badRequest().body(response);
        }

        String consumerKey = env.getProperty("CONSUMER_KEY");
        String consumerSecret = env.getProperty("CONSUMER_SECRET");
        String accessToken = env.getProperty("ACCESS_TOKEN");
        String accessTokenSecret = env.getProperty("ACCESS_TOKEN_SECRET");
        if (isBlank(consumerKey) || isBlank(consumerSecret)
                || isBlank(accessToken) || isBlank(accessTokenSecret)) {
            response.put("error", "trend provider credentials are not configured");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setDebugEnabled(false)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        Twitter twitter = new TwitterFactory(configuration.build()).getInstance();
        try {
            Trends trends = twitter.getPlaceTrends(placeId);
            int added = 0;
            for (Trend trend : trends.getTrends()) {
                if (added >= count) {
                    break;
                }
                response.put(trend.getName(), trend.getURL());
                added++;
            }
            return ResponseEntity.ok(response);
        } catch (TwitterException ex) {
            response.put("error", "trend provider request failed");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
