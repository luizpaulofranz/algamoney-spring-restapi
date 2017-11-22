package com.algaworks.algamoney.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.repository.PessoaRepository;


@Service
public class PessoaService {
	
	@Autowired
	PessoaRepository pessoas;
	
	public Pessoa findById(Long id) throws EmptyResultDataAccessException {
		// metodo padroa do Spring Data JPA
		Pessoa pessoa = pessoas.findOne(id);
		// se nao existe livro, lanca excecao
		if (pessoa == null) {
			throw new EmptyResultDataAccessException(1);
		}

		return pessoa;
	}
	
	public void update(Pessoa pessoa) {
		verificarExistencia(pessoa);
		pessoas.save(pessoa);
	}
	
	public void updateStatus(Long id, Boolean status){
		Pessoa pessoa = this.findById(id);
		pessoa.setAtivo(status);
		pessoas.save(pessoa);
	}

	// esse metodo eh usado para evitar que alguem faca update em uma pessoa q nao
	// existe
	private void verificarExistencia(Pessoa pessoa) {
		findById(pessoa.getId());
	}

}
