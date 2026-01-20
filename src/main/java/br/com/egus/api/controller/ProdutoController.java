package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mercados")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/{mercadoId}/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarPorMercado(@PathVariable int mercadoId) {
        return ResponseEntity.ok(produtoService.listarPorMercado(mercadoId));
    }
}