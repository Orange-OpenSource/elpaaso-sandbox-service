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

package com.orange.clara.cloud.services.sandbox;

import com.orange.clara.cloud.services.sandbox.infrastructure.CloudfoundryTarget;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.security.oauth2.client.OAuth2LoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.csrf.CsrfFilter;

@SpringBootApplication(exclude = {OAuth2LoadBalancerClientAutoConfiguration.class})
@EnableOAuth2Sso
//@EnableResourceServer
@EnableWebSecurity(debug = true)
public class ElpaasoIdentityApplication  extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(ElpaasoIdentityApplication.class, args);
    }

    @Autowired
    private CloudfoundryTarget cloudfoundryTarget;


    @Bean(name = "cloudFoundryClientAsUser")
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CloudFoundryClient getCloudFoundryClientAsUser() {
        OAuth2Authentication auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth2Authentication.getDetails();
        DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(details.getTokenValue());
        CloudCredentials credentials = new CloudCredentials(defaultOAuth2AccessToken, false);
        return new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(), cloudfoundryTarget.isTrustSelfSignedCerts());
    }


    @Bean(name = "cloudFoundryClientAsAdmin")
    public CloudFoundryClient getCloudFoundryClientAsAdmin() {
        CloudCredentials credentials = new CloudCredentials(cloudfoundryTarget.getCredentials().getUserId(), cloudfoundryTarget.getCredentials().getPassword());
        return new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(),cloudfoundryTarget.getOrg(),cloudfoundryTarget.getSpace(), cloudfoundryTarget.isTrustSelfSignedCerts());
    }



    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.logout().and().antMatcher("/**").authorizeRequests()
                .antMatchers("/index.html", "/home.html", "/", "/login").permitAll()
                .anyRequest().authenticated().and().csrf().disable();
    }

}
