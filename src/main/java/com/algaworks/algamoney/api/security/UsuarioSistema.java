package com.algaworks.algamoney.api.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.algaworks.algamoney.api.model.Usuario;

/**
 * Essa classe eh soh um wrapper para o objeto usuario, que precisamos para 
 * passar informações extra ao payload do token JWT 
 * 
 * Usado na classe TokenExtraInfo, para adicionar informações extra ao Token.
 * 
 * */
public class UsuarioSistema extends User {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	//construtor apenas pra passar para a classe pai
	public UsuarioSistema(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getEmail(), usuario.getSenha(), authorities);
		this.usuario = usuario;
	}

	//esse metodo que importa, ao fazer login, agora temos acesso ao objeto usuario inteiro
	public Usuario getUsuario() {
		return usuario;
	}

}