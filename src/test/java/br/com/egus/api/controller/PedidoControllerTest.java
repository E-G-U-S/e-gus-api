package br.com.egus.api.controller;

import br.com.egus.api.dto.PedidoDto;
import br.com.egus.api.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ==============================
    //     POST /pedidos (SUCESSO)
    // ==============================
    @Test
    void deveCriarPedidoComSucesso() {
        PedidoDto request = new PedidoDto();
        request.setId(1L);

        PedidoDto criado = new PedidoDto();
        criado.setId(1L);

        when(pedidoService.criarPedido(any(PedidoDto.class))).thenReturn(criado);

        ResponseEntity<PedidoDto> response = pedidoController.criar(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(criado, response.getBody());
        assertEquals(URI.create("/pedidos/1"), response.getHeaders().getLocation());

        verify(pedidoService, times(1)).criarPedido(any());
    }

    // =========================================
    //     POST /pedidos (BAD REQUEST - NULL)
    // =========================================
    @Test
    void deveRetornarBadRequestQuandoCriarRetornarNull() {
        PedidoDto request = new PedidoDto();

        when(pedidoService.criarPedido(any())).thenReturn(null);

        ResponseEntity<PedidoDto> response = pedidoController.criar(request);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(pedidoService, times(1)).criarPedido(any());
    }

    // ==============================
    //     GET /pedidos/{id}
    // ==============================
    @Test
    void deveBuscarPedidoComSucesso() {
        PedidoDto pedido = new PedidoDto();
        pedido.setId(10L);

        when(pedidoService.buscarPorId(10)).thenReturn(Optional.of(pedido));

        ResponseEntity<PedidoDto> response = pedidoController.buscar(10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pedido, response.getBody());
        verify(pedidoService).buscarPorId(10);
    }

    // =====================================
    //     GET /pedidos/{id} (NOT FOUND)
    // =====================================
    @Test
    void deveRetornarNotFoundQuandoPedidoNaoExistir() {
        when(pedidoService.buscarPorId(99)).thenReturn(Optional.empty());

        ResponseEntity<PedidoDto> response = pedidoController.buscar(99);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(pedidoService, times(1)).buscarPorId(99);
    }
}
