package com.algaworks.algafood.api.controller;

import java.util.List;
import java.util.Optional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;

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
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

  @Autowired
  private CozinhaRepository cozinhaRepository;

  @Autowired
  private CadastroCozinhaService cadastroCozinha;

  @GetMapping
  public List<Cozinha> listar() {
    return cozinhaRepository.findAll();  
  }

  @GetMapping("/{id}")
  public ResponseEntity<Cozinha> buscar(@PathVariable("id") Long id) {
    Optional<Cozinha> cozinha = cozinhaRepository.findById(id);
    if(cozinha.isPresent()) {
      return ResponseEntity.ok(cozinha.get());
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  public Cozinha adicionar(@RequestBody Cozinha cozinha) {
    return cadastroCozinha.salvar(cozinha);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Cozinha> atualizar(@PathVariable Long id, @RequestBody Cozinha cozinha) {
    Optional<Cozinha> cozinhaAtual = cozinhaRepository.findById(id);
    if(cozinhaAtual.isPresent()) {
      BeanUtils.copyProperties(cozinha, cozinhaAtual.get(), "id");
      Cozinha cozinhaSalva = cadastroCozinha.salvar(cozinhaAtual.get());
      return ResponseEntity.ok(cozinhaSalva);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Cozinha> remover(@PathVariable Long id) {
    try {
        cadastroCozinha.excluir(id);
        return ResponseEntity.noContent().build();
    } catch(EntidadeEmUsoException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch(EntidadeNaoEncontradaException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
