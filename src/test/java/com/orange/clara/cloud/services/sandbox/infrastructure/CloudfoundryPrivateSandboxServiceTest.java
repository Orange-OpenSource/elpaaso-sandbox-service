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

import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Created by sbortolussi on 02/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudfoundryPrivateSandboxServiceTest {

    @Mock
    CloudFoundryClient cloudFoundryClient;

    @Mock
    CloudfoundryTarget cloudfoundryTarget;

    @InjectMocks
    private CloudfoundryPrivateSandboxService privateSandboxService;

    @Test
    public void should_create_a_cloudfoundry_space_as_admin() throws Exception {
        final String orgName = "orange";
        final String userName = "mycuid";
        final String userId = "user-id";
        when(cloudfoundryTarget.getOrg()).thenReturn(orgName);

        SandboxInfo sandboxForUser = privateSandboxService.createSandboxForUser(new UserInfo(userName, userId));
        Mockito.verify(cloudFoundryClient).createSpace(userName);
        Mockito.verify(cloudFoundryClient).associateAuditorWithSpace(orgName, userName,userId);
        Mockito.verify(cloudFoundryClient).associateDeveloperWithSpace(orgName, userName,userId);
        Mockito.verify(cloudFoundryClient).associateAuditorWithSpace(orgName, userName,userId);

        Assertions.assertThat(sandboxForUser).isEqualTo(new SandboxInfo(orgName,userName));


    }

}