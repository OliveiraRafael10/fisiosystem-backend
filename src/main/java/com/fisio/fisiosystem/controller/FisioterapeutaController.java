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

import com.fisio.fisiosystem.entity.Fisioterapeuta;
import com.fisio.fisiosystem.service.FisioterapeutaService;

@RestController
@RequestMapping("/fisioterapeuta")
public class FisioterapeutaController {
	private final FisioterapeutaService fisioterapeutaService;
	
	public FisioterapeutaController(FisioterapeutaService fisioterapeutaService) {
		this.fisioterapeutaService  = fisioterapeutaService; 
	}
	
	@PostMapping
	public Fisioterapeuta salvar(@RequestBody Fisioterapeuta fisioterapeuta) {
		return fisioterapeutaService.salvar(fisioterapeuta);
	}
	
	@GetMapping
	public List<Fisioterapeuta> listar(){
		return fisioterapeutaService.listarTodos();
	}
	
	@GetMapping("/{id}")
    public Fisioterapeuta buscarPorId(@PathVariable Long id) {
        return fisioterapeutaService.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<Fisioterapeuta> buscarPorNome( @RequestParam String nome) {

        return fisioterapeutaService.buscarPorNome(nome);
    }

    @PutMapping("/{id}")
    public Fisioterapeuta atualizar(@PathVariable Long id, @RequestBody Fisioterapeuta fisioterapeuta) {

        return fisioterapeutaService.atualizar(id, fisioterapeuta);
    }

    @PutMapping("/{id}/inativar")
    public Fisioterapeuta inativar(@PathVariable Long id) {
        return fisioterapeutaService.inativar(id);
    }

    @PutMapping("/{id}/ativar")
    public Fisioterapeuta ativar(@PathVariable Long id) {
        return fisioterapeutaService.ativar(id);
    }

}
