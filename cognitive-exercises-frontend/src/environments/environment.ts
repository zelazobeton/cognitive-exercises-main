// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  authorizationServerUrl: 'http://localhost:8080/auth/realms/cognitive-exercises/protocol/openid-connect',
  authorizationServerClientId: 'cognitive-exercises-frontend',
  versionedApiUrl: 'http://localhost:8081/v1',
  //storageTokenKey: Math.random().toString(36).substring(10)
  storageTokenKey: 'storageTokenKey',
  storageRefreshTokenKey: 'storageRefreshTokenKey'
};

export const authServerUris = {
  authorizationServerTokenUrl: environment.authorizationServerUrl + '/token',
  authorizationServerLogoutUrl: environment.authorizationServerUrl + '/logout',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
