package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoBaseResponse;
import br.com.egus.api.service.ProdutoBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/produtos")
public class ProdutoBaseController {

    private final ProdutoBaseService produtoBaseService;

    public ProdutoBaseController(ProdutoBaseService produtoBaseService) {
        this.produtoBaseService = produtoBaseService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoBaseResponse>> listarTodos() {
        return ResponseEntity.ok(produtoBaseService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoBaseResponse> buscarPorId(@PathVariable Long id) {
        return produtoBaseService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}