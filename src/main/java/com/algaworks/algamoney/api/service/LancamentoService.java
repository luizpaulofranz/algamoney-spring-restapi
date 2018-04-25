package com.algaworks.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.algaworks.algamoney.api.dto.LancamentoCategoria;
import com.algaworks.algamoney.api.dto.LancamentoDia;
import com.algaworks.algamoney.api.dto.LancamentoPessoa;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.repository.LancamentoRepository;
import com.algaworks.algamoney.api.repository.PessoaRepository;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;
import com.algaworks.algamoney.api.repository.projection.ResumoLancamento;
import com.algaworks.algamoney.api.service.exception.PessoaInvalidaException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository repository;

	@Autowired
	private PessoaRepository pessoas;
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {
		List<LancamentoPessoa> dados = repository.porPessoa(inicio, fim);
		Map<String, Object> params = new HashMap<>();
		// DT_INICIO foi definido no jasper reports
		params.put("DT_INICIO", Date.valueOf(inicio));
		params.put("DT_FIM", Date.valueOf(fim));
		params.put("REPORT_LOCALE", new Locale("pt", "BR"));
		// importamos o relatorio jasper
		InputStream stream = this.getClass().getResourceAsStream("/relatorios/lancamentos_pessoa.jasper");
		// jasper esta no pom.xml
		JasperPrint print = JasperFillManager.fillReport(
				stream, params, 
				new JRBeanCollectionDataSource(dados)
			);
		return JasperExportManager.exportReportToPdf(print);
	}

	/* Apply the filter */
	public Page<Lancamento> list(LancamentoFilter filter, Pageable page) {
		return repository.filter(filter, page);
	}

	/* Lista os lancamentos e apresenta um JSON resumido */
	public Page<ResumoLancamento> listResume(LancamentoFilter filter, Pageable page) {
		return repository.resumir(filter, page);
	}
	
	public List<LancamentoCategoria> porCategoria(){
		return this.repository.porCategoria(LocalDate.now());
	}
	
	public List<LancamentoDia> porDia(){
		return this.repository.porDia(LocalDate.now());
	}

	// save and update
	public Lancamento save(Lancamento lancamento) {
		Pessoa pessoa = pessoas.findOne(lancamento.getPessoa().getId());
		// se pessoa for nulo ou inativo
		if (pessoa == null || !pessoa.isAtivo()) {
			throw new PessoaInvalidaException();
		}
		if (lancamento.getId() != null) {
			// verificar se o lancamento existe
			findOne(lancamento.getId());
		}
		return repository.save(lancamento);
	}

	public Lancamento findOne(Long id) {
		Lancamento lancamento = repository.findOne(id);
		if (lancamento == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return lancamento;
	}

	public void delete(Long id) {
		Lancamento lancamento = findOne(id);
		repository.delete(lancamento);
	}

	public Lancamento atualizar(Long id, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(id);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		lancamento.setId(lancamentoSalvo.getId());
		//BeanUtils.copyProperties(lancamento, lancamentoSalvo, "id");

		return repository.save(lancamento);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if (lancamento.getPessoa().getId() != null) {
			pessoa = pessoas.findOne(lancamento.getPessoa().getId());
		}

		if (pessoa == null || !pessoa.isAtivo()) {
			throw new PessoaInvalidaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lancamentoSalvo = repository.findOne(codigo);
		if (lancamentoSalvo == null) {
			throw new IllegalArgumentException();
		}
		return lancamentoSalvo;
	}

}
