package com.algaworks.algamoney.api.resource;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	@PostMapping
	public ResponseEntity<Lancamento> insert(@Valid @RequestBody Lancamento lancamento){
		Lancamento saved = service.save(lancamento);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();

		return ResponseEntity.created(uri).body(saved);
	}

}
