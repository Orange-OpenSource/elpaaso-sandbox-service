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

package com.orange.clara.cloud.services.sandbox;

import com.orange.clara.cloud.services.sandbox.config.CloudfoundryTarget;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.security.oauth2.client.OAuth2LoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@SpringBootApplication(exclude = {OAuth2LoadBalancerClientAutoConfiguration.class})
public class ElpaasoSandboxServiceApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(ElpaasoSandboxServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ElpaasoSandboxServiceApplication.class, args);
    }

    @Autowired
    private CloudfoundryTarget cloudfoundryTarget;

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OAuth2AccessToken getOAuth2AccessToken() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        return new DefaultOAuth2AccessToken(details.getTokenValue());
    }

    @Bean(name = "cloudFoundryClientAsUser")
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CloudFoundryClient getCloudFoundryClientAsUser() {
        LOGGER.debug("Creating new CloudFoundry client using access token");
        CloudCredentials credentials = new CloudCredentials(getOAuth2AccessToken(), false);
        return new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(), cloudfoundryTarget.isTrustSelfSignedCerts());
    }

    @Bean(name = "cloudFoundryClientAsAdmin")
    public CloudFoundryClient getCloudFoundryClientAsAdmin() {
        LOGGER.debug("Creating new ADMIN CloudFoundry client ");
        CloudCredentials credentials = new CloudCredentials(cloudfoundryTarget.getCredentials().getUserId(), cloudfoundryTarget.getCredentials().getPassword());
        return new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(), cloudfoundryTarget.isTrustSelfSignedCerts());
    }
}
