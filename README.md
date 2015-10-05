# elpaaso-sandbox-service

[![Build Status](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service.svg?branch=master)](https://travis-ci.org/Orange-OpenSource/elpaaso-sandbox-service)  [![Swagger UI](https://img.shields.io/badge/Swagger%20UI-explore-green.svg)](https://orange-opensource.github.io/elpaaso-sandbox-service/) [![Validator](http://online.swagger.io/validator/?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)](https://online.swagger.io/validator/debug?url=https://raw.githubusercontent.com/Orange-OpenSource/elpaaso-sandbox-service/master/src/main/resources/swagger.yaml)


## Sandbox service stories

###stories:
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


##Tech specs of the sandbox service 
![Sandbox service](http://g.gravizo.com/g?
@startuml
User -> SandboxService: POST /sandboxes
User <-- SandboxService: 302 location=uaa/login
User -> uaa: ....
User <-- uaa: 302: POST: SandboxService/sandboxes?code=rezrze
User -> SandboxService : POST /sandboxes?code=rezrze
SandboxService -> uaa : GET /oauth/token?code=rezrze
SandboxService <-- uaa : user_token
SandboxService -> CC_api: login (as user)
SandboxService -> CC_api: CreateSpace (as Admin)
SandboxService -> CC_api: AssignManagerRole("org_name","space_name","user_id") (as Admin)
SandboxService -> CC_api: AssignAuditorRole("org_name","space_name","user_id") (as Admin)
SandboxService -> CC_api: AssignDeveloperRole("org_name","space_name","user_id") (as Admin)
@enduml
)


* sandbox oauth roles 
  *  OAuth resource server (/sandbox)
    *  scopes: NONE specific yet

  *  OAuth client (CF resources)
    *  scope: scim... cloudcountrol...


* sandboxes/me GET (Bearer: AccessToken)
    *  301 sandbox/guid
    *  404 


* sandboxes POST (Bearer: AccessToken)
    *  301 sandbox/guid

* sandbox/guid/ GET (Bearer: AccessToken)
    * guid = sandbox_guid
    * 200: api URL, org, space


    UAA token https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-Tokens.md#getting-started
    "user id" guid http://apidocs.cloudfoundry.org/219/users/get_user_summary.html ?
    + preserves user identity
    username: LDAP DN = "orange CUID" 



Inspirations for API REST
  *  https://github.com/cloudfoundry/cc-api-v3-style-guide/#post
  *  avec "me"?
  *  SCIM ? http://www.simplecloud.info/

**Note:** If wanna use angularjs (or javascript in general) don't forget to manage CORS on UAA and API
