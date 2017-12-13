package com.algaworks.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long id, Pessoa pessoa) {
		Pessoa pessoaBd = buscarById(id);

		BeanUtils.copyProperties(pessoa, pessoaBd, "id");

		return pessoaRepository.save(pessoaBd);
	}

	public Pessoa atualizarAtivo(Long id, Boolean ativo) {
		Pessoa pessoaBd = buscarById(id);

		pessoaBd.setAtivo(ativo);

		return pessoaRepository.save(pessoaBd);
	}

	public Pessoa buscarById(Long id) {
		Pessoa pessoa = pessoaRepository.findOne(id);

		if (pessoa == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return pessoa;
	}
}
