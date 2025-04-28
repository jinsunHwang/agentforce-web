package com.agent.agentforce.service;

import java.util.*;

import com.agent.agentforce.mapper.AccessTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HomeService {
    @Autowired
    private AccessTokenMapper accessTokenMapper;

	private final String MY_DOMAIN_URL = "https://orgfarm-91676bf643-dev-ed.develop.my.salesforce.com";
	private final String TOKEN_URL     = MY_DOMAIN_URL + "/services/oauth2/token";

    private final String CLIENT_ID     = "3MVG9rZjd7MXFdLj2Xbmyd6jueL3l4Omv5IIdTG2V5XRDaaYMoVIQWvLPn5BSIOzVlsZQIZBfDGxz8sVSBJ._";
    private final String CLIENT_SECRET = "E5FC0CBB4BF1EE0B6C87824E35ECB83BBE7FEFD919C1AE754A52C511F61BF268";
    private final String USERNAME      = "jhchoi398@agentforce.com";
    private final String PASSWORD      = "qlalfqjsgh@8895hrelp0LLIXdPwSBfxGfi5icV4";
    
    private final String AGENT_ID      = "0XxgK000000JG4HSAW";
    private final String AGENT_API_URL = "https://api.salesforce.com/einstein/ai-agent/v1/agents/" + AGENT_ID + "/sessions";
    private final String AGENT_SESSION_URL = "https://api.salesforce.com/einstein/ai-agent/v1/sessions/";
    
    static String agentSessionId = "";

    public String getAccessToken() {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // POST 요청에 필요한 파라미터 설정
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", CLIENT_ID);
        form.add("client_secret", CLIENT_SECRET);

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            // POST 요청으로 Access Token 받기
            ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, String.class);
            String responseBody = response.getBody();

            if (responseBody != null) {
                String accessToken = parseAccessToken(responseBody);
                String issuedAtStr = parseIssuedAt(responseBody);
                Date issuedAt = new Date(Long.parseLong(issuedAtStr)); // issued_at 값 (밀리초 단위)

                // 만료 시간을 계산 (1시간 후)
                long expiresAtMillis = issuedAt.getTime() + 3600000; // 3600000 ms = 1 hour
                Date expiresAt = new Date(expiresAtMillis);

                // Map을 사용하여 액세스 토큰 정보를 담기
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("access_token", accessToken);
                tokenData.put("token_type", "Bearer");
                tokenData.put("expires_at", expiresAt);
                tokenData.put("issued_at", issuedAt);

                // DB에 액세스 토큰 저장
                accessTokenMapper.insertAccessToken(tokenData);

                return accessToken;
            } else {
                throw new RuntimeException("Failed to get access token. Response body is null.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from Salesforce", e);
        }
    }

    // response에서 access_token 값 파싱
    private String parseAccessToken(String responseBody) {
        String tokenPrefix = "\"access_token\":\"";
        String tokenSuffix = "\"";
        return parseJsonValue(responseBody, tokenPrefix, tokenSuffix);
    }

    // response에서 issued_at 값 파싱
    private String parseIssuedAt(String responseBody) {
        String issuedAtPrefix = "\"issued_at\":";
        String issuedAtSuffix = ",";

        String issuedAt = parseJsonValue(responseBody, issuedAtPrefix, issuedAtSuffix);

        // 숫자 앞뒤의 불필요한 따옴표 제거
        if (issuedAt.startsWith("\"") && issuedAt.endsWith("\"")) {
            issuedAt = issuedAt.substring(1, issuedAt.length() - 1);
        }

        return issuedAt;
    }

    // JSON 값 파싱 (간단한 예시로 지정)
    private String parseJsonValue(String responseBody, String prefix, String suffix) {
        int start = responseBody.indexOf(prefix) + prefix.length();
        int end = responseBody.indexOf(suffix, start);
        return responseBody.substring(start, end);
    }

    // 만료된 액세스 토큰을 재발급 받기
    public String getValidAccessToken() {
        // DB에서 가장 최신 액세스 토큰 조회
        Map<String, Object> tokenData = accessTokenMapper.selectLatestAccessToken();
        if (tokenData != null && new Date().before((Date) tokenData.get("expires_at"))) {
            return (String) tokenData.get("access_token"); // 유효한 토큰 반환
        } else {
            return getAccessToken(); // 토큰이 만료되었으면 새로 발급받기
        }
    }
	
	public String agentInit() {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(getValidAccessToken());

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
            	
            	if(agentSessionId.isEmpty()) {
            		agentSessionId = (String) responseBody.get("sessionId");
            	}
            	
                return (String) messages.get(0).get("message");
            } else {
                throw new RuntimeException("Error");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error", e);
        }
	}
	
	public String sendMessage(String message) {
		RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(getValidAccessToken());
	    
	    Map<String, Object> body = new HashMap<>();
	    
	    Map<String, Object> messageMap = new HashMap<>();
	    messageMap.put("sequenceId", 1);
	    messageMap.put("type", "Text");
	    messageMap.put("text", message);
	    
	    body.put("message", messageMap);

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
	    
	    try {
	    	ResponseEntity<Map> response = restTemplate.postForEntity(AGENT_SESSION_URL + agentSessionId + "/messages", request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            System.out.println(responseBody);

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
