package com.fisio.fisiosystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fisio.fisiosystem.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
	Optional<Paciente> findByNumeroSus(String numeroSus);

	boolean existsByNumeroSus(String numeroSus);
	
	List<Paciente> findByNomeContainingIgnoreCase(String nome);
	
	boolean existsByNumeroSusAndIdNot(String numeroSus, Long id);
}
