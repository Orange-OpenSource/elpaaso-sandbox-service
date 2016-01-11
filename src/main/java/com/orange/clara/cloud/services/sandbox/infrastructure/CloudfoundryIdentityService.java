/**
 * Copyright (C) 2015-2016 Orange
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orange.clara.cloud.services.sandbox.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.clara.cloud.services.sandbox.domain.IdentityService;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
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
    private static Logger LOGGER = LoggerFactory.getLogger(CloudfoundryIdentityService.class);

    private CloudFoundryClient cloudFoundryClient;
    private OAuth2AccessToken oAuth2AccessToken;

    @Autowired
    public CloudfoundryIdentityService(@Qualifier("cloudFoundryClientAsUser")CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Autowired
    public CloudfoundryIdentityService setoAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
        return this;
    }

    @Override
    public UserInfo getInfo(Principal userPrincipal) {
        createUserInCloudfoundry();
        String username = userPrincipal.getName();
        LOGGER.info("User {} has been created on cloudfoundry.",username);
        String userGuid = getUserGuidFromAccessToken(username);
        return new UserInfo(username, userGuid);
    }

    private String getUserGuidFromAccessToken(String username) {
        LOGGER.debug("Decoding JWT for user {}",username);
        Jwt jwt= JwtHelper.decode(oAuth2AccessToken.getValue());
        Map map;
        try {
            ObjectMapper mapper = new ObjectMapper();
            map=mapper.readValue(jwt.getClaims(), Map.class);
        } catch (IOException e) {
            throw  new RuntimeException("Cannot parse jwt token for user " + username,e);
        }
        LOGGER.debug("Getting user_id for user {}",username);
        return (String) map.get("user_id");
    }

    private void createUserInCloudfoundry() {
        cloudFoundryClient.getCloudInfo();
    }
}
