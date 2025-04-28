package com.agent.agentforce.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface AccessTokenMapper {
    void insertAccessToken(Map<String, Object> tokenData);  // 액세스 토큰 저장
    Map<String, Object> selectLatestAccessToken();  // 최신 액세스 토큰 조회
    void updateAccessToken(Map<String, Object> tokenData);  // 액세스 토큰 업데이트
}
