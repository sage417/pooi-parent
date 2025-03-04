import React, {useContext} from 'react'
// @ts-ignore
import ReactDOM from 'react-dom/client';

import {AuthContext, AuthProvider, type IAuthContext, type TAuthConfig} from 'react-oauth2-code-pkce'

// Get info from http://localhost:8080/realms/test/.well-known/openid-configuration

const authConfig: TAuthConfig = {
  clientId: 'workflow_spa',
  authorizationEndpoint: 'https://keycloak.pooi.app/realms/pooi/protocol/openid-connect/auth',
  logoutEndpoint: 'https://keycloak.pooi.app/realms/pooi/protocol/openid-connect/logout',
  tokenEndpoint: 'https://keycloak.pooi.app/realms/pooi/protocol/openid-connect/token',
  redirectUri: 'http://localhost:3000/',
  scope: 'profile openid',
  // Example to redirect back to original path after login has completed
  // preLogin: () => localStorage.setItem('preLoginPath', window.location.pathname),
  // postLogin: () => window.location.replace(localStorage.getItem('preLoginPath') || ''),
  decodeToken: true,
  autoLogin: false,
}

function LoginInfo() {
  const { tokenData, token, logIn, logOut, idToken, idTokenData, error }: IAuthContext = useContext(AuthContext)

  if (error) {
    return (
      <>
        <div style={{ color: 'red' }}>An error occurred during authentication: {error}</div>
        <button onClick={() => logOut()}>Log out</button>
      </>
    )
  }

  return (
    <>
      {token ? (
        <>
          <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
            <div>
              <h4>Access Token (JWT)</h4>
              <pre
                style={{
                  width: '400px',
                  margin: '10px',
                  padding: '5px',
                  border: 'black 2px solid',
                  wordBreak: 'break-all',
                  whiteSpace: 'break-spaces',
                }}
              >
                {token}
              </pre>
            </div>
            <div>
              <h4>Login Information from Access Token (Base64 decoded JWT)</h4>
              <pre
                style={{
                  width: '400px',
                  margin: '10px',
                  padding: '5px',
                  border: 'black 2px solid',
                  wordBreak: 'break-all',
                  whiteSpace: 'break-spaces',
                }}
              >
                {JSON.stringify(tokenData, null, 2)}
              </pre>
            </div>
          </div>
          <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
            <div>
              <h4>ID Token (JWT)</h4>
              <pre
                style={{
                  width: '400px',
                  margin: '10px',
                  padding: '5px',
                  border: 'black 2px solid',
                  wordBreak: 'break-all',
                  whiteSpace: 'break-spaces',
                }}
              >
                {idToken}
              </pre>
            </div>
            <div>
              <h4>Login Information from ID Token (Base64 decoded JWT)</h4>
              <pre
                style={{
                  width: '400px',
                  margin: '10px',
                  padding: '5px',
                  border: 'black 2px solid',
                  wordBreak: 'break-all',
                  whiteSpace: 'break-spaces',
                }}
              >
                {JSON.stringify(idTokenData, null, 2)}
              </pre>
            </div>
          </div>
          <button onClick={() => logOut()}>Log out</button>
        </>
      ) : (
        <>
          <div>You are not logged in.</div>
          <button onClick={() => logIn()}>Log in</button>
        </>
      )}
    </>
  )
}


const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <div>
    <div>
      <h1>Demo using the 'react-oauth2-code-pkce' package</h1>
      <p>
        Github:{' '}
        <a href='https://github.com/soofstad/react-oauth2-pkce'>https://github.com/soofstad/react-oauth2-pkce</a>
      </p>
      <p>
        NPM:{' '}
        <a href='https://www.npmjs.com/package/react-oauth2-code-pkce'>
          https://www.npmjs.com/package/react-oauth2-code-pkce
        </a>
      </p>
    </div>
    <AuthProvider authConfig={authConfig}>
      {/* @ts-ignore*/}
      <LoginInfo />
    </AuthProvider>
  </div>
)