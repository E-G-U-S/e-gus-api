package br.com.egus.api.controller;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    @Test
    void deveListarProdutosPorMercado() {
        int mercadoId = 10;

        ProdutoResponse p1 = new ProdutoResponse(
                1L, "Arroz", "Alimentos", "url1", false,
                12.50, 10.00, 9.00,
                100, "Mercado X", 999
        );

        ProdutoResponse p2 = new ProdutoResponse(
                2L, "Feijão", "Alimentos", "url2", false,
                8.90, 7.00, 6.50,
                80, "Mercado X", 998
        );

        when(produtoService.listarPorMercado(mercadoId))
                .thenReturn(List.of(p1, p2));

        ResponseEntity<List<ProdutoResponse>> response =
                produtoController.listarPorMercado(mercadoId);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Arroz", response.getBody().get(0).getNome());
        assertEquals("Feijão", response.getBody().get(1).getNome());

        verify(produtoService).listarPorMercado(mercadoId);
    }
}
