package br.com.egus.api.repository;

import br.com.egus.api.model.produto.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PromocaoRepository extends JpaRepository<Promocao, Integer> {

    @Query("select pr from Promocao pr where pr.produtoMercado.id = :produtoMercadoId and pr.dataInicio <= :agora and pr.dataValidade >= :agora")
    Optional<Promocao> findActiveByProdutoMercado(@Param("produtoMercadoId") int produtoMercadoId,
                                                  @Param("agora") LocalDateTime agora);
}