package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoBaseResponse;
import br.com.egus.api.service.ProdutoBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoBaseControllerTest {

    @Mock
    private ProdutoBaseService produtoBaseService;

    @InjectMocks
    private ProdutoBaseController produtoBaseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------
    // TESTE 1: listarTodos()
    // -------------------------------------------------------------
    @Test
    void deveListarTodosOsProdutosBase() {

        ProdutoBaseResponse p1 = new ProdutoBaseResponse(
                1L, "Coca-Cola", "Bebida", 10,
                "img1.png", false, "123"
        );

        ProdutoBaseResponse p2 = new ProdutoBaseResponse(
                2L, "Heineken", "Cerveja", 20,
                "img2.png", true, "456"
        );

        when(produtoBaseService.listarTodos()).thenReturn(List.of(p1, p2));

        ResponseEntity<List<ProdutoBaseResponse>> response = produtoBaseController.listarTodos();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals("Coca-Cola", response.getBody().get(0).getNome());

        verify(produtoBaseService, times(1)).listarTodos();
    }

    // -------------------------------------------------------------
    // TESTE 2: buscarPorId() - encontrado
    // -------------------------------------------------------------
    @Test
    void deveBuscarProdutoBasePorId() {

        ProdutoBaseResponse produto = new ProdutoBaseResponse(
                1L, "Coca-Cola", "Bebida", 10,
                "img1.png", false, "123"
        );

        when(produtoBaseService.buscarPorId(1L)).thenReturn(Optional.of(produto));

        ResponseEntity<ProdutoBaseResponse> response = produtoBaseController.buscarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Coca-Cola", response.getBody().getNome());

        verify(produtoBaseService, times(1)).buscarPorId(1L);
    }

    // -------------------------------------------------------------
    // TESTE 3: buscarPorId() - não encontrado
    // -------------------------------------------------------------
    @Test
    void deveRetornarNotFoundQuandoProdutoNaoExistir() {

        when(produtoBaseService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProdutoBaseResponse> response = produtoBaseController.buscarPorId(99L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(produtoBaseService, times(1)).buscarPorId(99L);
    }
}
