package com.fisio.fisiosystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisio.fisiosystem.entity.Consulta;
import com.fisio.fisiosystem.entity.enums.StatusConsulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long>{
	List<Consulta> findByStatus(StatusConsulta status);
	
	List<Consulta> findByFisioterapeutaId(Long fisioterapeutaId);
	
	List<Consulta> findByEncaminhamentoPacienteNomeContainingIgnoreCase(String nome);
	
	List<Consulta> findByDataHoraBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
	
	List<Consulta> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);
	
	boolean existsByFisioterapeutaIdAndDataHoraAndStatus(Long fisioterapeutaId, LocalDateTime dataHora, StatusConsulta status);
	
	List<Consulta> findByFisioterapeutaIdAndDataHoraBetweenOrderByDataHoraAsc(Long fisioterapeutaId, LocalDateTime inicio, LocalDateTime fim);
	
	long countByStatus(StatusConsulta status);
	
	long countByStatusAndDataHoraBetween(StatusConsulta status, LocalDateTime inicio, LocalDateTime fim);
	
	long countByFisioterapeutaIdAndStatus(Long fisioterapeutaId, StatusConsulta status);
	
	long countByFisioterapeutaIdAndStatusAndDataHoraBetween(
	        Long fisioterapeutaId,
	        StatusConsulta status,
	        LocalDateTime inicio,
	        LocalDateTime fim
	);
	
	
} 
