<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../api'
import { moeda, dataHora } from '../formato'

const registros = ref([])
const erro = ref('')
const carregando = ref(true)

async function carregar() {
  carregando.value = true
  try {
    registros.value = await api.auditoria()
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

onMounted(carregar)
</script>

<template>
  <div class="cabecalho-pagina">
    <h1>Auditoria (eventos do Kafka)</h1>
    <button class="secundario" @click="carregar">Atualizar</button>
  </div>
  <p class="descricao">Notas autorizadas registradas pelo consumidor do tópico <code>nfe-autorizada</code>.</p>

  <p v-if="erro" class="erro">{{ erro }}</p>

  <table class="tabela">
    <thead>
      <tr>
        <th>Nº</th>
        <th>Cliente</th>
        <th>Protocolo</th>
        <th class="num">Valor</th>
        <th>Autorizada em</th>
        <th>Partição / offset</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="r in registros" :key="r.chaveAcesso">
        <td>{{ r.numero }}</td>
        <td>{{ r.clienteNome }}</td>
        <td>{{ r.protocolo }}</td>
        <td class="num">{{ moeda(r.valorTotal) }}</td>
        <td>{{ dataHora(r.autorizadaEm) }}</td>
        <td>{{ r.particao }} / {{ r.offset }}</td>
      </tr>
      <tr v-if="!carregando && registros.length === 0">
        <td colspan="6" class="vazio">Nenhum evento registrado ainda.</td>
      </tr>
    </tbody>
  </table>
</template>
