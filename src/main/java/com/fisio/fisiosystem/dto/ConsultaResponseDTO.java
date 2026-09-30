package com.fisio.fisiosystem.dto;

import java.time.LocalDateTime;

import com.fisio.fisiosystem.entity.enums.StatusConsulta;

public record ConsultaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        StatusConsulta status,
        Long encaminhamentoId,
        Long pacienteId,
        String pacienteNome,
        Long fisioterapeutaId,
        String fisioterapeutaNome,
        String observacoes,
        String diagnostico,
        String procedimentos,
        String conduta
) {
}
