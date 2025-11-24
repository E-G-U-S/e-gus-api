package br.com.egus.api.service.auth;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.model.pessoa.Usuario;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.service.SenhaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CpfAuthenticationStrategyTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private CpfAuthenticationStrategy cpfStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // SUPORTA CPF?
    // ----------------------------------------------------------
    @Test
    void deveRetornarTrueQuandoRequestTiverCpf() {
        LoginRequest req = new LoginRequest();
        req.setCpf(123L);

        assertTrue(cpfStrategy.supports(req));
    }

    @Test
    void deveRetornarFalseQuandoRequestNaoTiverCpf() {
        LoginRequest req = new LoginRequest();
        req.setCpf(null);

        assertFalse(cpfStrategy.supports(req));
    }

    // ----------------------------------------------------------
    // AUTENTICAÇÃO COM SUCESSO
    // ----------------------------------------------------------
    @Test
    void deveAutenticarComSucesso() {
        LoginRequest req = new LoginRequest();
        req.setCpf(123L);
        req.setSenha("senha");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Arthur");
        usuario.setCpf(123L);
        usuario.setEmail("arthur@test.com");
        usuario.setSenha("senhaHash");
        usuario.setPreferences(Map.of("tema", "dark"));

        when(usuarioRepository.findByCpf(123L)).thenReturn(Optional.of(usuario));
        when(senhaService.validarSenha("senha", "senhaHash")).thenReturn(true);

        LoginResponse response = cpfStrategy.authenticate(req);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Arthur", response.getNome());
        assertEquals("USUARIO", response.getTipo());
        assertEquals(123L, response.getCpf());
        assertEquals("arthur@test.com", response.getEmail());
        assertEquals(Map.of("tema", "dark"), response.getPreferences());
    }

    // ----------------------------------------------------------
    // CPF NÃO ENCONTRADO
    // ----------------------------------------------------------
    @Test
    void deveRetornarUnauthorizedQuandoCpfNaoExistir() {
        LoginRequest req = new LoginRequest();
        req.setCpf(999L);
        req.setSenha("123");

        when(usuarioRepository.findByCpf(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> cpfStrategy.authenticate(req));

        assertEquals(401, ex.getStatusCode().value());
    }

    // ----------------------------------------------------------
    // SENHA INVÁLIDA
    // ----------------------------------------------------------
    @Test
    void deveRetornarUnauthorizedQuandoSenhaInvalida() {
        LoginRequest req = new LoginRequest();
        req.setCpf(123L);
        req.setSenha("errada");

        Usuario usuario = new Usuario();
        usuario.setCpf(123L);
        usuario.setSenha("senhaHash");

        when(usuarioRepository.findByCpf(123L)).thenReturn(Optional.of(usuario));
        when(senhaService.validarSenha("errada", "senhaHash")).thenReturn(false);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> cpfStrategy.authenticate(req));

        assertEquals(401, ex.getStatusCode().value());
    }
}
