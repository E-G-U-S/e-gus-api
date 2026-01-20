package br.com.egus.api.repository;

import br.com.egus.api.model.produto.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}