package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoPrecoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoPrecoController produtoPrecoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------
    // TESTE 1: listar preços por produto
    // -------------------------------------------------------------
    @Test
    void deveListarPrecosPorProduto() {

        ProdutoResponse r1 = new ProdutoResponse(
                1L, "Coca-Cola", "Bebida", "img1.png", false,
                7.99, 10.0, 6.99, 150, "Mercado A", 11
        );

        ProdutoResponse r2 = new ProdutoResponse(
                1L, "Coca-Cola", "Bebida", "img2.png", false,
                8.20, 10.0, 7.50, 200, "Mercado B", 12
        );

        when(produtoService.listarPorProduto(1L)).thenReturn(List.of(r1, r2));

        ResponseEntity<List<ProdutoResponse>> response =
                produtoPrecoController.listarPrecosPorProduto(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Coca-Cola", response.getBody().get(0).getNome());

        verify(produtoService, times(1)).listarPorProduto(1L);
    }

    // -------------------------------------------------------------
    // TESTE 2: lista vazia (produto existe mas nenhum mercado)
    // -------------------------------------------------------------
    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMercadosParaProduto() {

        when(produtoService.listarPorProduto(5L)).thenReturn(List.of());

        ResponseEntity<List<ProdutoResponse>> response =
                produtoPrecoController.listarPrecosPorProduto(5L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(produtoService, times(1)).listarPorProduto(5L);
    }
}
