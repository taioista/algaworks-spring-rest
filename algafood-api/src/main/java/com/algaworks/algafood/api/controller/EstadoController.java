package com.algaworks.algafood.api.controller;

import java.util.List;
import java.util.Optional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;
import com.algaworks.algafood.domain.service.CadastroEstadoService;

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
@RequestMapping("/estados")
public class EstadoController {
  
  @Autowired
  private EstadoRepository estadoRepository;

  @Autowired
  private CadastroEstadoService cadastroEstado;

  @GetMapping
  public List<Estado> listar() {
    return estadoRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Estado> buscar(@PathVariable("id") Long id) {
    Optional<Estado> estado = estadoRepository.findById(id);
    if(estado.isPresent()) {
      return ResponseEntity.ok(estado.get());
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  public Estado adicionar(@RequestBody Estado estado) {
    return cadastroEstado.salvar(estado);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Estado> atualizar(@PathVariable Long id, @RequestBody Estado estado) {
    Optional<Estado> estadoAtual = estadoRepository.findById(id);
    if(estadoAtual.isPresent()) {
      BeanUtils.copyProperties(estado, estadoAtual, "id");
      Estado estadoSalvo = cadastroEstado.salvar(estadoAtual.get());
      return ResponseEntity.ok(estadoSalvo);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Estado> remover(@PathVariable Long id) {
    try {
        cadastroEstado.excluir(id);
        return ResponseEntity.noContent().build();
    } catch(EntidadeEmUsoException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch(EntidadeNaoEncontradaException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
