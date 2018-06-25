package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvento;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

/**
 * CategoriaResource.class
 * 
 * @author felippe.ferreira
 * O proprio Spring retorna os Obj em formato JSON.
 * Recebe um Obj do body com a @RequestBody.
 * Recebe um Obj da URL com @PathVariable
 * Para comportamentos esperados dos recursos, sempre implementar um EVENT, pra evitar duplicidade
 * Qdo tiver não tiver um retorno no body, usar a @ResponseStatus(HttpStatus.XXX)
 * TODAS as excessões esperadas de REST e VALIDAÇÕES(@Valid do Bean validation), são tratadas no interceptor "AlgamoneyApiApplication"
 * Regras de negocio são tratadas no SERVICE do resource
 * E as excessões de negocio, que são disparadas no SERVICE, são tratadas no proprio RESOURCE
 * Para a pesquisa por filtros, tem que criar uma classe com os filtro desejados, criar uma interface QUERY do repositorio, fazer o 
 *REPOSITORIO EXTENDER essa interface, implementar a QUERY, e montar o metodo que recebe o filter com CRITERIA, com seus propios campos
 * Para a paginação, tem que ser mandado um PAGEABLE, e retornado um PAGE. No retorno PAGE, tem que retornar a lista de resultados
 *em conjunto com a pagina, o numero do registro inicial(Os 2 pegos do PAGEABLE) e o total de registros.
 *
 */

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository repository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Categoria> listar() {
		return getRepository().findAll();
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Categoria categoria = getRepository().findOne(codigo);
		if(categoria != null) {
			return ResponseEntity.ok().body(categoria);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = getRepository().save(categoria);
		getPublisher().publishEvent(new RecursoCriadoEvento(this, response, categoriaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);		
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo){
		getRepository().delete(codigo);
	}

	public CategoriaRepository getRepository() {
		return repository;
	}
	
	public ApplicationEventPublisher getPublisher() {
		return publisher;
	}
	
}
