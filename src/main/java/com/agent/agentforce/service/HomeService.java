package com.agent.agentforce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HomeService {
	private final String MY_DOMAIN_URL = "https://orgfarm-91676bf643-dev-ed.develop.my.salesforce.com";
	private final String TOKEN_URL     = MY_DOMAIN_URL + "/services/oauth2/token";

    private final String CLIENT_ID     = "3MVG9rZjd7MXFdLj2Xbmyd6jueL3l4Omv5IIdTG2V5XRDaaYMoVIQWvLPn5BSIOzVlsZQIZBfDGxz8sVSBJ._";
    private final String CLIENT_SECRET = "E5FC0CBB4BF1EE0B6C87824E35ECB83BBE7FEFD919C1AE754A52C511F61BF268";
    private final String USERNAME      = "jhchoi398@agentforce.com";
    private final String PASSWORD      = "qlalfqjsgh@8895hrelp0LLIXdPwSBfxGfi5icV4";
    
    private final String AGENT_ID      = "0XxgK000000J5SDSA0";
    private final String AGENT_API_URL = "https://api.salesforce.com/einstein/ai-agent/v1/agents/" + AGENT_ID + "/sessions";
    
    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type",    "client_credentials");
        form.add("client_id",     CLIENT_ID);
        form.add("client_secret", CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            } else {
                throw new RuntimeException("Access token not found in response");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from Salesforce", e);
        }
    }
	
	public String sendMessage(String message) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(getAccessToken());

	    Map<String, Object> body = new HashMap<>();
	    
	    body.put("externalSessionKey", "abc");
	    
	    Map<String, Object> instanceConfig = new HashMap<>();
	    instanceConfig.put("endpoint", MY_DOMAIN_URL);
	    body.put("instanceConfig", instanceConfig);
	    
	    Map<String, Object> streamingCapabilities = new HashMap<>();
	    ArrayList<String> chunkTypes = new ArrayList<String>();
	    chunkTypes.add("Text");
	    streamingCapabilities.put("chunkTypes", chunkTypes);
	    
	    body.put("streamingCapabilities", streamingCapabilities);
	    body.put("bypassUser", true);

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
	    
	    try {
	    	ResponseEntity<Map> response = restTemplate.postForEntity(AGENT_API_URL, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("messages")) {
            	List<Map<String, Object>> messages = (ArrayList<Map<String, Object>>) responseBody.get("messages");
            	
                return (String) messages.get(0).get("message");
            } else {
                throw new RuntimeException("Error");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error", e);
        }
	}
}
