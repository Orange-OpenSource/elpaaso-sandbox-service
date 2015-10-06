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

import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.security.Principal;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by sbortolussi on 05/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudfoundryIdentityServiceTest {

    //access token containing "user_id: uaa-id-314"
    public static final String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoidWFhLWlkLTMxNCIsImVtYWlsIjoiZW1haWwtMjA3QHNvbWVkb21haW4uY29tIiwic2NvcGUiOlsiY2xvdWRfY29udHJvbGxlci5hZG1pbiJdLCJhdWQiOlsiY2xvdWRfY29udHJvbGxlciJdLCJleHAiOjE0NDQyNjA2NTF9.UKdlvpim-D0p5bzKPeAHdpWOHJthtrfQpQAZzDtBO7o";
    @Mock
    CloudFoundryClient cloudFoundryClient;

    @Mock
    OAuth2ClientContext oauth2Context;

    @InjectMocks
    private CloudfoundryIdentityService identityService;

    @Test
    public void should_get_user_id() throws Exception {
        CloudInfo cloudInfo = new CloudInfo(Collections.unmodifiableMap(Stream.of(new AbstractMap.SimpleEntry<>("user", "myUsername")).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()))));
        Mockito.when(cloudFoundryClient.getCloudInfo()).thenReturn(cloudInfo);
        Mockito.when(oauth2Context.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken(ACCESS_TOKEN));

        Principal user = new Principal() {
            @Override
            public String getName() {
                return "myUsername";
            }
        };

        UserInfo userInfo = identityService.getInfo(user);
        Assertions.assertThat(userInfo).isEqualTo(new UserInfo("myUsername", "uaa-id-314"));
    }


}