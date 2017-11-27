package com.algaworks.algamoney.api.repository.filter;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;


//this contains the user filter data
public class LancamentoFilter {

	
	private String descricao;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate initDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate finishDate;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getInitDate() {
		return initDate;
	}

	public void setInitDate(LocalDate initDate) {
		this.initDate = initDate;
	}

	public LocalDate getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(LocalDate finishDate) {
		this.finishDate = finishDate;
	}
	
	
	
}
