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

package com.orange.clara.cloud.services.sandbox.config;


/**
 * Created by O. Orand on 13/11/2015.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity(debug = false)
@EnableResourceServer
public class SecurityConfiguration extends ResourceServerConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Value("${security.oauth2.admin.scope}")
    private String oauth2AdminScope;

    @Autowired
    SecurityProperties securityProperties;

    @Autowired
    ManagementServerProperties managementServerProperties;

    @Autowired
    private ResourceServerProperties resource;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources.resourceId(this.resource.getResourceId());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String managementContextPath = managementServerProperties.getContextPath();

        // @formatter:off
        if (securityProperties.isRequireSsl()) {
            LOGGER.info("SSL enabled in springboot config, cannot access this app using http");
            http.requiresChannel().anyRequest().requiresSecure();
        }
        if (securityProperties.isEnableCsrf()) {
            LOGGER.info("CSRF enabled in springboot config");
            http.csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
        }
        http
            .authorizeRequests()
                .antMatchers(managementContextPath+"/health",managementContextPath+"/info").access("isAnonymous() or #oauth2.throwOnError(#oauth2.hasScope('"+oauth2AdminScope+"'))")
                .antMatchers(managementContextPath+"/**").access("#oauth2.hasScope('"+oauth2AdminScope+"')")
                .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(securityProperties.getSessions());
        // @formatter:on


    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                        .getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null
                            && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}