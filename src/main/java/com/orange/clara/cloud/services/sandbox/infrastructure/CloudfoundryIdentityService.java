/*
 * Copyright (C) 2015 Orange
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.orange.clara.cloud.services.sandbox.infrastructure;

import com.orange.clara.cloud.services.sandbox.domain.IdentityService;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

/**
 * Created by sbortolussi on 05/10/2015.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CloudfoundryIdentityService implements IdentityService {

    private OAuth2ClientContext oauth2Context;

    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    public CloudfoundryIdentityService(@Qualifier("cloudFoundryClientAsUser") CloudFoundryClient cloudFoundryClient, OAuth2ClientContext oauth2Context) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.oauth2Context = oauth2Context;
    }

    @Override
    public UserInfo getInfo(Principal principal) {
        //TO force user info from ldap loading into CC
        final String username = cloudFoundryClient.getCloudInfo().getUser();
        UserInfo userInfo = new UserInfo(username,getCurrentUserId(username));
        //UserInfo userInfo = new UserInfo(userPrincipal.getName(), getCurrentUserId(userPrincipal.getName()));

        return userInfo;
    }

    private Map<String, Object> getUserInfo(String user) {
//		String userJson = getRestTemplate().getForObject(getUrl("/v2/users/{guid}"), String.class, user);
//		Map<String, Object> userInfo = (Map<String, Object>) JsonUtil.convertJsonToMap(userJson);
//		return userInfo();
        //TODO: remove this temporary hack once the /v2/users/ uri can be accessed by mere mortals
        String userJson = "{}";
        OAuth2AccessToken accessToken = oauth2Context.getAccessToken();
        if (accessToken != null) {
            String tokenString = accessToken.getValue();
            int x = tokenString.indexOf('.');
            int y = tokenString.indexOf('.', x + 1);
            String encodedString = tokenString.substring(x + 1, y);
            try {
                byte[] decodedBytes = new sun.misc.BASE64Decoder().decodeBuffer(encodedString);
                userJson = new String(decodedBytes, 0, decodedBytes.length, "UTF-8");
            } catch (IOException e) {
            }
        }
        return (JsonUtil.convertJsonToMap(userJson));
    }

    private String getCurrentUserId(String username) {
        Map<String, Object> userMap = getUserInfo(username);
        String userId = (String) userMap.get("user_id");
        return userId;
    }
    /*
         private String getCurrentUserId(String username) {
        return String.valueOf(oauth2Context.getAccessToken().getAdditionalInformation().get("user_id"));
    }
     */
}
