package br.com.egus.api.service;

import br.com.egus.api.dto.PedidoDto;
import br.com.egus.api.dto.PedidoItemDto;
import br.com.egus.api.model.pessoa.Usuario;
import br.com.egus.api.model.produto.PedidoItemModel;
import br.com.egus.api.model.produto.PedidoModel;
import br.com.egus.api.model.produto.Produto;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.repository.PedidoRepository;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.repository.ProdutoMercadoRepository;
import br.com.egus.api.repository.ProdutoRepository;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoMercadoRepository produtoMercadoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------
    // TESTE 1 — Criação de pedido com idUsuario fornecido
    // ------------------------------------------------------------
    @Test
    void deveCriarPedidoComIdUsuarioDireto() {
        PedidoDto dto = new PedidoDto();
        dto.setIdUsuario(10L);
        dto.setIdMercado(1);

        PedidoItemDto itemDto = new PedidoItemDto();
        itemDto.setProdutoMercadoId(200);
        itemDto.setQuantidade(2);
        itemDto.setPrecoUnitario(5.0);
        dto.setItens(List.of(itemDto));

        Usuario u = new Usuario();
        u.setId(10L);

        ProdutoMercado pm = new ProdutoMercado();
        pm.setId(200);
        pm.setIdMercado(1);
        pm.setEstoque(10);
        pm.setPreco(5.0);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(u));
        when(produtoMercadoRepository.findById(200)).thenReturn(Optional.of(pm));

        PedidoModel saved = new PedidoModel();
        saved.setId(55);
        saved.setIdUsuario(10L);
        saved.setIdMercado(1);
        saved.setValorTotal(10.0);

        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(saved);

        PedidoDto result = pedidoService.criarPedido(dto);

        assertNotNull(result);
        assertEquals(55L, result.getId());
        assertEquals(10.0, result.getValorTotal());
        verify(pedidoRepository, times(1)).save(any());
    }

    // ------------------------------------------------------------
    // TESTE 2 — Lança erro se usuário não existir
    // ------------------------------------------------------------
    @Test
    void deveLancarErroQuandoUsuarioNaoEncontrado() {
        PedidoDto dto = new PedidoDto();
        dto.setIdUsuario(99L);
        dto.setIdMercado(1);
        dto.setItens(Collections.emptyList());

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pedidoService.criarPedido(dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ------------------------------------------------------------
    // TESTE 3 — Estoque insuficiente
    // ------------------------------------------------------------
    @Test
    void deveLancarErroEstoqueInsuficiente() {
        PedidoDto dto = new PedidoDto();
        dto.setIdUsuario(10L);
        dto.setIdMercado(1);

        PedidoItemDto item = new PedidoItemDto();
        item.setProdutoMercadoId(123);
        item.setQuantidade(50);
        dto.setItens(List.of(item));

        Usuario u = new Usuario();
        u.setId(10L);

        ProdutoMercado pm = new ProdutoMercado();
        pm.setId(123);
        pm.setIdMercado(1);
        pm.setEstoque(10);
        pm.setPreco(3.0);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(u));
        when(produtoMercadoRepository.findById(123)).thenReturn(Optional.of(pm));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> pedidoService.criarPedido(dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Estoque insuficiente"));
    }

    // ------------------------------------------------------------
    // TESTE 4 — Busca por ID
    // ------------------------------------------------------------
    @Test
    void deveBuscarPedidoPorId() {
        PedidoModel model = new PedidoModel();
        model.setId(20);
        model.setIdUsuario(1L);
        model.setIdMercado(2);
        model.setValorTotal(15.0);

        when(pedidoRepository.findById(20)).thenReturn(Optional.of(model));

        Optional<PedidoDto> result = pedidoService.buscarPorId(20);

        assertTrue(result.isPresent());
        assertEquals(20L, result.get().getId());
    }

    // ------------------------------------------------------------
    // TESTE 5 — Autenticação deve preencher idUsuario se dto não mandar
    // ------------------------------------------------------------
    @Test
    void devePreencherIdUsuarioAPartirDaAutenticacao() {

        Usuario user = new Usuario();
        user.setId(88L);
        user.setEmail("test@test.com");

        // Mock da autenticação
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // DTO sem idUsuario
        PedidoDto dto = new PedidoDto();
        dto.setIdMercado(1);
        dto.setItens(Collections.emptyList());

        when(usuarioRepository.findById(88L)).thenReturn(Optional.of(user));

        PedidoModel saved = new PedidoModel();
        saved.setId(900);
        saved.setIdUsuario(88L);
        saved.setIdMercado(1);
        saved.setValorTotal(0.0);

        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(saved);

        PedidoDto result = pedidoService.criarPedido(dto);

        assertEquals(88, result.getIdUsuario());
    }
}
