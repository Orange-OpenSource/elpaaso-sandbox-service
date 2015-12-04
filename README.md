# elpaaso-sandbox-service

[![Build Status](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service)
[![Swagger UI](https://img.shields.io/badge/Swagger%20UI-explore-green.svg)](https://orange-opensource.github.io/elpaaso-sandbox-service/)
[![Validator](http://online.swagger.io/validator/?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)](https://online.swagger.io/validator/debug?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)
[![Apache Version 2 Licence](http://img.shields.io/:license-Apache%20v2-blue.svg)](LICENSE)
[![Bintray](https://www.bintray.com/docs/images/bintray_badge_color.png)](https://bintray.com/elpaaso/maven/elpaaso-sandbox-service/view?source=watch)
[![JCenter](https://img.shields.io/badge/JCenter-available-blue.svg)](https://bintray.com/bintray/jcenter?filterByPkgName=elpaaso-sandbox-service)
[![Join the chat at https://gitter.im/Orange-OpenSource/elpaaso](https://img.shields.io/badge/gitter-join%20chat%20→-brightgreen.svg)](https://gitter.im/Orange-OpenSource/elpaaso?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


## Sandbox service stories

### stories:

* MVP: as a paas-user, once logged in, I can use a private CF space: API endpoint, org and space details displayed.
  * given: a user with a account into CF UAA
  * when: the user browses to the sandbox service through sandbox-ui
  * then: the user is prompted in the UAA to provide his consent from sandbox-service to act on his behalf (cloudcontroler.read, openid scopes)
  * and: the user is displayed with CF a CLI command to connect to a private space
     

Pending stories:
* paas-ops email notification:
   * given: the paas-ops has configured an email address $email in the sandbox-service config and a cf instance name $cf_instance
   * when: the paas-user first access the sandbox to access her private space
   * then: a notification email is sent to the configured adress which includes: 
      * in subject: [sandbox $cf_instance]: private space created for $user_name      
      * in the email body: user name, user email, CC API endpoint, org & space name
* private space templating
   * given: the paas-ops has configured:
      * a security group with name $sec-group-name
      * a space quota with name $space-quota-name 
   * when: the paas-user first access the sandbox to access her private space
   * then: a space is allocated and bound to the specific security group $sec-group-name and the space quota $space-quota-name


## GUI
  * Simple GUI available: [Sandbox-UI](https://github.com/Orange-OpenSource/elpaaso-sandbox-ui)

## Tech specs of the sandbox service

### Overview

![Sandbox service](http://g.gravizo.com/g?
@startuml
User -> SandboxService: GET sandboxes/me;
User <-- SandboxService: 302 location=uaa/login;
User -> uaa: ....;
User <-- uaa: 302: GET: SandboxService/sandboxes/me?code=rezrze;
User -> SandboxService : GET /sandboxes/me?code=rezrze;
SandboxService -> uaa : GET /oauth/token?code=rezrze;
SandboxService <-- uaa : user_token;
SandboxService -> CC_api: login (as user);
SandboxService -> CC_api: getCloudInfo (as user);
SandboxService -> CC_api: getOrg (as Admin);
SandboxService -> CC_api: AssociateUserWithOrganization (as Admin);
SandboxService -> CC_api: CreateSpace (as Admin);
SandboxService -> CC_api: AssignManagerRole("org_name","space_name","user_id") (as Admin);
SandboxService -> CC_api: AssignAuditorRole("org_name","space_name","user_id") (as Admin);
SandboxService -> CC_api: AssignDeveloperRole("org_name","space_name","user_id") (as Admin);
User <-- SandboxService: "org_name","space_name","cc_api_url";
@enduml)



* sandbox oauth roles 
  *  OAuth resource server (/sandbox)
    *  scopes: NONE specific yet

  *  OAuth client (CF resources)
    *  scope: scim... cloudcountrol...


* sandboxes/me GET (Bearer: AccessToken)
    *  301 sandbox/guid

    UAA token https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-Tokens.md#getting-started
    "user id" guid http://apidocs.cloudfoundry.org/219/users/get_user_summary.html ?
    + preserves user identity
    username: LDAP DN = "orange CUID" 



Inspirations for API REST
  *  https://github.com/cloudfoundry/cc-api-v3-style-guide/#post
  *  SCIM ? http://www.simplecloud.info/

**Note:** If wanna use angularjs (or javascript in general) don't forget to manage CORS on UAA and API

# Build
To be able to build this project, you have to update your maven settings. You can use the one provided [here](/settings.xml)

## Running Tests

### Unit Tests
   * `mvn clean install`
### Integration Tests
   * `mvn clean install -PrunITs`

# Release
We use [OJO](https://oss.jfrog.org). Thus we can use build promotion to release our component on [Jcenter](https://jcenter.bintray.com).

Full release process is detailed in /src/bin/release

# Download
According to your usage, you can use Maven [Jcenter](https://jcenter.bintray.com) or [Bintray](https://bintray.com/elpaaso/maven/elpaaso-sandbox-service/_latestVersion)

# Install

Please use cf-manifest-reference.yml as template for your CF CLI manifest file.

```
$ mvn package
$ cf push sanbox-ui -p target/elpaaso-service-1.0-SNAPSHOT.jar -m manifest.yml
```

Or inherit cf-manifest-reference.yml and customize

```
`---
inherrit: cf-manifest-reference.yml
applications:
- name: my-sandbox-ui
  domains:
   - cf.rocks.org
   - cf.rocks.com
  env:
    CLOUDFOUNDRY_API_URL: https://my-cloud-foundry.org
    CLOUDFOUNDRY_CREDENTIALS_USER_ID: my-sandbox-admin
    CLOUDFOUNDRY_CREDENTIALS_PASSWORD: <my_sandbox_admin_password>
    SECURITY_OAUTH2_RESOURCE_JWT_KEY_VALUE: |
                -----BEGIN PUBLIC KEY-----
                ZZZZZIIIIIIGGGGGGGERSFRRRRRRRRRRRRRRRRRRTTTTTTTTTTTT2kOrV1r000Hj
                2OrOv/HmuMQMDd0tvUNivz+QWA0SaDEhOmj9T7y0000000fg8f/no00rDeBk/ir+
                3UwpLlw7+AZERTY4FTfp88888888888888888888889999977r2zb1Gkkij0Kd03
                I2YTREZA6W96CA/u/RTHOTPB
                -----END PUBLIC KEY-----
```

## Getting UAA public key to validate JWT signature
UAA public key is used to decode JWT Token signature.
The easiest way to get this key, is to ask UAA!
From [UUA documentation](https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#get-the-token-signing-key-get-token_key),
it is possible to identify the endpoint **/token_key**

```
curl https://uaa.<your_domains>/token_key

{
  "alg": "SHA256withRSA",
  "value": "-----BEGIN PUBLIC KEY-----\nZZZZZIIIIIIGGGGGGGERSFRRRRRRRRRRRRRRRRRRTTTTTTTTTTTT2kOrV1r000Hj\n2OrOv/HmuMQMDd0tvUNivz+QWA0SaDEhOmj9T7y0000000fg8f/no00rDeBk/ir+\n3UwpLlw7+AZERTY4FTfp88888888888888888888889999977r2zb1Gkkij0Kd03\nI2YTREZA6W96CA/u/RTHOTPB\n-----END PUBLIC KEY-----\n",
  "kty": "RSA",
  "use": "sig",
  "n": "AL5NZWqsdffWl789798751RRTgtytrhfsssdfjjhjuk9Q2K/P5BYfghf799yhfhPvJsPLqChJfrhT+f0xisN4GTsKfghfghuXDv5bMp71T456546987bdfh9eGGTPLUFVqsdfrGlUbvuvfghUaSSKM4p0fcjY4oGNC3pb3oIff79",
  "e": "AQAB"

}
```
**Warning, the value contains multiple \n (remove it, if required)**






# Running
## Pre-requisites
 * Cloudfoundry use, used to create space and to assign role requires at least Org Admin privilege,
 * Org should exist. Creates a new one if required:
    * `cf org-users sandboxes`
 * A default space should exist,

