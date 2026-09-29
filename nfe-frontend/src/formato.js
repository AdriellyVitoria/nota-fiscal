const moedaBR = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
const dataHoraBR = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' })

export const moeda = valor => moedaBR.format(Number(valor ?? 0))

export const dataHora = valor => (valor ? dataHoraBR.format(new Date(valor)) : '—')

export const chaveAgrupada = chave => chave?.replace(/(\d{4})(?=\d)/g, '$1 ') ?? ''
