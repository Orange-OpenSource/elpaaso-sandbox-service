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
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * Created by sbortolussi on 05/10/2015.
 */
public class CloudfoundryIdentityService implements IdentityService {

    @Autowired
    private CloudfoundryTarget cloudfoundryTarget;

    public CloudfoundryIdentityService(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    private CloudFoundryClient cloudFoundryClient = null;

    private OAuth2Authentication auth2Authentication;

    private CloudFoundryClient getCloudFoundryClient() {
        if (this.cloudFoundryClient == null) {
            auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth2Authentication.getDetails();
            DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(details.getTokenValue());
            CloudCredentials credentials = new CloudCredentials(defaultOAuth2AccessToken, false);
            this.cloudFoundryClient = new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(), cloudfoundryTarget.isTrustSelfSignedCerts());
        }
        return this.cloudFoundryClient;
    }

    @Override
    public String getUserId() {
        return getCloudFoundryClient().getCloudInfo().getUser();
    }
}
