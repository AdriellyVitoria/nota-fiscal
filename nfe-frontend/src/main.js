import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { iniciarLogin } from './auth'
import './estilo.css'

iniciarLogin()
  .then(() => createApp(App).use(router).mount('#app'))
  .catch(() => {
    document.getElementById('app').textContent = 'Não foi possível conectar ao Keycloak (http://localhost:8280).'
  })
