/**
 * Copyright (C) 2015-2016 Orange
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

package com.orange.clara.cloud.services.sandbox.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URL;

/**
 * Created by sbortolussi on 02/10/2015.
 */
public class SandboxInfo {

    private String orgName;
    private String spaceName;

    private URL apiUrl;

    public SandboxInfo(String orgName, String spaceName, URL apiUrl) {
        this.orgName = orgName;
        this.spaceName = spaceName;
        this.apiUrl = apiUrl;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public URL getApiUrl() {
        return apiUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SandboxInfo that = (SandboxInfo) o;

        return new EqualsBuilder()
                .append(orgName, that.orgName)
                .append(spaceName, that.spaceName)
                .append(apiUrl, that.apiUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(orgName)
                .append(spaceName)
                .append(apiUrl)
                .toHashCode();
    }
}
