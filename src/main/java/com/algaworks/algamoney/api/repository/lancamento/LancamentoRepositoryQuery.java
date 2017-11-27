package com.algaworks.algamoney.api.repository.lancamento;

import java.util.List;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;

//This contract assigns what query methods we will create
public interface LancamentoRepositoryQuery {

	//assinamos um metodo que desejamos extender ao JPA nativo
	//esse metodo eh implementado pela classe Impl
	public List<Lancamento> filter(LancamentoFilter filter);
	
}
