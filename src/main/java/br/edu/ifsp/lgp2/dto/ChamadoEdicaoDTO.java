package br.edu.ifsp.lgp2.dto;

public record ChamadoEdicaoDTO(
        String titulo,
        String descricao,
        String categoria,
        String status,
        String prioridade,
        String responsavel,
        String observacaoAtendimento) {
}

