package com.fisio.fisiosystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Fisioterapeuta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String crefito;
	private String nome;
	private String telefone;
	private Boolean ativo = true;
	
	public Fisioterapeuta() {
		
	}
	
	public long getId() {
		return id;
	}
	
	public String getCrefito() {
		return crefito;
	}
	
	public void setCrefito(String crefito) {
		this.crefito = crefito;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
