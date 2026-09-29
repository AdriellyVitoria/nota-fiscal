import { createRouter, createWebHistory } from 'vue-router'
import { usuario } from './auth'
import NotasView from './views/NotasView.vue'
import NovaNotaView from './views/NovaNotaView.vue'
import NotaDetalheView from './views/NotaDetalheView.vue'
import AuditoriaView from './views/AuditoriaView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/notas' },
    { path: '/notas', component: NotasView },
    { path: '/notas/nova', component: NovaNotaView, meta: { exigeEmissor: true } },
    { path: '/notas/:id', component: NotaDetalheView, props: true },
    { path: '/auditoria', component: AuditoriaView }
  ]
})

router.beforeEach(destino => {
  if (destino.meta.exigeEmissor && !usuario.emissor) {
    return '/notas'
  }
})

export default router
