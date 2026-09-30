package com.fisio.fisiosystem.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fisio.fisiosystem.dto.EncaminhamentoResponseDTO;
import com.fisio.fisiosystem.entity.Encaminhamento;
import com.fisio.fisiosystem.entity.Fisioterapeuta;
import com.fisio.fisiosystem.entity.Paciente;
import com.fisio.fisiosystem.entity.enums.Prioridade;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;
import com.fisio.fisiosystem.repository.EncaminhamentoRepository;
import com.fisio.fisiosystem.repository.FisioterapeutaRepository;
import com.fisio.fisiosystem.repository.PacienteRepository;

@Service
public class EncaminhamentoService {
	private final EncaminhamentoRepository encaminhamentoRepository;
	private final PacienteRepository pacienteRepository;
	private final FisioterapeutaRepository fisioterapeutaRepository;
	
	public EncaminhamentoService(EncaminhamentoRepository encaminhamentoRepository, PacienteRepository pacienteRepository, FisioterapeutaRepository fisioterapeutaRepository) {
		this.encaminhamentoRepository = encaminhamentoRepository;
		this.pacienteRepository = pacienteRepository;
		this.fisioterapeutaRepository = fisioterapeutaRepository;
	}
	
	//SALVAR ENCAMINHAMENTO
	public Encaminhamento salvar(Encaminhamento encaminhamento) {
		Long pacienteId = encaminhamento.getPaciente().getId();
		
		Paciente paciente = pacienteRepository.findById(pacienteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado !"));
		
		encaminhamento.setPaciente(paciente);
		
		return encaminhamentoRepository.save(encaminhamento);
	}
	
	//LISTAR TODOS
	public List<EncaminhamentoResponseDTO> listarTodos() {

	    return encaminhamentoRepository.findAll()
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	//BUSCART POR ID
	public EncaminhamentoResponseDTO buscarPorId(Long id) {

	    Encaminhamento encaminhamento =
	            encaminhamentoRepository.findById(id)
	                    .orElseThrow(() -> new ResponseStatusException(
	                            HttpStatus.NOT_FOUND,
	                            "Encaminhamento não encontrado"
	                    ));

	    return toResponseDTO(encaminhamento);
	}
	
	//ASSUMIR ENCAMINHAMENTO
	public Encaminhamento assumirEncaminhamento(Long encaminhamentoId, Long fisioterapeutaId) {
		Encaminhamento encaminhamento = encaminhamentoRepository.findById(encaminhamentoId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encaminhamento não encontrado !"));
		
		Fisioterapeuta fisioterapeuta = fisioterapeutaRepository.findById(fisioterapeutaId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fisioterapeuta não encontrado !"));
		
		if(encaminhamento.getStatusEncaminhamento() != StatusEncaminhamento.NA_FILA) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O encaminhamento não está disponivel na fila !");
		}
		
		if(!fisioterapeuta.isAtivo()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fisioterapeuta está inativo"); 
		}
		
		encaminhamento.setFisioterapeutaResponsavel(fisioterapeuta);
		encaminhamento.setDataAssuncao(LocalDate.now());
		encaminhamento.setStatusEncaminhamento(StatusEncaminhamento.ASSUMIDO);
		
		return encaminhamentoRepository.save(encaminhamento);
	}
	
	//RETIRAR ENCAMIHAMENTO
	public Encaminhamento retirarEncaminhamento(Long id, String motivoRetirada) {
		Encaminhamento encaminhamento = encaminhamentoRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encaminhamento não encontrado !"));
		
		encaminhamento.setMotivoRetirada(motivoRetirada);
		encaminhamento.setStatusEncaminhamento(StatusEncaminhamento.RETIRADO_PELO_PACIENTE);
		encaminhamento.setDataRetirada(LocalDate.now());
		
		return encaminhamentoRepository.save(encaminhamento);
	
	}
	
	//DAR ALTA 
	public Encaminhamento darAlta(Long id) {
		Encaminhamento encaminhamento = encaminhamentoRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encaminhamento não encontrado !")
				);
		
		if(encaminhamento.getStatusEncaminhamento() != StatusEncaminhamento.EM_TRATAMENTO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente encaminhamentos em tratamento podem receber alta");
		}
		
		encaminhamento.setStatusEncaminhamento(StatusEncaminhamento.ALTA);
		encaminhamento.setDataAlta(LocalDate.now());
		
		return encaminhamentoRepository.save(encaminhamento);
	}
	
	//FILA DE ENCAMINHAMENTOS
	public List<EncaminhamentoResponseDTO> listarFila() {

	    return encaminhamentoRepository
	            .buscarFilaOrdenada(StatusEncaminhamento.NA_FILA)
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	//FILTROS
	public List<EncaminhamentoResponseDTO> buscarPorStatus(
	        StatusEncaminhamento status) {

	    return encaminhamentoRepository
	            .findByStatusEncaminhamento(status)
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	public List<EncaminhamentoResponseDTO> buscarPorPrioridade(
	        Prioridade prioridade) {

	    return encaminhamentoRepository
	            .findByPrioridade(prioridade)
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	public List<EncaminhamentoResponseDTO> buscarPorPaciente(String nome) {

	    if (nome == null || nome.isBlank()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Nome do paciente é obrigatório"
	        );
	    }

	    return encaminhamentoRepository
	            .findByPacienteNomeContainingIgnoreCase(nome)
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	public List<EncaminhamentoResponseDTO> buscarPorPeriodo(
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

	    return encaminhamentoRepository
	            .findByDataEntregaBetween(dataInicio, dataFim)
	            .stream()
	            .map(this::toResponseDTO)
	            .toList();
	}
	
	//INDICADORES
	public Map<String, Long> obterIndicadoresDeStatus() {

	    Map<String, Long> indicadores = new HashMap<>();

	    indicadores.put(
	            "naFila",
	            encaminhamentoRepository.countByStatusEncaminhamento(
	                    StatusEncaminhamento.NA_FILA
	            )
	    );

	    indicadores.put(
	            "assumidos",
	            encaminhamentoRepository.countByStatusEncaminhamento(
	                    StatusEncaminhamento.ASSUMIDO
	            )
	    );

	    indicadores.put(
	            "emTratamento",
	            encaminhamentoRepository.countByStatusEncaminhamento(
	                    StatusEncaminhamento.EM_TRATAMENTO
	            )
	    );

	    indicadores.put(
	            "altas",
	            encaminhamentoRepository.countByStatusEncaminhamento(
	                    StatusEncaminhamento.ALTA
	            )
	    );

	    indicadores.put(
	            "retirados",
	            encaminhamentoRepository.countByStatusEncaminhamento(
	                    StatusEncaminhamento.RETIRADO_PELO_PACIENTE
	            )
	    );

	    indicadores.put(
	            "total",
	            encaminhamentoRepository.count()
	    );

	    return indicadores;
	}
	
	public Map<String, Long> ObterIndicadoresDePrioridade(){
		
		Map<String, Long> indicadores = new HashMap<>();
		
		indicadores.put(
				"primaria", 
				encaminhamentoRepository.countByPrioridade(Prioridade.PRIMARIA)
		);
		
		indicadores.put(
				"secundaria", 
				encaminhamentoRepository.countByPrioridade(Prioridade.SECUNDARIA)
		);
		
		indicadores.put(
				"terciaria", 
				encaminhamentoRepository.countByPrioridade(Prioridade.TERCIARIA)
		);
		
		indicadores.put(
				"total",
				encaminhamentoRepository.count()
		);
		
		return indicadores;
	}
	
	public Map<String, Long> ObterIndicadoresPrioridadeDaFila(){
		
		Map<String, Long> indicadores = new HashMap<>();
		
		indicadores.put(
				"primaria",
				encaminhamentoRepository.countByStatusEncaminhamentoAndPrioridade(
						StatusEncaminhamento.NA_FILA,
						Prioridade.PRIMARIA)
		);
		
		indicadores.put(
				"secundaria",
				encaminhamentoRepository.countByStatusEncaminhamentoAndPrioridade(
						StatusEncaminhamento.NA_FILA,
						Prioridade.SECUNDARIA)
		);
		
		indicadores.put(
				"terciaria",
				encaminhamentoRepository.countByStatusEncaminhamentoAndPrioridade(
						StatusEncaminhamento.NA_FILA,
						Prioridade.TERCIARIA)
		);
		
		return indicadores;
	}
	
	public double calcularTempoMedioEspera() {

	    List<Encaminhamento> encaminhamentos =
	            encaminhamentoRepository.findByDataAssuncaoIsNotNull();

	    long totalDias = 0;
	    int quantidadeValidos = 0;

	    for (Encaminhamento encaminhamento : encaminhamentos) {

	        if (encaminhamento.getDataEntrega() == null) {
	            continue;
	        }

	        long dias = ChronoUnit.DAYS.between(
	                encaminhamento.getDataEntrega(),
	                encaminhamento.getDataAssuncao()
	        );

	        if (dias >= 0) {
	            totalDias += dias;
	            quantidadeValidos++;
	        }
	    }

	    if (quantidadeValidos == 0) {
	        return 0.0;
	    }

	    return (double) totalDias / quantidadeValidos;
	}
	
	private EncaminhamentoResponseDTO toResponseDTO(
	        Encaminhamento encaminhamento) {

	    Long fisioterapeutaId = null;
	    String fisioterapeutaNome = null;

	    if (encaminhamento.getFisioterapeutaResponsavel() != null) {

	        fisioterapeutaId =
	                encaminhamento.getFisioterapeutaResponsavel().getId();

	        fisioterapeutaNome =
	                encaminhamento.getFisioterapeutaResponsavel().getNome();
	    }

	    return new EncaminhamentoResponseDTO(
	            encaminhamento.getId(),
	            encaminhamento.getDataEntrega(),

	            encaminhamento.getPaciente().getId(),
	            encaminhamento.getPaciente().getNome(),

	            encaminhamento.getMedicoSolicitante(),
	            encaminhamento.getTipoAtendimento(),
	            encaminhamento.getStatusEncaminhamento(),
	            encaminhamento.getPrioridade(),

	            fisioterapeutaId,
	            fisioterapeutaNome,

	            encaminhamento.getDataAssuncao(),
	            encaminhamento.getDataRetirada(),
	            encaminhamento.getMotivoRetirada(),
	            encaminhamento.getDataAlta(),

	            encaminhamento.getPatologia(),
	            encaminhamento.getObservacoes()
	    );
	}
	
	
}
