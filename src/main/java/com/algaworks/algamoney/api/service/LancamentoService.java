package com.algaworks.algamoney.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.repository.LancamentoRepository;
import com.algaworks.algamoney.api.repository.PessoaRepository;
import com.algaworks.algamoney.api.service.exception.PessoaInvalidaException;


@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository repository;
	
	@Autowired
	private PessoaRepository pessoas;
	
	public List<Lancamento> findAll(){
		return repository.findAll();
	}
	
	//save and update
	public Lancamento save(Lancamento lancamento) {
		Pessoa pessoa = pessoas.findOne(lancamento.getPessoa().getId());
		//se pessoa for nulo ou inativo
		if(pessoa == null || !pessoa.isAtivo()){
			throw new PessoaInvalidaException();
		}
		if(lancamento.getId() != null){
			//verificar se o lancamento existe
			findOne(lancamento.getId());
		}
		return repository.save(lancamento);
	}
	
	public Lancamento findOne(Long id){
		Lancamento lancamento = repository.findOne(id);
		if (lancamento == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return lancamento;
	}
	
}
