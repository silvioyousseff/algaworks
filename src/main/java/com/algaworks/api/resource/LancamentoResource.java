package com.algaworks.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.api.event.RecursoCriadoEvent;
import com.algaworks.api.model.Lancamento;
import com.algaworks.api.repository.LancamentoRepository;
import com.algaworks.api.repository.filter.LancamentoFilter;
import com.algaworks.api.repository.projection.ResumoLancamento;
import com.algaworks.api.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@CrossOrigin
	@GetMapping("/listar")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<Lancamento> listar(LancamentoFilter filtro, Pageable pageable) {
		return lancamentoRepository.listar(filtro, pageable);
	}

	@CrossOrigin
	@GetMapping("/listarResumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<ResumoLancamento> listarResumo(LancamentoFilter filtro, Pageable pageable) {
		return lancamentoRepository.resumir(filtro, pageable);
	}

	@GetMapping("buscar/{id}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Lancamento> buscar(@PathVariable Long id) {
		Lancamento lancamento = lancamentoRepository.findOne(id);

		return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build();
	}

	@PostMapping("/salvar")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento> salvar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento save = lancamentoService.salvar(lancamento);

		publisher.publishEvent(new RecursoCriadoEvent(this, response, save.getId()));

		return ResponseEntity.status(HttpStatus.CREATED).body(save);
	}

	@DeleteMapping("excluir/{id}")
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		lancamentoRepository.delete(id);
	}
}
