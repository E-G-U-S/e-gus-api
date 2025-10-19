package br.com.egus.api.repository;

import br.com.egus.api.model.produto.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Integer> {
}