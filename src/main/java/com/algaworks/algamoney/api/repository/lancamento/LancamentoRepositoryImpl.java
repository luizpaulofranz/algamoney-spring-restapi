package com.algaworks.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;

//Essa classe que extende as funcionalidades nativas do JPA
public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Lancamento> filter(LancamentoFilter filter, Pageable page) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		//createQuery indica o tipo de registro que vamos criar
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// predicates sao gerados a partir do filter
		Predicate[] predicates = createPredicades(filter, builder, root);

		criteria.where(predicates);
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		//tratar paginacao
		this.addPagination(query, page);
		
		return new PageImpl<>(query.getResultList(), page, total(filter));
	}

	private Predicate[] createPredicades(LancamentoFilter filter, CriteriaBuilder builder, Root<Lancamento> root) {

		List<Predicate> predicates = new ArrayList<>();

		if (filter.getDescricao() != null) {
			predicates.add(builder.like(
					// o root pega o campo da base no qual vamos fazer o where
					// para nao precisarmos digitar esses campos, podemos usar o
					// metamodel, aula 5.7
					builder.lower(root.get("descricao")),
					// aqui o valor filtrado
					"%" + filter.getDescricao().toLowerCase() + "%"));

		}

		if (filter.getInitDate() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("dataVencimento"), filter.getInitDate()));
		}

		if (filter.getFinishDate() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("dataVencimento"), filter.getFinishDate()));
		}

		// convertemos para array
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	//trata a paginacao
	private void addPagination(TypedQuery<Lancamento> query, Pageable pageable) {
		int currentPage = pageable.getPageNumber();
		int totalPerPage = pageable.getPageSize();
		int firstRow = currentPage * totalPerPage;

		query.setFirstResult(firstRow);
		query.setMaxResults(totalPerPage);
	}
	
	//to get the total results from current filter
	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		//createQuery indica o tipo de registro que vamos criar
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		//indicamos o from
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = createPredicades(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
}

}
