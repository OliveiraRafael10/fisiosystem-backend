package com.fisio.fisiosystem.entity;

import java.time.LocalDateTime;

import com.fisio.fisiosystem.entity.enums.StatusConsulta;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Consulta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime dataHora;
	
	@ManyToOne
	@JoinColumn(name = "fisioterapeuta_id")
	private Fisioterapeuta fisioterapeuta;
	
	@ManyToOne
	@JoinColumn(name = "encaminhamento_id")
	private Encaminhamento encaminhamento;
	
	@Enumerated(EnumType.STRING)
	private StatusConsulta status;
	
	private String observacoes;
	
	private String diagnostico;

	private String procedimentos;

	private String conduta;
	

	public Consulta() {
		
	}

	public Long getId() {
		return id;
	}
	
	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public Fisioterapeuta getFisioterapeuta() {
		return fisioterapeuta;
	}

	public void setFisioterapeuta(Fisioterapeuta fisioterapeuta) {
		this.fisioterapeuta = fisioterapeuta;
	}

	public Encaminhamento getEncaminhamento() {
		return encaminhamento;
	}

	public void setEncaminhamento(Encaminhamento encaminhamento) {
		this.encaminhamento = encaminhamento;
	}

	public StatusConsulta getStatus() {
		return status;
	}

	public void setStatus(StatusConsulta status) {
		this.status = status;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	
	public String getDiagnostico() {
		return diagnostico;
	}
	
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	
	public String getProcedimentos() {
		return procedimentos;
	}
	
	public void setProcedimentos(String procedimentos) {
		this.procedimentos = procedimentos;
	}
	
	public String getConduta() {
		return conduta;
	}
	
	public void setConduta(String conduta) {
		this.conduta = conduta;
	}
}
