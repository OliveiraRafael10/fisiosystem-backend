package com.fisio.fisiosystem.dto;

import java.time.LocalDate;

import com.fisio.fisiosystem.entity.enums.Prioridade;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;
import com.fisio.fisiosystem.entity.enums.TipoAtendimento;

public record EncaminhamentoResponseDTO(
        Long id,
        LocalDate dataEntrega,

        Long pacienteId,
        String pacienteNome,

        String medicoSolicitante,
        TipoAtendimento tipoAtendimento,
        StatusEncaminhamento statusEncaminhamento,
        Prioridade prioridade,

        Long fisioterapeutaResponsavelId,
        String fisioterapeutaResponsavelNome,

        LocalDate dataAssuncao,
        LocalDate dataRetirada,
        String motivoRetirada,
        LocalDate dataAlta,

        String patologia,
        String observacoes
) {
}
