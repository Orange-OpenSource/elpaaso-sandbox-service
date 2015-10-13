# elpaaso-sandbox-service

[![Build Status](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service)
[![Swagger UI](https://img.shields.io/badge/Swagger%20UI-explore-green.svg)](https://orange-opensource.github.io/elpaaso-sandbox-service/)
[![Validator](http://online.swagger.io/validator/?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)](https://online.swagger.io/validator/debug?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)
[![Apache Version 2 Licence](http://img.shields.io/:license-Apache%20v2-blue.svg)](LICENSE)
[![Bintray](https://www.bintray.com/docs/images/bintray_badge_color.png)](https://bintray.com/elpaaso/maven/elpaaso-sandbox-service/view?source=watch)
[![JCenter](https://img.shields.io/badge/JCenter-available-blue.svg)](https://bintray.com/bintray/jcenter?filterByPkgName=elpaaso-sandbox-service)


## Sandbox service stories

### stories:
  * Validated 
    * as a paas-user, once logged in, I can use a private CF space (CF CLI credentials displayed in the UI)
    TBC UI 
      * To be drafted with balsamiq https://elpaaso.mybalsamiq.com/projects/opensource/grid

      * Server-side
        * ElPaaso homepage 
        
      * Client-side
        * another standalone UI (e.g. JS): through an OAuth gateway 

 * Suspended until further spec refinements
    * as a paas-user, once registering with pwm, I can go to the sandbox service.
    * as a paas-user, once logged into the sandbox service, I can paste the Cf login CLI commands display in the homepage
    * as a paas-user, once logged into the sandbox service, I am notified by userId of my private space credentials


## GUI
  * Simple GUI available: [Sandbox-UI](https://github.com/Orange-OpenSource/elpaaso-sandbox-ui)

## Tech specs of the sandbox service

### Overview

![Sandbox service](http://g.gravizo.com/g?
@startuml
User -> SandboxService: GET sandboxes/me
User <-- SandboxService: 302 location=uaa/login
User -> uaa: ....
User <-- uaa: 302: GET: SandboxService/sandboxes/me?code=rezrze
User -> SandboxService : GET /sandboxes/me?code=rezrze
SandboxService -> uaa : GET /oauth/token?code=rezrze
SandboxService <-- uaa : user_token
SandboxService -> CC_api: login (as user)
SandboxService -> CC_api: getCloudInfo (as user)
SandboxService -> CC_api: getOrg (as Admin)
SandboxService -> CC_api: AssociateUserWithOrganization (as Admin)
SandboxService -> CC_api: CreateSpace (as Admin)
SandboxService -> CC_api: AssignManagerRole("org_name","space_name","user_id") (as Admin)
SandboxService -> CC_api: AssignAuditorRole("org_name","space_name","user_id") (as Admin)
SandboxService -> CC_api: AssignDeveloperRole("org_name","space_name","user_id") (as Admin)
User <-- SandboxService: "org_name","space_name","cc_api_url"
@enduml
)



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
To be able to build this project, you have to update your maven settings. You can use the one provided [here]()

## Running Tests

### Unit Tests
   * `mvn clean install`
### Integration Tests
   * `mvn clean install -PrunITs`

# Install

# Running
## Pre-requisites
 * Cloudfoundry use, used to create space and to assign role requires at least Org Admin privilege,
 * Org should exist. Creates a new one if required:
    * `cf org-users sandboxes`
 * A default space should exist,

