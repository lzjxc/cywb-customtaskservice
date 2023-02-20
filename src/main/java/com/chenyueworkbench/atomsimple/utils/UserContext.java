package com.chenyueworkbench.atomsimple.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN     = "Authorization";
    public static final String USER_ID        = "tmx-user-id";
    public static final String INSIGHTPROJECT_ID         = "tmx-atomsimple-id";
    public static final String STARTER_USER_ID         = "starter-user-id";

    private static final ThreadLocal<String> correlationId= new ThreadLocal<String>();
    private static final ThreadLocal<String> authToken= new ThreadLocal<String>();
    private static final ThreadLocal<String> userId = new ThreadLocal<String>();
    private static final ThreadLocal<String> atomsimpleId = new ThreadLocal<String>();
    private static final ThreadLocal<String> starterUserId = new ThreadLocal<String>();


    public static String getCorrelationId() { return correlationId.get(); }
    public static void setCorrelationId(String cid) {correlationId.set(cid);}
 
    public static String getAuthToken() { return authToken.get(); }
    public static void setAuthToken(String aToken) {authToken.set(aToken);}

    public static String getUserId() { return userId.get(); }
    public static void setUserId(String aUser) {userId.set(aUser);}

    public static String getAtomsimpleId() { return atomsimpleId.get(); }
    public static void setAtomsimpleId(String aAtomsimple) {atomsimpleId.set(aAtomsimple);}

    public static String getStarterUserId() { return starterUserId.get(); }
    public static void setStarterUserId(String aStarterUserId) {starterUserId.set(aStarterUserId);}

    public static HttpHeaders getHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CORRELATION_ID, getCorrelationId());

        return httpHeaders;
    }

}
