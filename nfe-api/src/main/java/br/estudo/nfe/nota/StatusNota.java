package br.estudo.nfe.nota;

/**
 * Ciclo de vida da nota:
 * RASCUNHO → ENVIADA → AUTORIZADA → CANCELADA
 *                    ↘ REJEITADA
 */
public enum StatusNota {
    RASCUNHO,
    ENVIADA,
    AUTORIZADA,
    REJEITADA,
    CANCELADA
}
