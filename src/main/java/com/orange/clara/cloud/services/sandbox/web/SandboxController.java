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

package com.orange.clara.cloud.services.sandbox.web;

import com.orange.clara.cloud.services.sandbox.domain.PrivateSandboxService;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by sbortolussi on 14/09/2015.
 */
@RestController
@RequestMapping("/v1/sandboxes")
public class SandboxController {

    @Autowired
    PrivateSandboxService privateSandboxService;

    private static Logger LOGGER = LoggerFactory.getLogger(SandboxController.class);

    /**
     * register into cloud controller
     * *
     */
    @RequestMapping(method = RequestMethod.POST)
    public void register(Principal principal) {
        privateSandboxService.create(new SandboxInfo("orange", principal.getName(), "userId"));
    }


}
