/**
 * @PostLoad executa apos o carregamento da entidade.
 * eh uma anotacao do hibernate, ha outros, como @postpersist por exemplo
 * Cada um eh executado de acordo com o ciclo de vida da entidade
 */

package com.algaworks.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.algaworks.algamoney.api.AlgamoneyApiApplication;
import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.storage.S3;

public class LancamentoAnexoListener {

	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if (StringUtils.hasText(lancamento.getAnexo())) {
			// assim usamos a DI do spring nessa classe que esta fora do spring
			// essa classe eh usada pelo Hibernate
			S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}
}
