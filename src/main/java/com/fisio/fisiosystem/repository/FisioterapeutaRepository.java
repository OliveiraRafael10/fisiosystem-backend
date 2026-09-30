package com.fisio.fisiosystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fisio.fisiosystem.entity.Fisioterapeuta;

public interface FisioterapeutaRepository extends JpaRepository<Fisioterapeuta, Long> {
	List<Fisioterapeuta> findByNomeContainingIgnoreCase(String nome);
}



