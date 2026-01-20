package br.com.egus.api.service.auth;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.model.pessoa.Cargo;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.model.pessoa.Usuario;
import br.com.egus.api.repository.FuncionarioRepository;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.service.SenhaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailAuthenticationStrategyTest {

    private FuncionarioRepository funcionarioRepository;
    private UsuarioRepository usuarioRepository;
    private SenhaService senhaService;
    private EmailAuthenticationStrategy strategy;

    @BeforeEach
    void setup() {
        funcionarioRepository = mock(FuncionarioRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        senhaService = mock(SenhaService.class);
        strategy = new EmailAuthenticationStrategy(funcionarioRepository, usuarioRepository, senhaService);
    }

    // ------------------------------------------------------------
    // supports()
    // ------------------------------------------------------------
    @Test
    void testSupports_WhenEmailPresent() {
        LoginRequest request = new LoginRequest();
        request.setEmail("teste@exemplo.com");

        assertTrue(strategy.supports(request));
    }

    @Test
    void testSupports_WhenEmailIsNull() {
        LoginRequest request = new LoginRequest();
        request.setEmail(null);

        assertFalse(strategy.supports(request));
    }

    // ------------------------------------------------------------
    // LOGIN COMO FUNCIONARIO
    // ------------------------------------------------------------
    @Test
    void testAuthenticate_FuncionarioAtivo_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("func@empresa.com");
        request.setSenha("123");

        Funcionario funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setNome("João Admin");
        funcionario.setAtivo(true);
        funcionario.setCargo(Cargo.Administrador);
        funcionario.setIdMercado(5);
        funcionario.setSenha("senha-hash");

        when(funcionarioRepository.findByEmail("func@empresa.com"))
                .thenReturn(Optional.of(funcionario));

        when(senhaService.validarSenha("123", "senha-hash")).thenReturn(true);

        LoginResponse response = strategy.authenticate(request);

        assertEquals(10L, response.getId());
        assertEquals("João Admin", response.getNome());
        assertEquals("FUNCIONARIO", response.getTipo());
        assertEquals("Administrador", response.getCargo()); // enum → String
        assertEquals(5, response.getIdMercado());
    }

    @Test
    void testAuthenticate_FuncionarioInativo() {
        LoginRequest request = new LoginRequest();
        request.setEmail("func@empresa.com");
        request.setSenha("123");

        Funcionario funcionario = new Funcionario();
        funcionario.setAtivo(false);

        when(funcionarioRepository.findByEmail("func@empresa.com"))
                .thenReturn(Optional.of(funcionario));

        assertThrows(ResponseStatusException.class,
                () -> strategy.authenticate(request));
    }

    @Test
    void testAuthenticate_FuncionarioSenhaInvalida() {
        LoginRequest request = new LoginRequest();
        request.setEmail("func@empresa.com");
        request.setSenha("123");

        Funcionario funcionario = new Funcionario();
        funcionario.setAtivo(true);
        funcionario.setSenha("hash");

        when(funcionarioRepository.findByEmail("func@empresa.com"))
                .thenReturn(Optional.of(funcionario));

        when(senhaService.validarSenha("123", "hash")).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> strategy.authenticate(request));
    }

    // ------------------------------------------------------------
    // LOGIN COMO USUARIO
    // ------------------------------------------------------------
    @Test
    void testAuthenticate_Usuario_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("usuario@teste.com");
        request.setSenha("pass");

        Usuario usuario = new Usuario();
        usuario.setId(77L);
        usuario.setNome("Maria");
        usuario.setCpf(12345678901L);
        usuario.setEmail("usuario@teste.com");
        usuario.setPreferences(Map.of("tema", "escuro"));
        usuario.setSenha("hash-pass");

        when(funcionarioRepository.findByEmail("usuario@teste.com"))
                .thenReturn(Optional.empty());

        when(usuarioRepository.findByEmail("usuario@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(senhaService.validarSenha("pass", "hash-pass"))
                .thenReturn(true);

        LoginResponse response = strategy.authenticate(request);

        assertEquals(77L, response.getId());
        assertEquals("Maria", response.getNome());
        assertEquals("USUARIO", response.getTipo());
        assertEquals(12345678901L, response.getCpf());
        assertEquals("usuario@teste.com", response.getEmail());
        assertEquals(Map.of("tema", "escuro"), response.getPreferences());
    }

    @Test
    void testAuthenticate_UsuarioNaoEncontrado() {
        LoginRequest request = new LoginRequest();
        request.setEmail("naoexiste@teste.com");

        when(funcionarioRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(usuarioRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> strategy.authenticate(request));
    }

    @Test
    void testAuthenticate_UsuarioSenhaInvalida() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@teste.com");
        request.setSenha("123");

        Usuario usuario = new Usuario();
        usuario.setSenha("hash");

        when(funcionarioRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(usuarioRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(usuario));

        when(senhaService.validarSenha("123", "hash")).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> strategy.authenticate(request));
    }
}
