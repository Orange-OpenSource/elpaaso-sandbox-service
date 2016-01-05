/**
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
 */

package com.orange.clara.cloud.services.sandbox.exploration;

import com.orange.clara.cloud.services.sandbox.config.CloudfoundryTarget;
import lombok.extern.slf4j.Slf4j;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import reactor.rx.Promise;
import reactor.rx.Streams;
import reactor.rx.broadcast.Broadcaster;
import reactor.rx.subscriber.Control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by O. Orand on 29/12/2015.
 */
@Slf4j
@Configuration
@EnableAutoConfiguration
@ComponentScan("com.orange.clara.cloud.services.sandbox.config")
public class CfClientV2IT {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(CfClientV2IT.class).web(false).profiles("orange").run(args)
                .getBean(Runner.class).run();
    }

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(CloudfoundryTarget cloudfoundryTarget) {

        return SpringCloudFoundryClient.builder()
                .host(cloudfoundryTarget.getApiUrl().getHost())
                .username(cloudfoundryTarget.getCredentials().getUserId())
                .password(cloudfoundryTarget.getCredentials().getPassword())
                .skipSslValidation(cloudfoundryTarget.isTrustSelfSignedCerts())
                .build();
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


    @Component
    @Slf4j
    private static final class Runner {


        private final CloudFoundryClient cloudFoundryClient;

        @Autowired
        private Runner(CloudFoundryClient cloudFoundryClient) {
            this.cloudFoundryClient = cloudFoundryClient;
        }

        private void run() throws IOException, InterruptedException {

            Broadcaster<String> sink = Broadcaster.create();

            sink.map(String::toUpperCase)
                    .consume(s -> System.out.printf("s=%s%n", s));

            sink.onNext("HelLLLLLLLLo World!");

            Map<String, String> organizations = new HashMap<>();

            ListOrganizationsRequest request = ListOrganizationsRequest.builder().build();
            Publisher<ListOrganizationsResponse> publisher = this.cloudFoundryClient.organizations().list(request);

            ListOrganizationsResponse organizationsResponse = Streams.wrap(publisher).next().get();
            organizationsResponse.getResources().stream().forEachOrdered(resource -> {
                log.info("Stream - Org: {} - Id: {}", resource.getEntity().getName(), resource.getMetadata().getId());
            });


            for (ListOrganizationsResponse.Resource orgResource : organizationsResponse.getResources()) {
                log.info("Foreach - Org: {} - Id: {}", orgResource.getEntity().getName(), orgResource.getMetadata().getId());
            }

            log.info("##################################################");

            final String[] orgId = new String[1];
            Paginated.requestResources(page -> {
                ListOrganizationsRequest sandboxOrgRequest = ListOrganizationsRequest.builder().page(page).name("sandboxes").build();
                return this.cloudFoundryClient.organizations().list(sandboxOrgRequest);
            })
                    .consume(r -> {
                        String key = r.getMetadata().getId();
                        String value = r.getEntity().getName();
                        log.info("Org: {} - Id: {}", value, key);
                        organizations.put(key, value);
                    });

            log.info("{} Organizations Found", organizations.size());
        }

    }


}
