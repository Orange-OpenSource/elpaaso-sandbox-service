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

package com.orange.clara.cloud.services.sandbox.domain;

/**
 * Created by sbortolussi on 02/10/2015.
 */
public class SandboxInfo {

    private String orgName;
    private String spaceName;
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public SandboxInfo(String orgName, String spaceName, String userId) {
        this.orgName = orgName;
        this.spaceName = spaceName;
        this.userId = userId;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getUserId() {
        return userId;
    }
}
