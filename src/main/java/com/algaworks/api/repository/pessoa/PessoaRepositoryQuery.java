package com.algaworks.api.repository.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.filter.PessoaFilter;

public interface PessoaRepositoryQuery {

	Page<Pessoa> listar(PessoaFilter filtro, Pageable pageable);
}
