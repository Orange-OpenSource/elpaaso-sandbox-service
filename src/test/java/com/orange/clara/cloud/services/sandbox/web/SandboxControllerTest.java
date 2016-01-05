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

package com.orange.clara.cloud.services.sandbox.web;

import com.orange.clara.cloud.services.sandbox.application.SandboxService;
import com.orange.clara.cloud.services.sandbox.domain.SandboxInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.security.Principal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by O. Orand on 05/01/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SandboxControllerTest {

    private MockMvc mockMvc;

    @Mock
    SandboxService sandboxService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(new SandboxController(sandboxService))
                .build();
    }

    @Test
    public void should_register() throws Exception {
        when(sandboxService.createSandbox(any())).thenReturn(new SandboxInfo("myOrg", "mySpace", new URL("https://api.cloudfoundry.org")));

        Principal user = () -> "myUser";
        mockMvc.perform(get("/v1/sandboxes/me").principal(user))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{orgName:\"myOrg\",spaceName:\"mySpace\",apiUrl:\"https://api.cloudfoundry.org\"}"))
        ;

        verify(sandboxService).createSandbox(user);
    }
}