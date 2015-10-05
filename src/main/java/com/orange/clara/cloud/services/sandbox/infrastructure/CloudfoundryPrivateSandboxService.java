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

import com.orange.clara.cloud.services.sandbox.domain.PrivateSandboxService;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sbortolussi on 02/10/2015.
 */
@Component
public class CloudfoundryPrivateSandboxService implements PrivateSandboxService {

    @Autowired
    private CloudfoundryTarget cloudfoundryTarget;

    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    public CloudfoundryPrivateSandboxService(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }


    private CloudFoundryClient getCloudFoundryClient() {
        if (this.cloudFoundryClient == null) {
            CloudCredentials credentials = new CloudCredentials(cloudfoundryTarget.getCredentials().getUserId(), cloudfoundryTarget.getCredentials().getPassword());
            this.cloudFoundryClient = new CloudFoundryClient(credentials, cloudfoundryTarget.getApiUrl(),cloudfoundryTarget.getOrg(),cloudfoundryTarget.getSpace(), cloudfoundryTarget.isTrustSelfSignedCerts());
        }
        return this.cloudFoundryClient;
    }


    @Override
    public void create(SandboxInfo sandboxInfo) {
        getCloudFoundryClient().createSpace(sandboxInfo.getSpaceName());
        getCloudFoundryClient().associateAuditorWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), sandboxInfo.getUserId());
        getCloudFoundryClient().associateManagerWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), sandboxInfo.getUserId());
        getCloudFoundryClient().associateDeveloperWithSpace(sandboxInfo.getOrgName(), sandboxInfo.getSpaceName(), sandboxInfo.getUserId());
    }
}
