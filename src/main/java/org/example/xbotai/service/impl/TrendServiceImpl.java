package org.example.xbotai.service.impl;

import org.example.xbotai.service.TrendService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Service
public class TrendServiceImpl implements TrendService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Override
    public String fetchTrends() {
        ResponseEntity<List> response = restTemplate.getForEntity(pythonApiUrl, List.class);
        List<String> trends = response.getBody();

        if (trends != null && !trends.isEmpty()) {
            String topTrend = trends.get(0);
            return topTrend;
        }
        return "No trends available.";
    }
}
