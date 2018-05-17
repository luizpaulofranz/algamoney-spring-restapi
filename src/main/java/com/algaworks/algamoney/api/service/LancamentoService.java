package com.algaworks.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.algaworks.algamoney.api.dto.LancamentoCategoria;
import com.algaworks.algamoney.api.dto.LancamentoDia;
import com.algaworks.algamoney.api.dto.LancamentoPessoa;
import com.algaworks.algamoney.api.mail.Mailer;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Pessoa;
import com.algaworks.algamoney.api.model.Usuario;
import com.algaworks.algamoney.api.repository.LancamentoRepository;
import com.algaworks.algamoney.api.repository.PessoaRepository;
import com.algaworks.algamoney.api.repository.UsuarioRepository;
import com.algaworks.algamoney.api.repository.filter.LancamentoFilter;
import com.algaworks.algamoney.api.repository.projection.ResumoLancamento;
import com.algaworks.algamoney.api.service.exception.PessoaInvalidaException;
import com.algaworks.algamoney.api.storage.S3;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

	@Autowired
	private LancamentoRepository repository;

	@Autowired
	private PessoaRepository pessoas;
	@Autowired
	private UsuarioRepository usuarios;
	@Autowired
	private Mailer mailer;
	@Autowired
	private S3 s3;
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	/*pode ser fixedDelay = 1000 (ms) executa esse metodo a cada 1 segundo, o timer so conta a partir do termino da execucao anterior
	 "cron" recebe uma expressao cron unix, nesse caso toda 6 hrs da manha 
	 Entao, toda manha o sistema verifica lancamentos vencidos e alerta os usuarios*/
	@Scheduled(cron="0 0 6 * * *")
	//@Scheduled(fixedDelay= 1000 * 10) - nesse caso a cada 10 segundos
	public void alertLancamentoVencido() {
		// como eh uma tarefa assincrona, usamos o logger para sabermos quando executou
		if (logger.isDebugEnabled()) {
			logger.debug("Preparando envio de "
					+ "e-mails de aviso de lançamentos vencidos.");
		}
		// recuperamos a lista de lancamentos vencida
		List<Lancamento> vencidos = repository.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		if (vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso.");
			return;
		}
		logger.info("Exitem {} lançamentos vencidos.", vencidos.size());
		// recuperamos a lista de usuarios com permissao, para pegar seus emails
		List<Usuario> destinatarios = usuarios.findByPermissoesDescricao(
				DESTINATARIOS
				);
		if (destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas o "
					+ "sistema não encontrou destinatários.");
			
			return;
		}
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		logger.info("Envio de e-mail de aviso concluído.");
	}
	
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
		// o arquivo deve ter sido enviado antes de vincular ele a um lancamento
		if(StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
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

		// se nao tem anexo no request e tem na base, devemos excluir do S3
		if(StringUtils.isEmpty(lancamento.getAnexo()) && StringUtils.hasText(lancamentoSalvo.getAnexo())){
			s3.remover(lancamentoSalvo.getAnexo());
		// se tiver um arquivo e ele for diferente do atual
		} else if(StringUtils.hasLength(lancamento.getAnexo()) && !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())){
			s3.substituir(lancamentoSalvo.getAnexo(),lancamento.getAnexo());
		}
		
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
