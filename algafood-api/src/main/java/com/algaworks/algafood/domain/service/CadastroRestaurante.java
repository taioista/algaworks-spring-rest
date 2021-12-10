package com.algaworks.algafood.domain.service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CadastroRestaurante {

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private CozinhaRepository cozinhaRepository;

  public Restaurante salvar(Restaurante restaurante) {
    Long cozinhaId = restaurante.getCozinha().getId();
    Cozinha cozinha = cozinhaRepository.findById(cozinhaId)
                        .orElseThrow(() -> new EntidadeNaoEncontradaException(
                            String.format("Nào existe cadastro de cozinha com código %d", cozinhaId)));

    restaurante.setCozinha(cozinha);
    return restauranteRepository.save(restaurante);
  }

  public void excluir(Long id) {
    try {
      restauranteRepository.deleteById(id);
    } catch(EmptyResultDataAccessException ex) {
      throw new EntidadeEmUsoException(
        String.format("Não existe um cadastro de restaurante com código %d", id));
    } catch(DataIntegrityViolationException ex) {
      throw new EntidadeEmUsoException(
        String.format("Restaurante de código %d não pode ser removida, pois está em uso", id));
    }
  }
}
