package com.algaworks.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.algamoney.api.model.Categoria;

//passamos o tipo da classe e o tipo da chave primaria
//o tipo da chave primaria eh para os metodos findOneById
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
