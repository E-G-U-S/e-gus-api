package br.com.egus.api.repository;

import br.com.egus.api.model.produto.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<PedidoModel, Integer> {}
