package com.fisio.fisiosystem.entity;

import java.time.LocalDate;

import com.fisio.fisiosystem.entity.enums.Prioridade;
import com.fisio.fisiosystem.entity.enums.StatusEncaminhamento;
import com.fisio.fisiosystem.entity.enums.TipoAtendimento;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Encaminhamento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDate dataEntrega;
	
	@ManyToOne
	@JoinColumn(name = "paciente_id")
	private Paciente paciente;
	
	@ManyToOne
	@JoinColumn(name = "fisioterapeuta_responsavel_id")
	private Fisioterapeuta fisioterapeutaResponsavel;
	
	private LocalDate dataAssuncao; 
	

	private String medicoSolicitante;
	
	@Enumerated(EnumType.STRING)
	private TipoAtendimento tipoAtendimento;
	
	@Enumerated(EnumType.STRING)
	private StatusEncaminhamento statusEncaminhamento;
		
	private LocalDate dataRetirada;
	
	private String motivoRetirada;
	
	@Enumerated(EnumType.STRING)
	private Prioridade prioridade;
	
	private String patologia;
	
	private String observacoes;
	
	private LocalDate dataAlta;

	public Encaminhamento(){
		
	}

	public long getId() {
		return id;
	}

	public LocalDate getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(LocalDate dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	
	public String getMedicoSolicitante() {
		return medicoSolicitante;
	}
	
	public void setMedicoSolicitante(String medicoSolicitante) {
		this.medicoSolicitante = medicoSolicitante;
	}
	
	public LocalDate getDataRetirada() {
		return dataRetirada;
	}
	
	public void setDataRetirada(LocalDate dataRetirada) {
		this.dataRetirada = dataRetirada;
	}
	
	public String getPatologia() {
		return patologia;
	}
	
	public void setPatologia(String patologia) {
		this.patologia = patologia;
	}
	
	public String getObservacoes() {
		return observacoes;
	}
	
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	
	public Fisioterapeuta getFisioterapeutaResponsavel() {
		return fisioterapeutaResponsavel;
	}
	
	public void setFisioterapeutaResponsavel(Fisioterapeuta fisioterapeuta) {
		this.fisioterapeutaResponsavel = fisioterapeuta;
	}
	
	public LocalDate getDataAssuncao() {
		return dataAssuncao;
	}
	
	public void setDataAssuncao(LocalDate dataAssuncao) {
		this.dataAssuncao = dataAssuncao;
	}
	
	public String getMotivoRetirada() {
		return motivoRetirada;
	}
	
	public void setMotivoRetirada(String motivoRetirada) {
		this.motivoRetirada = motivoRetirada;
	}

	public TipoAtendimento getTipoAtendimento() {
		return tipoAtendimento;
	}

	public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}

	public StatusEncaminhamento getStatusEncaminhamento() {
		return statusEncaminhamento;
	}

	public void setStatusEncaminhamento(StatusEncaminhamento statusEncaminhamento) {
		this.statusEncaminhamento = statusEncaminhamento;
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}
	
	public LocalDate getDataAlta() {
		return dataAlta;
	}
	
	public void setDataAlta(LocalDate dataAlta) {
		this.dataAlta = dataAlta;
	}
}
