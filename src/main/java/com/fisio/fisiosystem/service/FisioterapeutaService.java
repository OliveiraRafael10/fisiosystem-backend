package com.fisio.fisiosystem.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fisio.fisiosystem.entity.Fisioterapeuta;
import com.fisio.fisiosystem.repository.FisioterapeutaRepository;

@Service
public class FisioterapeutaService {
	private final FisioterapeutaRepository fisioterapeutaRepository; 
	
	public FisioterapeutaService(FisioterapeutaRepository fisioterapeutaRepository) {
		this.fisioterapeutaRepository = fisioterapeutaRepository;
	}
	
	public Fisioterapeuta salvar(Fisioterapeuta fisioterapeuta) {

	    if (fisioterapeuta.getNome() == null ||
	            fisioterapeuta.getNome().isBlank()) {

	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome do fisioterapeuta é obrigatório"
	        );
	    }

	    fisioterapeuta.setAtivo(true);

	    return fisioterapeutaRepository.save(fisioterapeuta);
	}
	
	public List<Fisioterapeuta> listarTodos(){
		return fisioterapeutaRepository.findAll();
	}
	
	public Fisioterapeuta atualizar(
	        Long id,
	        Fisioterapeuta dadosAtualizados) {

	    Fisioterapeuta fisioterapeuta =
	            fisioterapeutaRepository.findById(id)
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.NOT_FOUND,
	                            "Fisioterapeuta não encontrado"
	                    ));

	    if (dadosAtualizados.getNome() == null ||
	            dadosAtualizados.getNome().isBlank()) {

	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome do fisioterapeuta é obrigatório"
	        );
	    }

	    fisioterapeuta.setNome(dadosAtualizados.getNome());
	    fisioterapeuta.setCrefito(dadosAtualizados.getCrefito());
	    fisioterapeuta.setTelefone(dadosAtualizados.getTelefone());

	    return fisioterapeutaRepository.save(fisioterapeuta);
	}
	
	public Fisioterapeuta inativar(Long id) {

	    Fisioterapeuta fisioterapeuta =
	            fisioterapeutaRepository.findById(id)
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.NOT_FOUND,
	                            "Fisioterapeuta não encontrado"
	                    ));

	    if (!fisioterapeuta.isAtivo()) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Fisioterapeuta já está inativo"
	        );
	    }

	    fisioterapeuta.setAtivo(false);

	    return fisioterapeutaRepository.save(fisioterapeuta);
	}
	
	public Fisioterapeuta ativar(Long id) {

	    Fisioterapeuta fisioterapeuta =
	            fisioterapeutaRepository.findById(id)
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.NOT_FOUND,
	                            "Fisioterapeuta não encontrado"
	                    ));

	    if (fisioterapeuta.isAtivo()) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Fisioterapeuta já está ativo"
	        );
	    }

	    fisioterapeuta.setAtivo(true);

	    return fisioterapeutaRepository.save(fisioterapeuta);
	}
	
	public List<Fisioterapeuta> buscarPorNome(String nome) {

	    if (nome == null || nome.isBlank()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome para busca é obrigatório"
	        );
	    }

	    return fisioterapeutaRepository.findByNomeContainingIgnoreCase(nome);
	}
	
	public Fisioterapeuta buscarPorId(Long id) {
	    return fisioterapeutaRepository.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Fisioterapeuta não encontrado"
	            ));
	}
	
	
}
