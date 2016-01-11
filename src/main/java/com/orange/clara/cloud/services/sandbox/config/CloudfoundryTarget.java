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

package com.orange.clara.cloud.services.sandbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Created by sbortolussi on 05/10/2015.
 */
@Component
@ConfigurationProperties("cloudfoundry")
public class CloudfoundryTarget {

    private URL apiUrl;
    private ProxyInfo proxyInfo;
    private Credentials credentials;
    private boolean trustSelfSignedCerts;
    private String org;
    private String defaultSpace;

    public String getDefaultSpace() {
        return defaultSpace;
    }

    public CloudfoundryTarget setDefaultSpace(String defaultSpace) {
        this.defaultSpace = defaultSpace;
        return this;
    }

    private String space;

    public URL getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(URL apiUrl) {
        this.apiUrl = apiUrl;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public boolean isTrustSelfSignedCerts() {
        return trustSelfSignedCerts;
    }

    public void setTrustSelfSignedCerts(boolean trustSelfSignedCerts) {
        this.trustSelfSignedCerts = trustSelfSignedCerts;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }
}
