package com.fisio.fisiosystem.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisio.fisiosystem.entity.Paciente;
import com.fisio.fisiosystem.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
	
	private final PacienteService pacienteService;
	
	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}
	
	@PostMapping
	public Paciente cadastrar(@RequestBody Paciente paciente) {
		return pacienteService.salvar(paciente);
	}
	
	@GetMapping
	public List<Paciente> listar(){
		return pacienteService.listarTodos();
	}
	
	@GetMapping("/buscar")
	public List<Paciente> buscarPorNome(@RequestParam String nome) {

	    return pacienteService.buscarPorNome(nome);
	}
	
	@GetMapping("/sus/{numeroSus}")
	public Paciente buscarPorNumeroSus(@PathVariable String numeroSus) {

	    return pacienteService.buscarPorNumeroSus(numeroSus);
	}
	
	@PutMapping("/{id}")
	public Paciente atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {

	    return pacienteService.atualizar(id, paciente);
	}
	
	
	
}
