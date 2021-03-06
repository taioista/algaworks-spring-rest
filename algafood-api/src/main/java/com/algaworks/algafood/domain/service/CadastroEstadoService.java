package com.algaworks.algafood.domain.service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CadastroEstadoService {

  @Autowired
  private EstadoRepository estadoRepository;

  public Estado salvar(Estado estado) {
    return estadoRepository.save(estado);
  }

  public void excluir(Long id) {
    try {
      estadoRepository.deleteById(id);
    } catch(EmptyResultDataAccessException ex) {
      throw new EntidadeEmUsoException(
        String.format("Não existe um cadastro de estado com código %d", id));
    } catch(DataIntegrityViolationException ex) {
      throw new EntidadeEmUsoException(
        String.format("Estado de código %d não pode ser removido, pois está em uso", id));
    }
  }
}
