package com.fisio.fisiosystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisio.fisiosystem.entity.Encaminhamento;
import com.fisio.fisiosystem.entity.enums.Prioridade;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;

public interface EncaminhamentoRepository extends JpaRepository<Encaminhamento, Long> {
	List<Encaminhamento> findByStatusEncaminhamentoOrderByDataEntregaAsc(
	        StatusEncaminhamento statusEncaminhamento
	);
	
	@Query("""
		    SELECT e
		    FROM Encaminhamento e
		    WHERE e.statusEncaminhamento = :statusEncaminhamento
		    ORDER BY
		        CASE e.prioridade
		            WHEN com.fisio.fisiosystem.entity.enums.Prioridade.PRIMARIA THEN 1
		            WHEN com.fisio.fisiosystem.entity.enums.Prioridade.SECUNDARIA THEN 2
		            WHEN com.fisio.fisiosystem.entity.enums.Prioridade.TERCIARIA THEN 3
		        END,
		        e.dataEntrega ASC
		""")
		List<Encaminhamento> buscarFilaOrdenada(
		        @Param("statusEncaminhamento") StatusEncaminhamento statusEncaminhamento
		);
	
	List<Encaminhamento> findByStatusEncaminhamento(StatusEncaminhamento status);
	
	List<Encaminhamento> findByPrioridade(Prioridade prioridade);
	
	List<Encaminhamento> findByPacienteNomeContainingIgnoreCase(String nome);
	
	List<Encaminhamento> findByDataEntregaBetween(LocalDate dataInicio, LocalDate dataFim);
	
	long countByStatusEncaminhamento(StatusEncaminhamento status);
	
	long countByPrioridade(Prioridade prioridade);
	
	long countByStatusEncaminhamentoAndPrioridade(StatusEncaminhamento status, Prioridade prioridade);
	
	List<Encaminhamento> findByDataAssuncaoIsNotNull();
} 
