package br.com.egus.api.service;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.service.auth.AuthenticationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticaServiceTest {

    @Mock
    private AuthenticationStrategy strategy1;

    @Mock
    private AuthenticationStrategy strategy2;

    @InjectMocks
    private AutenticaService autenticaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        autenticaService = new AutenticaService(List.of(strategy1, strategy2));
    }

    @Test
    void deveAutenticarUsandoStrategyQueDaSuporte() {
        LoginRequest request = new LoginRequest();
        request.setEmail("teste@teste.com");

        LoginResponse responseMock = new LoginResponse(1L, "Arthur", "USUARIO", 123L, "teste@teste.com", null);

        when(strategy1.supports(request)).thenReturn(false);
        when(strategy2.supports(request)).thenReturn(true);
        when(strategy2.authenticate(request)).thenReturn(responseMock);

        LoginResponse response = autenticaService.login(request);

        assertNotNull(response);
        assertEquals("Arthur", response.getNome());
        verify(strategy2).authenticate(request);
        verify(strategy1, never()).authenticate(any());
    }

    @Test
    void deveLancarErroQuandoNenhumaStrategyDerSuporte() {
        LoginRequest request = new LoginRequest();

        when(strategy1.supports(request)).thenReturn(false);
        when(strategy2.supports(request)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> autenticaService.login(request));

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("É necessário informar email ou CPF", ex.getReason());

        verify(strategy1, never()).authenticate(any());
        verify(strategy2, never()).authenticate(any());
    }

    @Test
    void deveUsarPrimeiraStrategyQueDerSuporteMesmoQueOutraTambemSuporte() {
        LoginRequest request = new LoginRequest();
        request.setCpf(123L);

        LoginResponse responseMock = new LoginResponse(2L, "João", "USUARIO", 123L, "joao@dominio.com", null);

        when(strategy1.supports(request)).thenReturn(true);
        when(strategy2.supports(request)).thenReturn(true); // deve ser ignorada
        when(strategy1.authenticate(request)).thenReturn(responseMock);

        LoginResponse response = autenticaService.login(request);

        assertEquals("João", response.getNome());
        verify(strategy1).authenticate(request);
        verify(strategy2, never()).authenticate(request);
    }
}
