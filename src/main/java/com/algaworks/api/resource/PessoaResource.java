package com.algaworks.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.api.event.RecursoCriadoEvent;
import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.PessoaRepository;
import com.algaworks.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping("/listar")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public List<Pessoa> listar() {
		return pessoaRepository.findAll();
	}

	@PostMapping("/salvar")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> salvar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa save = pessoaRepository.save(pessoa);

		publisher.publishEvent(new RecursoCriadoEvent(this, response, save.getId()));

		return ResponseEntity.status(HttpStatus.CREATED).body(save);
	}

	@GetMapping("buscar/{id}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long id) {
		Pessoa pessoa = pessoaRepository.findOne(id);

		return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("excluir/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('read')")
	public void excluir(@PathVariable Long id) {
		pessoaRepository.delete(id);
	}

	@PutMapping("atualizar/{id}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long id, @Valid @RequestBody Pessoa pessoa) {

		Pessoa pessoaBd = pessoaService.atualizar(id, pessoa);

		return ResponseEntity.ok(pessoaBd);
	}

	@PutMapping("atualizar/{id}/ativo")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public void atualizarAtivo(@PathVariable Long id, @Valid @RequestBody Boolean ativo) {
		pessoaService.atualizarAtivo(id, ativo);
	}
}
