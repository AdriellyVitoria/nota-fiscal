import { tokenValido } from './auth'

const BASE = import.meta.env.VITE_API_URL

async function requisicao(caminho, { metodo = 'GET', corpo, tipo = 'json' } = {}) {
  const token = await tokenValido()
  const resposta = await fetch(BASE + caminho, {
    method: metodo,
    headers: {
      Authorization: `Bearer ${token}`,
      ...(corpo ? { 'Content-Type': 'application/json' } : {})
    },
    body: corpo ? JSON.stringify(corpo) : undefined
  })

  if (!resposta.ok) {
    throw new Error(await mensagemDeErro(resposta))
  }
  if (resposta.status === 204) {
    return null
  }
  return tipo === 'texto' ? resposta.text() : resposta.json()
}

async function mensagemDeErro(resposta) {
  if (resposta.status === 401) return 'Sessão expirada. Faça login novamente.'
  if (resposta.status === 403) return 'Você não tem permissão para esta ação.'
  try {
    const erro = await resposta.json()
    if (erro.mensagem) return erro.mensagem
    if (erro.violations) return erro.violations.map(v => v.message).join('; ')
  } catch {
    return `Erro ${resposta.status}`
  }
  return `Erro ${resposta.status}`
}

export const api = {
  notas: (status, pagina = 0) =>
    requisicao(`/notas?pagina=${pagina}&tamanho=10${status ? `&status=${status}` : ''}`),
  nota: id => requisicao(`/notas/${id}`),
  criarNota: pedido => requisicao('/notas', { metodo: 'POST', corpo: pedido }),
  emitir: id => requisicao(`/notas/${id}/emitir`, { metodo: 'POST' }),
  excluir: id => requisicao(`/notas/${id}`, { metodo: 'DELETE' }),
  xml: id => requisicao(`/notas/${id}/xml`, { tipo: 'texto' }),
  danfe: id => requisicao(`/notas/${id}/danfe`, { tipo: 'texto' }),
  produtos: () => requisicao('/produtos'),
  clientes: () => requisicao('/clientes'),
  auditoria: () => requisicao('/auditoria')
}
