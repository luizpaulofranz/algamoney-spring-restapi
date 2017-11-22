package com.algaworks.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoService service;
	
	@GetMapping
	public List<Lancamento> listar(){
		return this.service.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Lancamento> getById(@PathVariable Long id){
		Lancamento lancamento = service.findOne(id);
		return ResponseEntity.ok().body(lancamento);
	}

}
