package org.example.xbotai.service.core.impl;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.example.xbotai.config.SocialMediaProperties;
import org.example.xbotai.provider.SocialMediaBotPropertiesProvider;
import org.example.xbotai.provider.SocialMediaUserPropertiesProvider;
import org.example.xbotai.service.core.SocialMediaService;
import org.springframework.stereotype.Service;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    private final SocialMediaUserPropertiesProvider propertiesProvider;

    private final SocialMediaBotPropertiesProvider botPropertiesProvider;

    private final BlockchainService blockchainService;

    private static final String TWEET_ENDPOINT = "https://api.twitter.com/2/tweets";

    public SocialMediaServiceImpl(SocialMediaUserPropertiesProvider propertiesProvider,
                                  SocialMediaBotPropertiesProvider botPropertiesProvider,
                                  BlockchainService blockchainService) {
        this.propertiesProvider = propertiesProvider;
        this.botPropertiesProvider = botPropertiesProvider;
        this.blockchainService = blockchainService;
    }

    /**
     * Post a tweet as a bot.
     */
    @Override
    public String postBotTweet(String tweetContent, boolean logToBlockchain) {
        SocialMediaProperties botProperties = botPropertiesProvider.getPropertiesForCurrentUser();
        return doPostTweet(botProperties, tweetContent, logToBlockchain);
    }

    /**
     * Post a tweet as a user.
     */
    @Override
    public String postUserTweet(String tweetContent, boolean logToBlockchain) {
        SocialMediaProperties userProperties = propertiesProvider.getPropertiesForCurrentUser();
        return doPostTweet(userProperties, tweetContent, logToBlockchain);
    }

    /**
     * General logic for publishing a tweet for any account.
     */
    private String doPostTweet(SocialMediaProperties props, String tweetContent, boolean logToBlockchain) {
        OAuth10aService service = new ServiceBuilder(props.getApiKey())
                .apiSecret(props.getApiSecretKey())
                .build(TwitterApi.instance());

        OAuth1AccessToken oauth1AccessToken = new OAuth1AccessToken(
                props.getAccessToken(),
                props.getAccessTokenSecret());

        OAuthRequest request = new OAuthRequest(Verb.POST, TWEET_ENDPOINT);
        request.addHeader("Content-Type", "application/json");
        String payload = "{\"text\":\"" + tweetContent + "\"}";
        request.setPayload(payload);
        service.signRequest(oauth1AccessToken, request);

        try {
            Response response = service.execute(request);
            if (response.getCode() == 201) {
                if (logToBlockchain) {
                    String blockchainResult = blockchainService.logTweetToBlockchain(tweetContent);
                    return "Tweet successfully posted! Blockchain log: " + blockchainResult;
                } else {
                    return "Tweet successfully posted!";
                }
            } else {
                return "Failed to post tweet: " + response.getCode() + " " + response.getBody();
            }
        } catch (Exception e) {
            return "Error occurred while posting tweet: " + e.getMessage();
        }
    }

    @Override
    public String postBotReplyTweet(String tweetContent, String inReplyToTweetId, boolean logToBlockchain) {
        OAuth10aService service = new ServiceBuilder(botPropertiesProvider.getPropertiesForCurrentUser().getApiKey())
                .apiSecret(botPropertiesProvider.getPropertiesForCurrentUser().getApiSecretKey())
                .build(TwitterApi.instance());

        OAuth1AccessToken oauth1AccessToken = new OAuth1AccessToken(
                botPropertiesProvider.getPropertiesForCurrentUser().getAccessToken(),
                botPropertiesProvider.getPropertiesForCurrentUser().getAccessTokenSecret());

        String payload = "{\"text\":\"" + tweetContent + "\", \"reply\":{\"in_reply_to_tweet_id\":\"" + inReplyToTweetId + "\"}}";
        OAuthRequest request = new OAuthRequest(Verb.POST, TWEET_ENDPOINT);
        request.addHeader("Content-Type", "application/json");
        request.setPayload(payload);

        service.signRequest(oauth1AccessToken, request);

        try {
            Response response = service.execute(request);
            if (response.getCode() == 201) {
                if (logToBlockchain) {
                    String blockchainResult = blockchainService.logTweetToBlockchain(tweetContent);
                    return "Tweet reply successfully posted! Blockchain log: " + blockchainResult;
                } else {
                    return "Tweet reply successfully posted!";
                }
            } else {
                return "Failed to post tweet reply: " + response.getCode() + " " + response.getBody();
            }
        } catch (Exception e) {
            return "Error occurred while posting tweet reply: " + e.getMessage();
        }
    }
}
