package com.algaworks.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.repository.lancamento.LancamentoRepositoryQuery;

//a segunda interface fui eu que criei, assim que extendemos as funcionalidades do JPA
//temos de declarar uma interface extender ela aqui, e criar uma classe para implementar ela
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{

}
