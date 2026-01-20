package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/produtos")
public class ProdutoPrecoController {

    private final ProdutoService produtoService;

    public ProdutoPrecoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/{id}/mercados")
    public ResponseEntity<List<ProdutoResponse>> listarPrecosPorProduto(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.listarPorProduto(id));
    }
}