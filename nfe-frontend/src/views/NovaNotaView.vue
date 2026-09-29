<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { moeda } from '../formato'

const router = useRouter()

const clientes = ref([])
const produtos = ref([])
const clienteId = ref(null)
const itens = ref([{ produtoId: null, quantidade: 1 }])
const erro = ref('')
const salvando = ref(false)

const produtoPorId = computed(() => new Map(produtos.value.map(p => [p.id, p])))

const subtotal = item => (produtoPorId.value.get(item.produtoId)?.valorUnitario ?? 0) * (item.quantidade || 0)

const total = computed(() => itens.value.reduce((soma, item) => soma + subtotal(item), 0))

const podeSalvar = computed(
  () => clienteId.value && itens.value.length > 0 && itens.value.every(i => i.produtoId && i.quantidade > 0)
)

onMounted(async () => {
  try {
    ;[clientes.value, produtos.value] = await Promise.all([api.clientes(), api.produtos()])
  } catch (e) {
    erro.value = e.message
  }
})

function adicionarItem() {
  itens.value.push({ produtoId: null, quantidade: 1 })
}

function removerItem(indice) {
  itens.value.splice(indice, 1)
}

async function salvar() {
  salvando.value = true
  erro.value = ''
  try {
    const nota = await api.criarNota({ clienteId: clienteId.value, itens: itens.value })
    router.push(`/notas/${nota.id}`)
  } catch (e) {
    erro.value = e.message
  } finally {
    salvando.value = false
  }
}
</script>

<template>
  <h1>Nova nota fiscal</h1>

  <p v-if="erro" class="erro">{{ erro }}</p>

  <section class="cartao">
    <label>
      Destinatário
      <select v-model="clienteId">
        <option :value="null" disabled>Selecione o cliente</option>
        <option v-for="c in clientes" :key="c.id" :value="c.id">{{ c.nome }} — {{ c.documento }}</option>
      </select>
    </label>
  </section>

  <section class="cartao">
    <h2>Itens</h2>
    <table class="tabela">
      <thead>
        <tr>
          <th>Produto</th>
          <th class="num">Valor unit.</th>
          <th class="num">Quantidade</th>
          <th class="num">Subtotal</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, i) in itens" :key="i">
          <td>
            <select v-model="item.produtoId">
              <option :value="null" disabled>Selecione</option>
              <option v-for="p in produtos" :key="p.id" :value="p.id">{{ p.codigo }} — {{ p.descricao }}</option>
            </select>
          </td>
          <td class="num">{{ moeda(produtoPorId.get(item.produtoId)?.valorUnitario) }}</td>
          <td class="num"><input v-model.number="item.quantidade" type="number" min="1" step="1" class="qtd" /></td>
          <td class="num">{{ moeda(subtotal(item)) }}</td>
          <td><button class="link" :disabled="itens.length === 1" @click="removerItem(i)">remover</button></td>
        </tr>
      </tbody>
    </table>
    <button class="secundario" @click="adicionarItem">+ Adicionar item</button>
  </section>

  <div class="rodape-form">
    <div class="total">Total: {{ moeda(total) }}</div>
    <button :disabled="!podeSalvar || salvando" @click="salvar">
      {{ salvando ? 'Salvando…' : 'Criar rascunho' }}
    </button>
  </div>
</template>
