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

package com.orange.clara.cloud.services.sandbox.infrastructure;

import com.orange.clara.cloud.services.sandbox.config.CloudfoundryTarget;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.client.v2.spaces.*;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.reactivestreams.Publisher;
import reactor.rx.Streams;

import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by sbortolussi on 02/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudfoundryPrivateSandboxServiceTest {

    @Mock
    CloudFoundryClient cloudFoundryClient;

    @Mock
    CloudfoundryTarget cloudfoundryTarget;

    Organizations cfOrganizations;

    @InjectMocks
    private CloudfoundryPrivateSandboxService privateSandboxService;

    @Test
    public void should_create_a_cloudfoundry_space_as_admin() throws Exception {

        final String orgName = "orange";
        final String userName = "mycuid";
        final String userId = "user-id";
        final URL apiUrl = new URL("http://localhost");
        Organizations cfOrganizations = mock(Organizations.class);
        when(cloudFoundryClient.organizations()).thenReturn(cfOrganizations);
        when(cfOrganizations.list(isA(ListOrganizationsRequest.class))).thenReturn(x());
        Spaces cfSpaces = mock(Spaces.class);
        when(cfSpaces.create(isA(CreateSpaceRequest.class))).thenAnswer(invocation -> getCreateSpaceResponseMock((CreateSpaceRequest) invocation.getArguments()[0]));
        when(cloudFoundryClient.spaces()).thenReturn(cfSpaces);
        when(cloudfoundryTarget.getOrg()).thenReturn(orgName);
        when(cloudfoundryTarget.getApiUrl()).thenReturn(apiUrl);

        SandboxInfo sandboxForUser = privateSandboxService.createSandboxForUser(new UserInfo(userName, userId));

        verify(cfOrganizations).list(any());
        verify(cfOrganizations).associateUserByUsername(any());
        verify(cfSpaces).create(any());
        verifyNoMoreInteractions(cfOrganizations,cfSpaces);
        Assertions.assertThat(sandboxForUser).isEqualTo(new SandboxInfo(orgName, userName, apiUrl));
    }

    private Publisher<ListOrganizationsResponse> x() {
        return Streams.from(Collections.singletonList(getOrgResponseMock("orange")));
    }

    private ListOrganizationsResponse getOrgResponseMock(String orgName) {
        return ListOrganizationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ListOrganizationsResponse.Resource.builder()
                        .metadata(Resource.Metadata.builder()
                                .id("deb3c359-2261-45ba-b34f-ee7487acd71a")
                                .url("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a")
                                .createdAt("2015-07-27T22:43:05Z")
                                .build())
                        .entity(OrganizationEntity.builder()
                                .name(orgName)
                                .billingEnabled(false)
                                .quotaDefinitionId("9b56a1ec-4981-4a1e-9348-0d78eeca842c")
                                .status("active")
                                .quotaDefinitionUrl("/v2/quota_definitions/9b56a1ec-4981-4a1e-9348-0d78eeca842c")
                                .spacesUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/spaces")
                                .domainsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/domains")
                                .privateDomainsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/private_domains")
                                .usersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/users")
                                .managersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/managers")
                                .billingManagersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/billing_managers")
                                .auditorsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/auditors")
                                .applicationEventsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/app_events")
                                .spaceQuotaDefinitionsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/space_quota_definitions")
                                .build())
                        .build())
                .build();
    }


    Publisher<CreateSpaceResponse> getCreateSpaceResponseMock(CreateSpaceRequest createSpaceRequest) {
        return Streams.from(Collections.singletonList(
                CreateSpaceResponse.builder()
                        .metadata(Resource.Metadata.builder()
                                .id("d29dc30c-793c-49a6-97fe-9aff75dcbd12")
                                .url("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12")
                                .createdAt("2015-07-27T22:43:08Z")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name(createSpaceRequest.getName())
                                .organizationId(createSpaceRequest.getOrganizationId())
                                .allowSsh(true)
                                .organizationUrl("/v2/organizations/" + createSpaceRequest.getOrganizationId())
                                .developersUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/developers")
                                .managersUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/managers")
                                .auditorsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/auditors")
                                .applicationsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/apps")
                                .routesUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/routes")
                                .domainsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/domains")
                                .serviceInstancesUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/service_instances")
                                .applicationEventsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/app_events")
                                .eventsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/events")
                                .securityGroupsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/security_groups")
                                .build())
                        .build()));
    }
}