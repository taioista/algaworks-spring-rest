package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestaurante;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/restaurantes")
public class RestauranteController {
  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private CadastroRestaurante cadastroRestaurante;

  @GetMapping
  public List<Restaurante> listar() {
    return restauranteRepository.findAll();  
  }

  @GetMapping("/{id}")
  public ResponseEntity<Restaurante> buscar(@PathVariable("id") Long id) {
    Optional<Restaurante> restaurante = restauranteRepository.findById(id);
    if(restaurante.isPresent()) {
      return ResponseEntity.ok(restaurante.get());
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {
    try {
      restaurante = cadastroRestaurante.salvar(restaurante);
      return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
    } catch(EntidadeNaoEncontradaException ee) {
      return ResponseEntity.badRequest().body(ee.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante) {
    try {
      Restaurante restauranteAtual = restauranteRepository.findById(id).orElse(null);
      if(restauranteAtual != null) {
        BeanUtils.copyProperties(restaurante, restauranteAtual, "id");
        restauranteAtual = cadastroRestaurante.salvar(restauranteAtual);
        return ResponseEntity.ok(restauranteAtual);
      }
      return ResponseEntity.notFound().build();
    } catch(EntidadeNaoEncontradaException ee) {
      return ResponseEntity.badRequest().body(ee.getMessage());
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
    Restaurante restauranteAtual = restauranteRepository.findById(id).orElse(null);
    if(restauranteAtual == null) {
      return ResponseEntity.notFound().build();
    }

    merge(campos, restauranteAtual);

    return atualizar(id, restauranteAtual);
  }

  private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino) {
    ObjectMapper mapper = new ObjectMapper();
    Restaurante restauranteOrigem = mapper.convertValue(dadosOrigem, Restaurante.class);

    dadosOrigem.forEach((nome, valor) -> {
      Field field = ReflectionUtils.findField(Restaurante.class, nome);
      field.setAccessible(true);
      Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);      
      ReflectionUtils.setField(field, restauranteDestino, novoValor);
    });
  }
}
