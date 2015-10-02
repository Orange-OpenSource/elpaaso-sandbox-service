# elpaaso-sandbox-service


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
    * as a paas-user, once logged into the sandbox service, I am notified by email of my private space credentials


##Tech specs of the sandbox service 

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
