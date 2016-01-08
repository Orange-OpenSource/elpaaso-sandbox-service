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
import com.orange.clara.cloud.services.sandbox.domain.PrivateSandboxService;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.Mono;
import reactor.rx.Streams;

/**
 * Created by sbortolussi on 02/10/2015.
 */
@Component
public class CloudfoundryPrivateSandboxService implements PrivateSandboxService {
    private static Logger LOGGER = LoggerFactory.getLogger(CloudfoundryPrivateSandboxService.class);

    private CloudfoundryTarget cloudfoundryTarget;

    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    public CloudfoundryPrivateSandboxService(CloudfoundryTarget cloudfoundryTarget, @Qualifier("cloudFoundryClientAsAdmin") CloudFoundryClient cloudFoundryClient) {
        this.cloudfoundryTarget = cloudfoundryTarget;
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public SandboxInfo createSandboxForUser(UserInfo userInfo) {
        SandboxInfo sandboxInfo = new SandboxInfo(cloudfoundryTarget.getOrg(), userInfo.getUsername(), cloudfoundryTarget.getApiUrl());
        LOGGER.info("Associates user {} with org {}", userInfo.getUsername(), sandboxInfo.getOrgName());
        String orgGuid = getTargetedOrganizationGuid();


        AssociateOrganizationUserByUsernameRequest associateOrganizationUserByUsernameRequest = AssociateOrganizationUserByUsernameRequest.builder().id(orgGuid).username(userInfo.getUsername()).build();
        cloudFoundryClient.organizations().associateUserByUsername(associateOrganizationUserByUsernameRequest);
        LOGGER.info("Creates space {} for user {} (id: {}), also adds auditor, manager and developer rights", new Object[]{sandboxInfo.getSpaceName(), userInfo.getUsername(),userInfo.getUserId()});

        CreateSpaceRequest sandboxUserSpaceCreationRequest = CreateSpaceRequest.builder()
                .organizationId(orgGuid)
                .name(sandboxInfo.getSpaceName())
                .managerId(userInfo.getUserId())
                .auditorId(userInfo.getUserId())
                .developerId(userInfo.getUserId())
                .build();
        Mono<CreateSpaceResponse> sandboxUserSpaceCreationResponse = cloudFoundryClient.spaces().create(sandboxUserSpaceCreationRequest);
        Mono.from(sandboxUserSpaceCreationResponse).get().getMetadata().getId();
        return sandboxInfo;
    }

    private String getTargetedOrganizationGuid() {
        ListOrganizationsRequest organizationsRequest = ListOrganizationsRequest.builder().name(cloudfoundryTarget.getOrg()).build();
        Mono<ListOrganizationsResponse> sandboxOrgPublisher = cloudFoundryClient.organizations().list(organizationsRequest);
        ListOrganizationsResponse organizationsResponse = Mono.from(sandboxOrgPublisher).get();
        OrganizationResource orgResource = organizationsResponse.getResources().stream().findFirst().get();
        return orgResource.getMetadata().getId();
    }

}
