<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { usuario } from '../auth'
import { moeda, dataHora, chaveAgrupada } from '../formato'
import StatusNota from '../components/StatusNota.vue'

const props = defineProps({ id: { type: String, required: true } })
const router = useRouter()

const nota = ref(null)
const erro = ref('')
const processando = ref(false)
const xml = ref('')
let temporizador

const podeEmitir = computed(() => usuario.emissor && ['RASCUNHO', 'REJEITADA'].includes(nota.value?.status))
const podeExcluir = computed(() => usuario.emissor && nota.value?.status === 'RASCUNHO')

async function carregar() {
  try {
    nota.value = await api.nota(props.id)
  } catch (e) {
    erro.value = e.message
  }
}

async function executar(acao) {
  processando.value = true
  erro.value = ''
  try {
    await acao()
  } catch (e) {
    erro.value = e.message
  } finally {
    processando.value = false
  }
}

const emitir = () => executar(async () => (nota.value = await api.emitir(props.id)))

const excluir = () =>
  executar(async () => {
    if (!confirm('Excluir este rascunho?')) return
    await api.excluir(props.id)
    router.push('/notas')
  })

const verXml = () => executar(async () => (xml.value = xml.value ? '' : await api.xml(props.id)))

const verDanfe = () =>
  executar(async () => {
    const html = await api.danfe(props.id)
    const url = URL.createObjectURL(new Blob([html], { type: 'text/html' }))
    window.open(url, '_blank')
  })

onMounted(() => {
  carregar()
  temporizador = setInterval(() => {
    if (nota.value?.status === 'ENVIADA') carregar()
  }, 2000)
})
onUnmounted(() => clearInterval(temporizador))
</script>

<template>
  <p v-if="erro" class="erro">{{ erro }}</p>

  <template v-if="nota">
    <div class="cabecalho-pagina">
      <h1>Nota nº {{ nota.numero }} <small>série {{ nota.serie }}</small></h1>
      <StatusNota :status="nota.status" />
    </div>

    <p v-if="nota.status === 'ENVIADA'" class="aviso">Aguardando o SEFAZ processar o recibo {{ nota.recibo }}…</p>
    <p v-if="nota.motivo" class="erro">{{ nota.motivo }}</p>

    <section class="cartao grade">
      <div><span class="rotulo">Destinatário</span>{{ nota.clienteNome }}</div>
      <div><span class="rotulo">Emissão</span>{{ dataHora(nota.dataEmissao) }}</div>
      <div><span class="rotulo">Valor total</span><strong>{{ moeda(nota.valorTotal) }}</strong></div>
      <div><span class="rotulo">Protocolo</span>{{ nota.protocolo ?? '—' }}</div>
      <div><span class="rotulo">Criada por</span>{{ nota.criadaPor ?? '—' }}</div>
      <div><span class="rotulo">Emitida por</span>{{ nota.emitidaPor ?? '—' }}</div>
      <div class="largura-total"><span class="rotulo">Chave de acesso</span><code>{{ chaveAgrupada(nota.chaveAcesso) }}</code></div>
    </section>

    <section class="cartao">
      <h2>Itens</h2>
      <table class="tabela">
        <thead>
          <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th class="num">Qtd</th>
            <th class="num">Valor unit.</th>
            <th class="num">Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in nota.itens" :key="item.produtoCodigo">
            <td>{{ item.produtoCodigo }}</td>
            <td>{{ item.descricao }}</td>
            <td class="num">{{ item.quantidade }}</td>
            <td class="num">{{ moeda(item.valorUnitario) }}</td>
            <td class="num">{{ moeda(item.valorTotal) }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <div class="acoes">
      <button v-if="podeEmitir" :disabled="processando" @click="emitir">Emitir no SEFAZ</button>
      <button class="secundario" :disabled="processando" @click="verDanfe">Ver DANFE</button>
      <button class="secundario" :disabled="processando" @click="verXml">{{ xml ? 'Ocultar XML' : 'Ver XML' }}</button>
      <button v-if="podeExcluir" class="perigo" :disabled="processando" @click="excluir">Excluir rascunho</button>
    </div>

    <pre v-if="xml" class="xml">{{ xml }}</pre>
  </template>
</template>
