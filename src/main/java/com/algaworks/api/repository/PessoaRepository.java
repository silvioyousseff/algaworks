package com.algaworks.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.pessoa.PessoaRepositoryQuery;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepositoryQuery{

	public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable);
}
