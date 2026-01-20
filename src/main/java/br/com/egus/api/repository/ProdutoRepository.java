package br.com.egus.api.repository;

import br.com.egus.api.model.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Query("select p from Produto p join fetch p.categoria c")
    List<Produto> findAllFetchCategoria();
}