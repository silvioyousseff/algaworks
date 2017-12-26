package com.algaworks.api.repository.lancamento;

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

import com.algaworks.api.model.Categoria_;
import com.algaworks.api.model.Lancamento;
import com.algaworks.api.model.Lancamento_;
import com.algaworks.api.model.Pessoa_;
import com.algaworks.api.repository.filter.LancamentoFilter;
import com.algaworks.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Lancamento> listar(LancamentoFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		List<Predicate> predicates = predicatesLancamento(filtro, builder, root);

		criteria.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Lancamento> query = manager.createQuery(criteria);

		paginacao(query, pageable);

		List<Lancamento> resultList = query.getResultList();

		return new PageImpl<Lancamento>(resultList, pageable, resultList.size());
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		criteria.select(builder.construct(ResumoLancamento.class, root.get(Lancamento_.id),
				root.get(Lancamento_.descricao), root.get(Lancamento_.dataVencimento),
				root.get(Lancamento_.dataPagamento), root.get(Lancamento_.valor), root.get(Lancamento_.tipo),
				root.get(Lancamento_.categoria).get(Categoria_.nome), root.get(Lancamento_.pessoa).get(Pessoa_.nome)));

		List<Predicate> predicates = predicatesLancamento(filtro, builder, root);

		criteria.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);

		paginacao(query, pageable);

		List<ResumoLancamento> resultList = query.getResultList();

		return new PageImpl<>(resultList, pageable, resultList.size());
	}

	private List<Predicate> predicatesLancamento(LancamentoFilter filtro, CriteriaBuilder builder,
			Root<Lancamento> root) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (filtro.getDescricao() != null) {
			predicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)),
					"%" + filtro.getDescricao().toLowerCase() + "%"));
		}

		if (filtro.getDataInicio() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), filtro.getDataInicio()));
		}

		if (filtro.getDataFim() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), filtro.getDataInicio()));
		}

		return predicates;
	}

	private void paginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalPorPagina = pageable.getPageSize();
		int registroInicial = paginaAtual * totalPorPagina;

		query.setFirstResult(registroInicial);
		query.setMaxResults(totalPorPagina);
	}
}
