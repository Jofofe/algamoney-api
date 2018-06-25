package com.example.algamoney.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	public Lancamento save(Lancamento lancamento) {
		Pessoa pessoa = getPessoaRepository().findOne(lancamento.getPessoa().getCodigo());
		if(pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		return getRepository().save(lancamento);
	}
	
	public LancamentoRepository getRepository() {
		return repository;
	}

	public PessoaRepository getPessoaRepository() {
		return pessoaRepository;
	}
	
}
