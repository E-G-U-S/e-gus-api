package br.com.egus.api.controller;

import br.com.egus.api.dto.FuncionarioRequest;
import br.com.egus.api.model.pessoa.Cargo;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.repository.FuncionarioRepository;
import br.com.egus.api.service.SenhaService;
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

class FuncionarioControllerTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------------------
    // TESTE DO POST /funcionarios
    // ------------------------------
    @Test
    void deveCadastrarFuncionarioComSucesso() {
        FuncionarioRequest request = new FuncionarioRequest();
        request.setNome("Arthur");
        request.setEmail("arthur@email.com");
        request.setSenha("123456");
        request.setAtivo(true);
        request.setCargo("Administrador");
        request.setIdMercado(10);

        Funcionario salvo = new Funcionario();
        salvo.setId(1L);
        salvo.setNome("Arthur");

        when(senhaService.gerarHash("123456")).thenReturn("HASHED");
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(salvo);

        ResponseEntity<Funcionario> response = funcionarioController.cadastrar(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    // ------------------------------
    // TESTE DO GET /funcionarios
    // ------------------------------
    @Test
    void deveListarFuncionariosPorMercado() {
        Funcionario f = new Funcionario();
        f.setId(1L);

        when(funcionarioRepository.findByIdMercado(10))
                .thenReturn(List.of(f));

        ResponseEntity<List<Funcionario>> response =
                funcionarioController.listarTodos(10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(funcionarioRepository).findByIdMercado(10);
    }

    // ------------------------------
    // TESTE DO PUT /funcionarios/{id}
    // ------------------------------
    @Test
    void deveAtualizarFuncionarioQuandoExistir() {
        Funcionario existente = new Funcionario();
        existente.setId(1L);
        existente.setSenha("ANTIGA");

        FuncionarioRequest req = new FuncionarioRequest();
        req.setNome("Novo Nome");
        req.setEmail("novo@email.com");
        req.setSenha("novaSenha");
        req.setAtivo(true);
        req.setCargo("Administrador");
        req.setIdMercado(55);

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(senhaService.gerarHash("novaSenha")).thenReturn("HASHED");
        when(funcionarioRepository.save(existente)).thenReturn(existente);

        ResponseEntity<Funcionario> response =
                funcionarioController.atualizar(1L, req);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Novo Nome", existente.getNome());
        assertEquals("HASHED", existente.getSenha());
        verify(funcionarioRepository).save(existente);
    }

    @Test
    void deveRetornarNotFoundQuandoFuncionarioNaoExistir() {
        FuncionarioRequest req = new FuncionarioRequest();

        when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Funcionario> response =
                funcionarioController.atualizar(99L, req);

        assertEquals(404, response.getStatusCodeValue());
        verify(funcionarioRepository, never()).save(any());
    }

}
