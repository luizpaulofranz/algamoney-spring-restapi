package com.algaworks.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algamoney.api.model.Cidade;
import com.algaworks.algamoney.api.repository.CidadeRepository;

@RestController
@RequestMapping("/cidades")
public class CidadeResource {
	
	@Autowired
	CidadeRepository repository;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<Cidade> find (@RequestParam Long idEstado) {
		return repository.findByEstadoId(idEstado);
	}
	
}
