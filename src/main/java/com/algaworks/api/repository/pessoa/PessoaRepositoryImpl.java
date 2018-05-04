package com.algaworks.api.repository.pessoa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.algaworks.api.model.Pessoa;
import com.algaworks.api.model.Pessoa_;
import com.algaworks.api.repository.filter.PessoaFilter;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery{

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Pessoa> listar(PessoaFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);
		
		Predicate[] predicates = predicatesPessoa(filtro, builder, root);
		criteria.where(predicates);
		
		TypedQuery<Pessoa> query = manager.createQuery(criteria);
		
		paginacao(query, pageable);
		
		List<Pessoa> resultList = query.getResultList();
		
		return new PageImpl<Pessoa>(resultList, pageable, total(filtro));
	}

	private void paginacao(TypedQuery<Pessoa> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalPorPagina = pageable.getPageSize();
		int registroInicial = paginaAtual * totalPorPagina;

		query.setFirstResult(registroInicial);
		query.setMaxResults(totalPorPagina);
	}

	private Predicate[] predicatesPessoa(PessoaFilter filtro, CriteriaBuilder builder, Root<Pessoa> root) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if (filtro.getNome() != null) {
			predicates.add(builder.like(builder.lower(root.get(Pessoa_.nome)),
					"%" + filtro.getNome().toLowerCase() + "%"));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private Long total(PessoaFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);

		Predicate[] predicates = predicatesPessoa(filtro, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}
	
}
