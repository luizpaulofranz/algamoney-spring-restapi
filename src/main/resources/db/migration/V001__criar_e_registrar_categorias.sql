/*
Quando adicionamos a dependencia do Flyway, as migrations sao executadas 
automaticamente ao levantarmos a aplicacao.

ATENÇÃO para o nome das migrations, devem começar com:
V maiusculo, indica versao
R maiusculo para migrations que devem ser executadas sempre

Seguido do numero da versao em ordem crescente
e de DOIS UNDERLINES, depois dos 2 underlines, podemos escrever qlq coisa 
*/

CREATE TABLE categoria(
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO categoria (nome) VALUES ('Lazer') , ('Alimentação'), ('Supermercado'), ('Farmácia'), ('Outros');