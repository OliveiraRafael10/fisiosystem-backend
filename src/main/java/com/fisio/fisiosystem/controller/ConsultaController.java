package com.fisio.fisiosystem.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisio.fisiosystem.dto.ConsultaResponseDTO;
import com.fisio.fisiosystem.dto.ObservacaoConsultaDTO;
import com.fisio.fisiosystem.dto.ReagendarConsultaDTO;
import com.fisio.fisiosystem.dto.RealizarConsultaDTO;
import com.fisio.fisiosystem.entity.Consulta;
import com.fisio.fisiosystem.entity.enums.StatusConsulta;
import com.fisio.fisiosystem.service.ConsultaService;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {
	private final ConsultaService consultaService;
	
	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}
	
	@PostMapping
	public Consulta cadastrar(@RequestBody Consulta consulta) {
		return consultaService.agendarConsulta(consulta);
	}
	
	@PutMapping("/{id}/realizar")
	public ConsultaResponseDTO realizarConsulta(@PathVariable Long id, @RequestBody RealizarConsultaDTO dados) {
		return consultaService.realizarConsulta(
				id, 
				dados.diagnostico(), 
				dados.procedimentos(), 
				dados.conduta());
	}
	
	@PutMapping("/{id}/cancelar")
	public ConsultaResponseDTO cancelarConsulta(@PathVariable Long id, @RequestBody ObservacaoConsultaDTO dados) {
	    return consultaService.cancelarConsulta(id, dados.observacao());
	}
	
	@PutMapping("/{id}/falta")
	public ConsultaResponseDTO registrarFalta(@PathVariable Long id, @RequestBody ObservacaoConsultaDTO dados) {
	    return consultaService.registrarFalta(id, dados.observacao());
	}
	
	@PutMapping("/{id}/reagendar")
	public ConsultaResponseDTO reagendarConsulta(@PathVariable Long id, @RequestBody ReagendarConsultaDTO dados) {
	    return consultaService.reagendarConsulta(id, dados.novaDataHora());
	}
	
	@GetMapping
	public List<ConsultaResponseDTO> listarTodas() {
		return consultaService.listarTodas();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id){
		
		Optional<Consulta> consulta = consultaService.buscarPorId(id);
		
		if(consulta.isPresent()) {
			return ResponseEntity.ok(consulta.get());
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/status/{status}")
	public List<ConsultaResponseDTO> buscarPorStatus(@PathVariable StatusConsulta status){
		return consultaService.buscarPorStatus(status);
	}
	
	@GetMapping("/fisioterapeuta/{fisioterapeutaId}")
	public List<ConsultaResponseDTO> buscarPorFisioterapeuta(@PathVariable Long fisioterapeutaId){
		return consultaService.buscarPorFisioterapeuta(fisioterapeutaId);
	}
	
	@GetMapping("/paciente")
	public List<ConsultaResponseDTO> buscarPorPaciente(@RequestParam String nome){
		return consultaService.buscarPorPaciente(nome);
	}
	
	@GetMapping("/periodo")
	public List<ConsultaResponseDTO> buscarPorPeriodo(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFinal){
		return consultaService.buscarPorPeriodo(dataInicio, dataFinal);
	}
	
	@GetMapping("/agenda")
	public List<ConsultaResponseDTO> buscarAgendaDoDia(@RequestParam LocalDate data){
		return consultaService.buscarAgendaDoDia(data);
	}
	
	@GetMapping("/agenda/fisioterapeuta/{fisioterapeutaId}")
	public List<ConsultaResponseDTO> buscarAgendaDoDiaFisioterapeuta(@PathVariable Long fisioterapeutaId, @RequestParam LocalDate data){
		return consultaService.buscarAgendaDoDiaFisioterapeuta(fisioterapeutaId, data);
	}
	
	@GetMapping("/indicadores")
	public Map<String, Long> obterIndicadores() {
	    return consultaService.obterIndicadores();
	}
	
	@GetMapping("/indicadores/periodo")
	public Map<String, Long> obterIndicadoresPorPeriodo( @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {

	    return consultaService.obterIndicadoresPorPeriodo(dataInicio, dataFim);
	}
	
	@GetMapping("/indicadores/taxa-faltas")
	public Map<String, Object> obterTaxaFaltasPorPeriodo(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {

	    return consultaService.obterTaxaFaltasPorPeriodo(dataInicio, dataFim);
	}
	
	@GetMapping("/indicadores/fisioterapeuta/{fisioterapeutaId}")
	public Map<String, Long> obterIndicadoresPorFisioterapeuta(@PathVariable Long fisioterapeutaId) {

	    return consultaService.obterIndicadoresPorFisioterapeuta(fisioterapeutaId);
	}
	
	@GetMapping("/estatisticas/fisioterapeuta/{fisioterapeutaId}")
	public Map<String, Object> obterEstatisticasPorFisioterapeutaEPeriodo(
	        @PathVariable Long fisioterapeutaId,
	        @RequestParam LocalDate dataInicio,
	        @RequestParam LocalDate dataFim) {

	    return consultaService.obterEstatisticasPorFisioterapeutaEPeriodo(
	            fisioterapeutaId,
	            dataInicio,
	            dataFim
	    );
	}
	
	
}