package com.algaworks.algafood.api.controller;

import java.util.List;
import java.util.Optional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.service.CadastroCidadeService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {
  @Autowired
  private CidadeRepository cidadeRepository;

  @Autowired
  private CadastroCidadeService cadastroCidade;

  @GetMapping
  public List<Cidade> listar() {
    return cidadeRepository.findAll();  
  }

  @GetMapping("/{id}")
  public ResponseEntity<Cidade> buscar(@PathVariable("id") Long id) {
    Optional<Cidade> cidade = cidadeRepository.findById(id);
    if(cidade.isPresent()) {
      return ResponseEntity.ok(cidade.get());
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  public Cidade adicionar(@RequestBody Cidade cidade) {
    return cadastroCidade.salvar(cidade);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @RequestBody Cidade cidade) {
    Optional<Cidade> cidadeAtual = cidadeRepository.findById(id);
    if(cidadeAtual.isPresent()) {
      BeanUtils.copyProperties(cidade, cidadeAtual.get(), "id");
      Cidade cidadeSalva = cadastroCidade.salvar(cidadeAtual.get());
      return ResponseEntity.ok(cidadeSalva);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Cidade> remover(@PathVariable Long id) {
    try {
        cadastroCidade.excluir(id);
        return ResponseEntity.noContent().build();
    } catch(EntidadeEmUsoException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch(EntidadeNaoEncontradaException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
