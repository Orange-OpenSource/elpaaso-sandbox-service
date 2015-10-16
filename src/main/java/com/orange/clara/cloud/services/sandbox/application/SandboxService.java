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

package com.orange.clara.cloud.services.sandbox.application;

import com.orange.clara.cloud.services.sandbox.domain.IdentityService;
import com.orange.clara.cloud.services.sandbox.domain.PrivateSandboxService;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import com.orange.clara.cloud.services.sandbox.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Created by sbortolussi on 05/10/2015.
 */
@Service
public class SandboxService {
    private static Logger LOGGER = LoggerFactory.getLogger(SandboxService.class);

    private IdentityService identityService;

    private PrivateSandboxService privateSandboxService;

    @Autowired
    public SandboxService(IdentityService myIdentityService, PrivateSandboxService myPrivateSandboxService) {
        this.identityService=myIdentityService;
        this.privateSandboxService=myPrivateSandboxService;
    }

    public SandboxInfo createSandbox(Principal principal){
        LOGGER.info("Create sandbox for {}",principal.getName());
        UserInfo userInfo = identityService.getInfo(principal);
        return privateSandboxService.createSandboxForUser(userInfo);
    }

}
