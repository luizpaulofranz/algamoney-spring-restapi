package com.algaworks.algamoney.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.algamoney.api.model.Pessoa;

//passamos o tipo da classe e o tipo da chave primaria
//o tipo da chave primaria eh para os metodos findOneById
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable);
	
}
