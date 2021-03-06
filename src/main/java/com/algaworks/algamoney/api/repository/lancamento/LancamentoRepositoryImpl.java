package com.algaworks.algamoney.api.repository.lancamento;

import java.time.LocalDate;
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

import com.algaworks.algamoney.api.dto.LancamentoCategoria;
import com.algaworks.algamoney.api.dto.LancamentoDia;
import com.algaworks.algamoney.api.dto.LancamentoPessoa;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;
import com.algaworks.algamoney.api.repository.projection.ResumoLancamento;

//Essa classe que extende as funcionalidades nativas do JPA
public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Lancamento> filter(LancamentoFilter filter, Pageable page) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// createQuery indica o tipo de registro que vamos criar
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// predicates sao gerados a partir do filter
		Predicate[] predicates = createPredicades(filter, builder, root);

		criteria.where(predicates);
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		// tratar paginacao
		this.addPagination(query, page);

		return new PageImpl<>(query.getResultList(), page, total(filter));
	}

	@Override
	// retorna uma versao resumida do resource lancamento
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// o generics da criteria contem o retorno
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		// o generics do root eh o "from"
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// damos os gets diretamente nas strings q representam os campos
		criteria.select(builder.construct(ResumoLancamento.class, root.get("id"), root.get("descricao"),
				root.get("dataVencimento"), root.get("dataPagamento"), root.get("valor"), root.get("tipo"),
				root.get("categoria").get("nome"), root.get("pessoa").get("nome")));

		Predicate[] predicates = createPredicades(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		addPagination(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
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

	// trata a paginacao
	private void addPagination(TypedQuery<?> query, Pageable pageable) {
		int currentPage = pageable.getPageNumber();
		int totalPerPage = pageable.getPageSize();
		int firstRow = currentPage * totalPerPage;

		query.setFirstResult(firstRow);
		query.setMaxResults(totalPerPage);
	}

	// to get the total results from current filter
	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// createQuery indica o tipo de registro que vamos criar
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		// indicamos o from
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = createPredicades(lancamentoFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

	// para relatorios, Total Lancamentos por Categoria
	@Override
	public List<LancamentoCategoria> porCategoria(LocalDate mesReferencia) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoCategoria> criteriaQuery = criteriaBuilder.createQuery(LancamentoCategoria.class);

		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

		criteriaQuery.select(criteriaBuilder.construct(LancamentoCategoria.class, root.get("categoria"),
				criteriaBuilder.sum(root.get("valor"))));

		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), ultimoDia));

		criteriaQuery.groupBy(root.get("categoria"));

		TypedQuery<LancamentoCategoria> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	// para relatorios, Total Lancamentos por dia e por tipo RECEITA DESPESA 
	@Override
	public List<LancamentoDia> porDia(LocalDate mesReferencia) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoDia> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoDia.class);

		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

		criteriaQuery.select(criteriaBuilder.construct(LancamentoDia.class, root.get("tipo"),
				root.get("dataVencimento"), criteriaBuilder.sum(root.get("valor"))));

		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), ultimoDia));

		criteriaQuery.groupBy(root.get("tipo"), root.get("dataVencimento"));

		TypedQuery<LancamentoDia> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}
	
	@Override
	public List<LancamentoPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoPessoa> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoPessoa.class);

		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

		criteriaQuery.select(criteriaBuilder.construct(LancamentoPessoa.class, root.get("tipo"),
				root.get("pessoa"), criteriaBuilder.sum(root.get("valor"))));


		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), inicio),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), fim));

		criteriaQuery.groupBy(root.get("tipo"), root.get("pessoa"));

		TypedQuery<LancamentoPessoa> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

}
