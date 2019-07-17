package com.common;

import android.widget.ExpandableListView;

abstract public class OAuthManager {

    protected String tokenURL = "";
    protected String oAuthURL = "";
    protected String redirectUri = "";
    protected String oAuthClientId = "";
    protected String oAuthClientSecret = "";
    protected String refreshToken = "";
    protected String accessToken = "";
    protected long tokenExpires = 0L;

    public String getAccessToken(){
        return accessToken;
    }

    public OAuthManager getToken() throws Exception {
        if(System.currentTimeMillis() > tokenExpires) {
            getTokenProcess();
        }
        return this;
    }

    protected abstract void getTokenProcess() throws Exception;

}
