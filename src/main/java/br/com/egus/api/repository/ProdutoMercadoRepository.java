package br.com.egus.api.repository;

import br.com.egus.api.model.produto.ProdutoMercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoMercadoRepository extends JpaRepository<ProdutoMercado, Integer> {

    @Query("select pm from ProdutoMercado pm join fetch pm.produto p join fetch p.categoria c where pm.idMercado = :idMercado")
    List<ProdutoMercado> findAllByIdMercadoFetch(@Param("idMercado") int idMercado);

    @Query("select pm from ProdutoMercado pm join fetch pm.produto p join fetch p.categoria c where p.id = :produtoId")
    List<ProdutoMercado> findAllByProdutoIdFetch(@Param("produtoId") Long produtoId);
}