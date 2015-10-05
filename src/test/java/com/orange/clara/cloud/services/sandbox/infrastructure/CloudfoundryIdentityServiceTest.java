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
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by sbortolussi on 05/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudfoundryIdentityServiceTest {

    @Mock
    CloudFoundryClient cloudFoundryClient;

    @Mock
    CloudInfo cloudInfo;

    @InjectMocks
    private CloudfoundryIdentityService identityService;

    @Test
    public void should_get_user_id() throws Exception {

        Mockito.when(cloudFoundryClient.getCloudInfo()).thenReturn(cloudInfo);

        identityService.getUserId();

        Mockito.verify(cloudFoundryClient.getCloudInfo().getUser());

    }
}