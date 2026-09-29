<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { api } from '../api'
import { moeda, dataHora } from '../formato'
import StatusNota from '../components/StatusNota.vue'

const STATUS = ['RASCUNHO', 'ENVIADA', 'AUTORIZADA', 'REJEITADA', 'CANCELADA']

const notas = ref([])
const filtro = ref('')
const pagina = ref(0)
const carregando = ref(false)
const erro = ref('')
let temporizador

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    notas.value = await api.notas(filtro.value, pagina.value)
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

watch(filtro, () => {
  pagina.value = 0
  carregar()
})
watch(pagina, carregar)

onMounted(() => {
  carregar()
  temporizador = setInterval(() => {
    if (notas.value.some(n => n.status === 'ENVIADA')) carregar()
  }, 3000)
})
onUnmounted(() => clearInterval(temporizador))
</script>

<template>
  <div class="cabecalho-pagina">
    <h1>Notas fiscais</h1>
    <select v-model="filtro">
      <option value="">Todos os status</option>
      <option v-for="s in STATUS" :key="s" :value="s">{{ s }}</option>
    </select>
  </div>

  <p v-if="erro" class="erro">{{ erro }}</p>

  <table class="tabela">
    <thead>
      <tr>
        <th>Nº</th>
        <th>Cliente</th>
        <th>Emissão</th>
        <th class="num">Valor</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="nota in notas" :key="nota.id" class="clicavel" @click="$router.push(`/notas/${nota.id}`)">
        <td>{{ nota.numero }}</td>
        <td>{{ nota.clienteNome }}</td>
        <td>{{ dataHora(nota.dataEmissao) }}</td>
        <td class="num">{{ moeda(nota.valorTotal) }}</td>
        <td><StatusNota :status="nota.status" /></td>
      </tr>
      <tr v-if="!carregando && notas.length === 0">
        <td colspan="5" class="vazio">Nenhuma nota encontrada.</td>
      </tr>
    </tbody>
  </table>

  <div class="paginacao">
    <button class="secundario" :disabled="pagina === 0" @click="pagina--">Anterior</button>
    <span>Página {{ pagina + 1 }}</span>
    <button class="secundario" :disabled="notas.length < 10" @click="pagina++">Próxima</button>
  </div>
</template>
