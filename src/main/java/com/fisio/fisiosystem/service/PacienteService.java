package com.fisio.fisiosystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fisio.fisiosystem.entity.Paciente;
import com.fisio.fisiosystem.repository.PacienteRepository;

@Service
public class PacienteService {
	private final PacienteRepository pacienteRepository;
	
	public PacienteService(PacienteRepository pacienteRepository) {
		this.pacienteRepository = pacienteRepository;
	}
	
	public Paciente salvar(Paciente paciente) {

	    if (paciente.getNumeroSus() == null || paciente.getNumeroSus().isBlank()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Número SUS é obrigatório"
	        );
	    }

	    if (pacienteRepository.existsByNumeroSus(paciente.getNumeroSus())) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Já existe um paciente cadastrado com este número SUS"
	        );
	    }
	    
	    if (paciente.getNome() == null || paciente.getNome().isBlank()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome do paciente é obrigatório"
	        );
	    }

	    return pacienteRepository.save(paciente);
	}
	
	public List<Paciente> listarTodos(){
		return pacienteRepository.findAll();
	}
	
	public List<Paciente> buscarPorNome(String nome) {

	    if (nome == null || nome.isBlank()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome para busca é obrigatório"
	        );
	    }

	    return pacienteRepository.findByNomeContainingIgnoreCase(nome);
	}
	
	public Paciente buscarPorNumeroSus(String numeroSus) {

	    return pacienteRepository.findByNumeroSus(numeroSus)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Paciente não encontrado"
	            ));
	}
	
	public Paciente atualizar(Long id, Paciente dadosAtualizados) {

	    Paciente paciente = pacienteRepository.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Paciente não encontrado"
	            ));

	    if (dadosAtualizados.getNome() == null ||
	            dadosAtualizados.getNome().isBlank()) {

	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome do paciente é obrigatório"
	        );
	    }

	    if (dadosAtualizados.getNumeroSus() == null ||
	            dadosAtualizados.getNumeroSus().isBlank()) {

	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Número SUS é obrigatório"
	        );
	    }

	    boolean numeroSusDuplicado =
	            pacienteRepository.existsByNumeroSusAndIdNot(
	                    dadosAtualizados.getNumeroSus(),
	                    id
	            );

	    if (numeroSusDuplicado) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Já existe outro paciente cadastrado com este número SUS"
	        );
	    }

	    paciente.setNome(dadosAtualizados.getNome());
	    paciente.setNumeroSus(dadosAtualizados.getNumeroSus());
	    paciente.setDataNascimento(dadosAtualizados.getDataNascimento());
	    paciente.setEndereco(dadosAtualizados.getEndereco());
	    paciente.setTelefone(dadosAtualizados.getTelefone());

	    return pacienteRepository.save(paciente);
	}
	
	
	
	
	
}
