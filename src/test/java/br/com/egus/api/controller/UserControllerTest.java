package br.com.egus.api.controller;

import br.com.egus.api.dto.UsuarioRequest;
import br.com.egus.api.model.pessoa.Usuario;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.service.SenhaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // CADASTRAR
    // ----------------------------------------------------------

    @Test
    void deveCadastrarUsuarioComSucesso() {
        UsuarioRequest req = new UsuarioRequest();
        req.setNome("Arthur");
        req.setEmail("arthur@test.com");
        req.setSenha("123");
        req.setCpf(Long.valueOf("12345678910"));

        when(usuarioRepository.existsByEmail("arthur@test.com")).thenReturn(false);
        when(usuarioRepository.existsByCpf(Long.valueOf("12345678910"))).thenReturn(false);
        when(senhaService.gerarHash("123")).thenReturn("senhaHash");

        Usuario salvo = new Usuario();
        salvo.setId(1L);
        salvo.setNome("Arthur");
        salvo.setEmail("arthur@test.com");
        salvo.setSenha("senhaHash");
        salvo.setCpf(Long.valueOf("12345678910"));

        when(usuarioRepository.save(any())).thenReturn(salvo);

        ResponseEntity<?> response = usuarioController.cadastrar(req);

        assertEquals(200, response.getStatusCode().value());
        Usuario usuarioRetornado = (Usuario) response.getBody();
        assertNotNull(usuarioRetornado);
        assertEquals("Arthur", usuarioRetornado.getNome());
        assertEquals("senhaHash", usuarioRetornado.getSenha());
    }

    @Test
    void deveRetornarBadRequestQuandoEmailJaExistir() {
        UsuarioRequest req = new UsuarioRequest();
        req.setEmail("teste@test.com");

        when(usuarioRepository.existsByEmail("teste@test.com")).thenReturn(true);

        ResponseEntity<?> response = usuarioController.cadastrar(req);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email já cadastrado", response.getBody());
    }

    @Test
    void deveRetornarBadRequestQuandoCpfJaExistir() {
        UsuarioRequest req = new UsuarioRequest();
        req.setEmail("ok@test.com");
        req.setCpf(Long.valueOf("12345678910"));

        when(usuarioRepository.existsByEmail("ok@test.com")).thenReturn(false);
        when(usuarioRepository.existsByCpf(Long.valueOf("12345678910"))).thenReturn(true);

        ResponseEntity<?> response = usuarioController.cadastrar(req);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("CPF já cadastrado", response.getBody());
    }

    // ----------------------------------------------------------
    // LISTAR POR ID
    // ----------------------------------------------------------

    @Test
    void deveListarPorIdComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Arthur");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> response = usuarioController.listarPorId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Arthur", response.getBody().getNome());
    }

    @Test
    void deveRetornarNotFoundAoListarPorId() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioController.listarPorId(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    // ----------------------------------------------------------
    // LISTAR TODOS
    // ----------------------------------------------------------

    @Test
    void deveListarTodosUsuarios() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();

        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<Usuario>> response = usuarioController.listarTodos();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    // ----------------------------------------------------------
    // ATUALIZAR
    // ----------------------------------------------------------

    @Test
    void deveAtualizarUsuarioComSucesso() {
        UsuarioRequest req = new UsuarioRequest();
        req.setNome("Novo Nome");
        req.setEmail("novo@test.com");
        req.setSenha("senhaNova");
        req.setCpf(99999999999L);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("tema", "dark");

        req.setPreferences(prefs);

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNome("Antigo");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(senhaService.gerarHash("senhaNova")).thenReturn("hashNova");

        Usuario atualizado = new Usuario();
        atualizado.setId(1L);
        atualizado.setNome("Novo Nome");
        atualizado.setEmail("novo@test.com");
        atualizado.setSenha("hashNova");
        atualizado.setCpf(99999999999L);
        atualizado.setPreferences(prefs);

        when(usuarioRepository.save(any())).thenReturn(atualizado);

        ResponseEntity<Usuario> response = usuarioController.atualizar(1L, req);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Novo Nome", response.getBody().getNome());
        assertEquals("hashNova", response.getBody().getSenha());
        assertEquals(prefs, response.getBody().getPreferences());
    }


    @Test
    void deveRetornarNotFoundAoAtualizarUsuario() {
        UsuarioRequest req = new UsuarioRequest();

        when(usuarioRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioController.atualizar(5L, req);

        assertEquals(404, response.getStatusCode().value());
        verify(usuarioRepository, never()).save(any());
    }
}
