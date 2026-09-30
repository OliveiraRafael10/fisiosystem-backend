package com.fisio.fisiosystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fisio.fisiosystem.dto.ConsultaResponseDTO;
import com.fisio.fisiosystem.entity.Consulta;
import com.fisio.fisiosystem.entity.Encaminhamento;
import com.fisio.fisiosystem.entity.Fisioterapeuta;
import com.fisio.fisiosystem.entity.enums.StatusConsulta;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;
import com.fisio.fisiosystem.repository.ConsultaRepository;
import com.fisio.fisiosystem.repository.EncaminhamentoRepository;
import com.fisio.fisiosystem.repository.FisioterapeutaRepository;

@Service
public class ConsultaService {
	private final ConsultaRepository consultaRepository;
	private final FisioterapeutaRepository fisioterapeutaRepository;
	private final EncaminhamentoRepository encaminhamentoRepository;
	
	public ConsultaService(ConsultaRepository consultaRepository, FisioterapeutaRepository fisioterapeutaRepository, EncaminhamentoRepository encaminhamentoRepository){
		this.consultaRepository = consultaRepository;
		this.fisioterapeutaRepository = fisioterapeutaRepository;
		this.encaminhamentoRepository = encaminhamentoRepository;
	}
	
	public Consulta agendarConsulta(Consulta consulta) {
		Long encaminhamentoId = consulta.getEncaminhamento().getId();
		
		Encaminhamento encaminhamento = encaminhamentoRepository.findById(encaminhamentoId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encaminhamento não encontrado !")
				);
		
		Long fisioterapeutaId = consulta.getFisioterapeuta().getId();
		
		Fisioterapeuta fisioterapeuta = fisioterapeutaRepository.findById(fisioterapeutaId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fisioterapeuta não encontrado !")
				);
		
		if(!fisioterapeuta.isAtivo()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fisioterapeuta não está ativo !");
		}
		
		if (consulta.getDataHora() == null) {
		    throw new ResponseStatusException(
		            HttpStatus.BAD_REQUEST,
		            "A data e hora da consulta são obrigatórias!"
		    );
		}
		
		if(consulta.getDataHora().isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Não é possível agendar uma consulta em uma data ou horário passado!"
				);
		}
		
		boolean horarioOcupado = consultaRepository.existsByFisioterapeutaIdAndDataHoraAndStatus(fisioterapeuta.getId(), consulta.getDataHora(), StatusConsulta.AGENDADA);
		
		if(horarioOcupado) {
			
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"O fisioterapeuta já possui uma consulta agendada neste horario !"
				);
			
		}
		
		if (encaminhamento.getStatusEncaminhamento() != StatusEncaminhamento.ASSUMIDO 
			&& encaminhamento.getStatusEncaminhamento() != StatusEncaminhamento.EM_TRATAMENTO) {

		    throw new ResponseStatusException(
		            HttpStatus.CONFLICT,
		            "O encaminhamento não está disponível para consultas !"
		    );
		}
		
		if (encaminhamento.getFisioterapeutaResponsavel() == null) {
		    throw new ResponseStatusException(
		            HttpStatus.CONFLICT,
		            "O encaminhamento não possui fisioterapeuta responsável !"
		    );
		}
		
		consulta.setEncaminhamento(encaminhamento);
		consulta.setFisioterapeuta(fisioterapeuta);
		consulta.setStatus(StatusConsulta.AGENDADA);
		
		return consultaRepository.save(consulta);
	}
	
	public ConsultaResponseDTO realizarConsulta(
	        Long consultaId,
	        String diagnostico,
	        String procedimentos,
	        String conduta) {

	    Consulta consulta = consultaRepository.findById(consultaId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Consulta não encontrada"
	            ));

	    if (consulta.getStatus() != StatusConsulta.AGENDADA) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Somente consultas agendadas podem ser realizadas"
	        );
	    }

	    if (diagnostico == null || diagnostico.isBlank()
	            || procedimentos == null || procedimentos.isBlank()
	            || conduta == null || conduta.isBlank()) {

	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Diagnóstico, procedimentos e conduta são obrigatórios"
	        );
	    }

	    consulta.setDiagnostico(diagnostico);
	    consulta.setProcedimentos(procedimentos);
	    consulta.setConduta(conduta);
	    consulta.setStatus(StatusConsulta.REALIZADA);

	    Encaminhamento encaminhamento = consulta.getEncaminhamento();

	    if (encaminhamento.getStatusEncaminhamento()
	            == StatusEncaminhamento.ASSUMIDO) {

	        encaminhamento.setStatusEncaminhamento(
	                StatusEncaminhamento.EM_TRATAMENTO
	        );

	        encaminhamentoRepository.save(encaminhamento);
	    }

	    Consulta consultaSalva = consultaRepository.save(consulta);

	    return toResponseDTO(consultaSalva);
	}
	
	public ConsultaResponseDTO cancelarConsulta(
	        Long consultaId,
	        String observacao) {

	    Consulta consulta = consultaRepository.findById(consultaId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Consulta não encontrada"
	            ));

	    if (consulta.getStatus() != StatusConsulta.AGENDADA) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Somente consultas agendadas podem ser canceladas"
	        );
	    }

	    consulta.setStatus(StatusConsulta.CANCELADA);

	    if (observacao != null && !observacao.isBlank()) {

	        if (consulta.getObservacoes() == null
	                || consulta.getObservacoes().isBlank()) {

	            consulta.setObservacoes(observacao);

	        } else {

	            consulta.setObservacoes(
	                    consulta.getObservacoes()
	                    + "\n"
	                    + observacao
	            );
	        }
	    }

	    Consulta consultaSalva = consultaRepository.save(consulta);

	    return toResponseDTO(consultaSalva);
	}
	
	public ConsultaResponseDTO reagendarConsulta(
			Long consultaId,
			LocalDateTime novaDataHora) {
		
		Consulta consulta = consultaRepository.findById(consultaId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Consulta não encontrada"
						));
		
		if (consulta.getStatus() != StatusConsulta.AGENDADA) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Somente consultas agendadas podem ser reagendadas"
					);
		}
		
		if (novaDataHora == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"A nova data e hora são obrigatórias"
					);
		}
		
		if (novaDataHora.equals(consulta.getDataHora())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"A nova data e hora devem ser diferentes da atual"
					);
		}
		
		if (novaDataHora.isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Não é possível reagendar para uma data ou horário passado"
					);
		}
		
		boolean horarioOcupado =
				consultaRepository.existsByFisioterapeutaIdAndDataHoraAndStatus(
						consulta.getFisioterapeuta().getId(),
						novaDataHora,
						StatusConsulta.AGENDADA
						);
		
		if (horarioOcupado) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"O fisioterapeuta já possui uma consulta agendada neste horário"
					);
		}
		
		consulta.setDataHora(novaDataHora);
		
		Consulta consultaSalva = consultaRepository.save(consulta);
		
		return toResponseDTO(consultaSalva);
	}
	
	public ConsultaResponseDTO registrarFalta(
	        Long consultaId,
	        String observacao) {

	    Consulta consulta = consultaRepository.findById(consultaId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Consulta não encontrada"
	            ));

	    if (consulta.getStatus() != StatusConsulta.AGENDADA) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Somente consultas agendadas podem receber registro de falta"
	        );
	    }

	    consulta.setStatus(StatusConsulta.FALTA);

	    if (observacao != null && !observacao.isBlank()) {

	        if (consulta.getObservacoes() == null
	                || consulta.getObservacoes().isBlank()) {

	            consulta.setObservacoes(observacao);

	        } else {

	            consulta.setObservacoes(
	                    consulta.getObservacoes()
	                    + "\n"
	                    + observacao
	            );
	        }
	    }

	    Consulta consultaSalva = consultaRepository.save(consulta);

	    return toResponseDTO(consultaSalva);
	}
	
	public List<ConsultaResponseDTO> listarTodas(){
		List<Consulta> consultas = consultaRepository.findAll();
		
		return consultas.stream()
				.map(this::toResponseDTO)
				.toList();
	}
	
	public Optional<Consulta> buscarPorId(Long id){
		return consultaRepository.findById(id);
	}
	
	//FILTROS
	public List<ConsultaResponseDTO> buscarPorStatus(StatusConsulta status){
		List<Consulta> consultas = consultaRepository.findByStatus(status);
		
		return consultas.stream()
				.map(this::toResponseDTO)
				.toList();
	}
	
	public List<ConsultaResponseDTO> buscarPorFisioterapeuta(Long fisioterapeutaId){
		Fisioterapeuta fisioterapeuta = fisioterapeutaRepository.findById(fisioterapeutaId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Fisioterapeuta não encontrado !")
				);
		
		
		
		List<Consulta> consultas = consultaRepository.findByFisioterapeutaId(fisioterapeuta.getId());
		
		return consultas.stream()
				.map(this::toResponseDTO)
				.toList();
	} 
	
	public List<ConsultaResponseDTO> buscarPorPaciente(String nome){
		
		if(nome ==null || nome.isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"O nome do paciente deve ser informado !"
				);
		}
		
		List<Consulta> consultas = consultaRepository.findByEncaminhamentoPacienteNomeContainingIgnoreCase(nome);
		
		return consultas.stream()
				.map(this::toResponseDTO)
				.toList();
	}
	
	public List<ConsultaResponseDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim){
		
		if(dataInicio.isAfter(dataFim)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"A data inicial não pode ser maior que a data final !"
				);
		}
		
		LocalDateTime inicio = dataInicio.atStartOfDay();
		LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
		
		List<Consulta> consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
		
		return consultas.stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	public List<ConsultaResponseDTO> buscarAgendaDoDia(LocalDate data){
		
		if(data == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"A data precisa ser definida !"
				);
		}
		
		LocalDateTime inicio = data.atStartOfDay();
		LocalDateTime fim = data.atTime(LocalTime.MAX);
		
		List<Consulta> consultas = consultaRepository.findByDataHoraBetweenOrderByDataHoraAsc(inicio, fim);
		
		 return consultas.stream()
		            .map(this::toResponseDTO)
		            .toList();
	}
	
	public List<ConsultaResponseDTO> buscarAgendaDoDiaFisioterapeuta(Long fisioterapeutaId, LocalDate data){
		
	  Fisioterapeuta fisioterapeuta = fisioterapeutaRepository.findById(fisioterapeutaId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Fisioterapeuta não encontrado"
	            ));
		
		if(data == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"A data precisa ser definida !"
				);
		}
		
		LocalDateTime inicio = data.atStartOfDay();
		LocalDateTime fim = data.atTime(LocalTime.MAX);
		
		List<Consulta> consultas = consultaRepository.
				findByFisioterapeutaIdAndDataHoraBetweenOrderByDataHoraAsc(
						fisioterapeuta.getId(),
						inicio, 
						fim); 
		
		 return consultas.stream()
		            .map(this::toResponseDTO)
		            .toList();
	}
	
	
	//INDICADORES
	public Map<String, Long> obterIndicadores() {

	    Map<String, Long> indicadores = new HashMap<>();

	    indicadores.put(
	            "agendadas",
	            consultaRepository.countByStatus(StatusConsulta.AGENDADA)
	    );

	    indicadores.put(
	            "realizadas",
	            consultaRepository.countByStatus(StatusConsulta.REALIZADA)
	    );

	    indicadores.put(
	            "faltas",
	            consultaRepository.countByStatus(StatusConsulta.FALTA)
	    );

	    indicadores.put(
	            "canceladas",
	            consultaRepository.countByStatus(StatusConsulta.CANCELADA)
	    );

	    indicadores.put(
	            "total",
	            consultaRepository.count()
	    );

	    return indicadores;
	}
	
	public Map<String, Long> obterIndicadoresPorPeriodo(
	        LocalDate dataInicio,
	        LocalDate dataFim) {

	    if (dataInicio == null || dataFim == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Data inicial e data final são obrigatórias"
	        );
	    }

	    if (dataInicio.isAfter(dataFim)) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "A data inicial não pode ser maior que a data final"
	        );
	    }

	    LocalDateTime inicio = dataInicio.atStartOfDay();
	    LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

	    Map<String, Long> indicadores = new HashMap<>();

	    indicadores.put(
	            "agendadas",
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.AGENDADA,
	                    inicio,
	                    fim
	            )
	    );

	    indicadores.put(
	            "realizadas",
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.REALIZADA,
	                    inicio,
	                    fim
	            )
	    );

	    indicadores.put(
	            "faltas",
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.FALTA,
	                    inicio,
	                    fim
	            )
	    );

	    indicadores.put(
	            "canceladas",
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.CANCELADA,
	                    inicio,
	                    fim
	            )
	    );

	    long total =
	            indicadores.get("agendadas")
	            + indicadores.get("realizadas")
	            + indicadores.get("faltas")
	            + indicadores.get("canceladas");

	    indicadores.put("total", total);

	    return indicadores;
	}
	
	public Map<String, Object> obterTaxaFaltasPorPeriodo(
	        LocalDate dataInicio,
	        LocalDate dataFim) {

	    if (dataInicio == null || dataFim == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Data inicial e data final são obrigatórias"
	        );
	    }

	    if (dataInicio.isAfter(dataFim)) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "A data inicial não pode ser maior que a data final"
	        );
	    }

	    LocalDateTime inicio = dataInicio.atStartOfDay();
	    LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

	    long realizadas =
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.REALIZADA,
	                    inicio,
	                    fim
	            );

	    long faltas =
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.FALTA,
	                    inicio,
	                    fim
	            );

	    long canceladas =
	            consultaRepository.countByStatusAndDataHoraBetween(
	                    StatusConsulta.CANCELADA,
	                    inicio,
	                    fim
	            );

	    long atendimentosEsperados = realizadas + faltas;

	    double taxaFaltas = 0.0;

	    if (atendimentosEsperados > 0) {
	        taxaFaltas = ((double) faltas / atendimentosEsperados) * 100;
	    }

	    Map<String, Object> indicadores = new HashMap<>();

	    indicadores.put("realizadas", realizadas);
	    indicadores.put("faltas", faltas);
	    indicadores.put("canceladas", canceladas);
	    indicadores.put("atendimentos esperados", atendimentosEsperados);
	    indicadores.put("taxaFaltas", taxaFaltas);

	    return indicadores;
	}
	
	public Map<String, Long> obterIndicadoresPorFisioterapeuta(Long fisioterapeutaId){
		
		Fisioterapeuta fisioterapeuta = fisioterapeutaRepository.findById(fisioterapeutaId).orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Fisioterapeuta não encaontrado !")
				);
		
		Map<String, Long> indicadores = new HashMap<>();
		
		indicadores.put("agendadas", consultaRepository.countByFisioterapeutaIdAndStatus(fisioterapeuta.getId(), StatusConsulta.AGENDADA));
		
		indicadores.put("realizadas", consultaRepository.countByFisioterapeutaIdAndStatus(fisioterapeuta.getId(), StatusConsulta.REALIZADA));
		
		indicadores.put("faltas", consultaRepository.countByFisioterapeutaIdAndStatus(fisioterapeuta.getId(), StatusConsulta.FALTA));
		
		indicadores.put("canceladas", consultaRepository.countByFisioterapeutaIdAndStatus(fisioterapeuta.getId(), StatusConsulta.CANCELADA));
		
		Long total = indicadores.get("agendadas") + indicadores.get("realizadas") + indicadores.get("faltas") + indicadores.get("canceladas");
		
		indicadores.put("total", total);
		
		return indicadores;
		
	}
	
	public Map<String, Object> obterEstatisticasPorFisioterapeutaEPeriodo(
	        Long fisioterapeutaId,
	        LocalDate dataInicio,
	        LocalDate dataFim) {

	    Fisioterapeuta fisioterapeuta = fisioterapeutaRepository
	            .findById(fisioterapeutaId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Fisioterapeuta não encontrado"
	            ));

	    if (dataInicio == null || dataFim == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Data inicial e data final são obrigatórias"
	        );
	    }

	    if (dataInicio.isAfter(dataFim)) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "A data inicial não pode ser maior que a data final"
	        );
	    }

	    LocalDateTime inicio = dataInicio.atStartOfDay();
	    LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

	    long agendadas =
	            consultaRepository
	                    .countByFisioterapeutaIdAndStatusAndDataHoraBetween(
	                            fisioterapeuta.getId(),
	                            StatusConsulta.AGENDADA,
	                            inicio,
	                            fim
	                    );

	    long realizadas =
	            consultaRepository
	                    .countByFisioterapeutaIdAndStatusAndDataHoraBetween(
	                            fisioterapeuta.getId(),
	                            StatusConsulta.REALIZADA,
	                            inicio,
	                            fim
	                    );

	    long faltas =
	            consultaRepository
	                    .countByFisioterapeutaIdAndStatusAndDataHoraBetween(
	                            fisioterapeuta.getId(),
	                            StatusConsulta.FALTA,
	                            inicio,
	                            fim
	                    );

	    long canceladas =
	            consultaRepository
	                    .countByFisioterapeutaIdAndStatusAndDataHoraBetween(
	                            fisioterapeuta.getId(),
	                            StatusConsulta.CANCELADA,
	                            inicio,
	                            fim
	                    );

	    long total = agendadas + realizadas + faltas + canceladas;

	    long atendimentosEsperados = realizadas + faltas;

	    double taxaFaltas = 0.0;

	    if (atendimentosEsperados > 0) {
	        taxaFaltas = ((double) faltas / atendimentosEsperados) * 100;
	    }

	    Map<String, Object> estatisticas = new HashMap<>();

	    estatisticas.put("agendadas", agendadas);
	    estatisticas.put("realizadas", realizadas);
	    estatisticas.put("faltas", faltas);
	    estatisticas.put("canceladas", canceladas);
	    estatisticas.put("total", total);
	    estatisticas.put("taxaFaltas", taxaFaltas);

	    return estatisticas;
	}
	
	private ConsultaResponseDTO toResponseDTO(Consulta consulta) {

	    return new ConsultaResponseDTO(
	            consulta.getId(),
	            consulta.getDataHora(),
	            consulta.getStatus(),

	            consulta.getEncaminhamento().getId(),

	            consulta.getEncaminhamento()
	                    .getPaciente()
	                    .getId(),

	            consulta.getEncaminhamento()
	                    .getPaciente()
	                    .getNome(),

	            consulta.getFisioterapeuta().getId(),

	            consulta.getFisioterapeuta().getNome(),

	            consulta.getObservacoes(),
	            consulta.getDiagnostico(),
	            consulta.getProcedimentos(),
	            consulta.getConduta()
	    );
	}
	
}
