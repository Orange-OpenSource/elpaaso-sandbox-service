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
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
        SandboxInfo sandboxInfo = new SandboxInfo(cloudfoundryTarget.getOrg(),userInfo.getUsername(), cloudfoundryTarget.getApiUrl());
        LOGGER.info("Associates user {} with org {}",userInfo.getUsername(),sandboxInfo.getOrgName());
        cloudFoundryClient.associateUserWithOrg(cloudfoundryTarget.getOrg(), userInfo.getUsername());
        LOGGER.info("Creates space {} for user {}",sandboxInfo.getSpaceName(),userInfo.getUsername());
        cloudFoundryClient.createSpace(sandboxInfo.getSpaceName());
        LOGGER.info("Associates user {} (auditor, manager and developer)with space {}",userInfo.getUsername(),sandboxInfo.getSpaceName());
        cloudFoundryClient.associateAuditorWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), userInfo.getUserId());
        cloudFoundryClient.associateManagerWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), userInfo.getUserId());
        cloudFoundryClient.associateDeveloperWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), userInfo.getUserId());
        return sandboxInfo;
    }
}
