import Keycloak from 'keycloak-js'
import { reactive } from 'vue'

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID
})

export const usuario = reactive({
  nome: '',
  papeis: [],
  get emissor() {
    return this.papeis.includes('emissor')
  }
})

export async function iniciarLogin() {
  await keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false
  })
  usuario.nome = keycloak.tokenParsed?.preferred_username ?? ''
  usuario.papeis = keycloak.tokenParsed?.realm_access?.roles ?? []
}

export async function tokenValido() {
  await keycloak.updateToken(30)
  return keycloak.token
}

export function sair() {
  keycloak.logout({ redirectUri: window.location.origin })
}
