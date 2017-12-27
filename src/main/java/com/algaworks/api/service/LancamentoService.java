package com.algaworks.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.api.model.Lancamento;
import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.LancamentoRepository;
import com.algaworks.api.repository.PessoaRepository;
import com.algaworks.api.service.exception.LancamentoNullException;
import com.algaworks.api.service.exception.PessoaInativaException;
import com.algaworks.api.service.exception.PessoaNullException;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	public Lancamento buscarById(Long id) {
		Lancamento lancamento = lancamentoRepository.findOne(id);

		if (lancamento == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return lancamento;
	}

	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getId());

		if (pessoa == null) {
			throw new PessoaNullException();
		}
		
		if (!pessoa.isAtivo()) {
			throw new PessoaInativaException();
		}
		
		return lancamentoRepository.save(lancamento);
	}

	public Lancamento atualizar(Long id, Lancamento lancamento) {
		Lancamento lancamentBd = buscarById(id);
		
		if(lancamentBd == null) {
			throw new LancamentoNullException();
		}
		
		if(!lancamento.getPessoa().equals(lancamentBd.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentBd, "id");
		
		return lancamentoRepository.save(lancamento);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getId());

		if (pessoa == null) {
			throw new PessoaNullException();
		}
		
		if (!pessoa.isAtivo()) {
			throw new PessoaInativaException();
		}
	}
}
