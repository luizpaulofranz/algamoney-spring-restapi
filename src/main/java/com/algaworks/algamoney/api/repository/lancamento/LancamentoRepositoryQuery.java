package com.algaworks.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.algamoney.api.dto.LancamentoCategoria;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;
import com.algaworks.algamoney.api.repository.projection.ResumoLancamento;

//This contract assigns what query methods we will create
public interface LancamentoRepositoryQuery {

	//assinamos um metodo que desejamos extender ao JPA nativo
	//esse metodo eh implementado pela classe Impl
	public Page<Lancamento> filter(LancamentoFilter filter, Pageable page);
	//aqui criamos um metodo que retorna um json resumido
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public List<LancamentoCategoria> porCategoria(LocalDate mesReferencia);
	
}
