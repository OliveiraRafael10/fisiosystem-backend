package com.fisio.fisiosystem.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisio.fisiosystem.dto.EncaminhamentoResponseDTO;
import com.fisio.fisiosystem.dto.MotivoRetiradaDTO;
import com.fisio.fisiosystem.entity.Encaminhamento;
import com.fisio.fisiosystem.entity.enums.Prioridade;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;
import com.fisio.fisiosystem.service.EncaminhamentoService;

@RestController
@RequestMapping("/encaminhamentos")
public class EncaminhamentoController {
	private final EncaminhamentoService encaminhamentoService;
	
	public EncaminhamentoController(EncaminhamentoService encaminhamentoService) {
		
		this.encaminhamentoService = encaminhamentoService;;
	}
	
	@PostMapping
	public Encaminhamento cadastrar(@RequestBody Encaminhamento encaminhamento) {
		
		return encaminhamentoService.salvar(encaminhamento);
	}
	
	@GetMapping
	public List<EncaminhamentoResponseDTO> listarTodos(){
		
		return encaminhamentoService.listarTodos();
	}
	
	@GetMapping("/{id}")
	public EncaminhamentoResponseDTO buscarPorId(@PathVariable Long id) {

	    return encaminhamentoService.buscarPorId(id);
	}
	
	@PutMapping("/{id}/assumir")
	public Encaminhamento assumirEncaminhamento(
			@PathVariable Long id, 
			@RequestParam Long fisioterapeutaId) {
		
		return encaminhamentoService.assumirEncaminhamento(id, fisioterapeutaId);
	}
	
	@PutMapping("/{id}/retirar")
	public Encaminhamento retirarEncaminhamento(
			@PathVariable Long id, 
			@RequestBody MotivoRetiradaDTO dados) {
		 
		return encaminhamentoService.retirarEncaminhamento(id, dados.motivo());
	}
	
	@PutMapping("/{id}/alta")
	public Encaminhamento darAlta(@PathVariable Long id) {
		return encaminhamentoService.darAlta(id);
	}
	
	@GetMapping("/fila")
	public List<EncaminhamentoResponseDTO> listarFila(){
		return encaminhamentoService.listarFila();
	}
	
	@GetMapping("/status/{status}")
	public List<EncaminhamentoResponseDTO> buscarPorStatus(@PathVariable StatusEncaminhamento status){
		return encaminhamentoService.buscarPorStatus(status);
	}
	
	@GetMapping("/prioridade/{prioridade}")
	public List<EncaminhamentoResponseDTO> buscarPorprioridade(@PathVariable Prioridade prioridade){
		return encaminhamentoService.buscarPorPrioridade(prioridade);
	}
	
	@GetMapping("/paciente")
	public List<EncaminhamentoResponseDTO> buscarPorNome(@RequestParam String nome){
		return encaminhamentoService.buscarPorPaciente(nome);
	}
	
	@GetMapping("/periodo")
	public List<EncaminhamentoResponseDTO> buscarPorPeriodo(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim){
		return encaminhamentoService.buscarPorPeriodo(dataInicio, dataFim);
	}
	
	@GetMapping("/indicadoresStatus")
	public Map<String, Long> obterIndicadoresStatus(){
		return encaminhamentoService.obterIndicadoresDeStatus();
	}
	
	@GetMapping("/indicadoresPrioridade")
	public Map<String, Long> obterIndicadoresPrioridade(){
		return encaminhamentoService.ObterIndicadoresDePrioridade();
	}
	
	@GetMapping("/indicadores/fila-prioridade")
	public Map<String, Long> obterIndicadoresPrioridadeFila(){
		return encaminhamentoService.ObterIndicadoresPrioridadeDaFila(); 
	}
	
	@GetMapping("/indicadores/tempo-medio-espera")
	public double ObterTempoMedioEspera() {
		return encaminhamentoService.calcularTempoMedioEspera();
	}
}

