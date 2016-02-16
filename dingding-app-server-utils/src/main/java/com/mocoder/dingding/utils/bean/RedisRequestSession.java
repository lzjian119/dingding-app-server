package com.mocoder.dingding.utils.bean;

import com.mocoder.dingding.constants.RedisKeyConstant;
import com.mocoder.dingding.utils.bean.TypeRef;
import com.mocoder.dingding.utils.spring.SpringContextHolder;
import com.mocoder.dingding.utils.web.RedisUtil;
import com.mocoder.dingding.vo.CommonRequest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yangshuai on 16/1/31.
 */
public class RedisRequestSession {
    private String sessionId;
    private Map<String,Object> attributes = new ConcurrentHashMap<String,Object>();
    private long expireDelay;

    /**
     * 创建一个session
     * @param sessionId
     * @param minutes n秒后过期
     */
    public RedisRequestSession(String sessionId,long minutes) {
        this.sessionId = sessionId;
        expireDelay = minutes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, Object> getAttributes() {
        return RedisUtil.hGetAllObj(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX, new TypeRef<Object>(),expireDelay*60);
    }

    public <T> T getAttribute(String key,Class<T> tClass){
        return RedisUtil.hGetObj(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX + sessionId,key, new TypeRef<T>(),expireDelay*60);
    }

    public void setAttribute(String key,Object obj){
        RedisUtil.hSetObj(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX+sessionId,key, obj,expireDelay*60);
    }

    public void removeAttribute(String key){
        RedisUtil.hDel(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX+sessionId,key);
    }

    public void saveUserSessionId(CommonRequest request,String userId){
        StringBuilder stringBuilder = new StringBuilder(request.getDeviceid()).append('_').append(request.getAppversion());
        RedisUtil.hSetString(RedisKeyConstant.USER_REQUEST_SESSION_INFO_KEY_PREFIX+userId,stringBuilder.toString(),sessionId,null);
    }

    public void destroy(){
        RedisUtil.del(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX+sessionId);
    }

    public void cleanUserAllDeviceSession(String userId){
        Map<String,String> sessionIdMap = RedisUtil.hGetAllString(RedisKeyConstant.USER_REQUEST_SESSION_INFO_KEY_PREFIX + userId, null);
        for(String value:sessionIdMap.values()){
            RedisUtil.del(RedisKeyConstant.REQUEST_SESSION_KEY_PREFIX+value);
        }
    }




}
