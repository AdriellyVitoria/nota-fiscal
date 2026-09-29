<script setup>
import { usuario, sair } from './auth'
</script>

<template>
  <header class="topo">
    <div class="marca">NF-e <span>Estudo</span></div>
    <nav>
      <RouterLink to="/notas">Notas</RouterLink>
      <RouterLink v-if="usuario.emissor" to="/notas/nova">Nova nota</RouterLink>
      <RouterLink to="/auditoria">Auditoria</RouterLink>
    </nav>
    <div class="usuario">
      <span>{{ usuario.nome }}</span>
      <span class="papel">{{ usuario.emissor ? 'emissor' : 'consulta' }}</span>
      <button class="secundario" @click="sair">Sair</button>
    </div>
  </header>
  <main class="conteudo">
    <RouterView />
  </main>
</template>
