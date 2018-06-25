package com.example.algamoney.api.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvento;
import com.example.algamoney.api.exceptionHandler.Erro;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.service.LancamentoService;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

/**
 * LancamentoResource.class
 * 
 * @author felippe.ferreira
 * O proprio Spring retorna os Obj em formato JSON.
 * Recebe um Obj do body com a @RequestBody.
 * Recebe um Obj da URL com @PathVariable
 * Para comportamentos esperados dos recursos, sempre implementar um EVENT, pra evitar duplicidade
 * Qdo tiver não tiver um retorno no body, usar a @ResponseStatus(HttpStatus.XXX)
 * TODAS as excessões esperadas de REST e VALIDAÇÕES(@Valid do Bean validation), são tratadas no interceptor "AlgamoneyExceptionHandler"
 * Regras de negocio são tratadas no SERVICE do resource
 * E as excessões de negocio, que são disparadas no SERVICE, são tratadas no proprio RESOURCE
 * Para a pesquisa por filtros, tem que criar uma classe com os filtro desejados, criar uma interface QUERY do repositorio, fazer o 
 *REPOSITORIO EXTENDER essa interface, implementar a QUERY, e montar o metodo que recebe o filter com CRITERIA, com seus propios campos
 * Para a paginação, tem que ser mandado um PAGEABLE, e retornado um PAGE. No retorno PAGE, tem que retornar a lista de resultados
 *em conjunto com a pagina, o numero do registro inicial(Os 2 pegos do PAGEABLE) e o total de registros.
 *
 */

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository repository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private LancamentoService service;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping
	public Page<Lancamento> pesquisar(LancamentoFilter filter, Pageable pageable){
		return getRepository().filtrar(filter, pageable);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo){
		Lancamento lancamento = getRepository().findOne(codigo);
		if(lancamento != null){
			return ResponseEntity.ok(lancamento);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping
	public ResponseEntity<Lancamento> criar(@RequestBody @Valid Lancamento lancamento, HttpServletResponse response){
		Lancamento lancamentoSalvo = getService().save(lancamento);
		getPublisher().publishEvent(new RecursoCriadoEvento(lancamento, response, lancamento.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long codigo) {
		getRepository().delete(codigo);
	}
	
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex){
		String msgView = getMessageSource().getMessage("pessoa.inexistente-inativa", null, LocaleContextHolder.getLocale());
		String msgStackTrace = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(msgView, msgStackTrace));
		return ResponseEntity.badRequest().body(erros);
	}

	public LancamentoRepository getRepository() {
		return repository;
	}

	public ApplicationEventPublisher getPublisher() {
		return publisher;
	}

	public LancamentoService getService() {
		return service;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}
	
}
