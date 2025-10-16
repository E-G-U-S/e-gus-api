package br.com.egus.api.controller;

import br.com.egus.api.AbstactIntegrationTest; 
import br.com.egus.api.dto.FuncionarioRequest;
import br.com.egus.api.model.pessoa.Cargo;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest; 
import org.junit.jupiter.params.provider.CsvSource; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference; 
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FuncionarioControllerIntegrationTest extends AbstactIntegrationTest { 

    @LocalServerPort
    private int port; 

    @Autowired
    private TestRestTemplate restTemplate; 

    @Autowired
    private FuncionarioRepository funcionarioRepository; 

    @BeforeEach
    void setUp() {
        funcionarioRepository.deleteAll();
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/funcionarios";
    }

    private void prepararDadosParaListagem() {
        funcionarioRepository.deleteAll(); 
        
        Funcionario f1 = new Funcionario(null, "Maria M1", "maria.m1@test.com", "hash", true, Cargo.Estoquista, 1);
        Funcionario f2 = new Funcionario(null, "Joao M1", "joao.m1@test.com", "hash", true, Cargo.Administrador, 1);
        Funcionario f3 = new Funcionario(null, "Pedro M2", "pedro.m2@test.com", "hash", true, Cargo.Estoquista, 2);
        
        funcionarioRepository.saveAll(List.of(f1, f2, f3));
    }


    // -------------------------------------------------------------------------
    // TESTES PARA POST /funcionarios (Cadastrar)
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
        "Administrador, true, 1, 200",
        "Estoquista, false, 2, 200",
        "CARGO_INVALIDO, true, 3, 400" // Testa a falha de Enum.valueOf()
    })
    @DisplayName("POST (Parametrizado) deve cadastrar ou retornar 400 para cargo inválido")
    void deveCadastrarFuncionarioComDiferentesCargosEStatus(String cargo, boolean ativo, int idMercado, int expectedStatus) {
        
        
        String nome = "Funcionario " + cargo;
        String email = "email." + cargo.toLowerCase() + "@egus.com.br";
        String senha = "senha123";

        FuncionarioRequest request = new FuncionarioRequest(
                nome, email, senha, ativo, cargo, idMercado
        );
        
       
        ResponseEntity<Funcionario> response = restTemplate.postForEntity(
                getBaseUrl(), 
                request, 
                Funcionario.class
        );

        
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);

        if (expectedStatus == 200) {
            Funcionario funcionarioSalvo = response.getBody();
            Optional<Funcionario> dbCheck = funcionarioRepository.findById(funcionarioSalvo.getId());
            assertThat(dbCheck).isPresent();
            
            assertThat(dbCheck.get().getSenha()).isNotEqualTo(senha); 
            assertThat(dbCheck.get().getCargo()).isEqualTo(Cargo.valueOf(cargo));
        }
    }
    
    @Test
    @DisplayName("POST deve retornar 409 Conflict ao tentar cadastrar funcionário com email duplicado")
    void deveRetornar409AoCadastrarEmailDuplicado() {
        
        Funcionario f1 = new Funcionario(null, "Teste Email", "duplicado@test.com", "hash", true, Cargo.Administrador, 1);
        funcionarioRepository.save(f1);

        FuncionarioRequest request = new FuncionarioRequest(
                "Teste Duplicado", "duplicado@test.com", "senha456", true, "Estoquista", 1
        );
       
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(), 
                request, 
                String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT); // 409
        assertThat(funcionarioRepository.count()).isEqualTo(1); 
    }
    
    // -------------------------------------------------------------------------
    // TESTES PARA GET /funcionarios (Listar Todos)
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
        "1, 2", 
        "2, 1", 
        "99, 0" 
    })
    @DisplayName("GET (Parametrizado) deve listar corretamente o número de funcionários por idMercado")
    void deveListarFuncionariosCorretamentePorIdMercado(int idMercado, int expectedCount) {
        
        prepararDadosParaListagem(); 
        
        ParameterizedTypeReference<List<Funcionario>> responseType = 
            new ParameterizedTypeReference<List<Funcionario>>() {};

        String url = getBaseUrl() + "?idMercado=" + idMercado;
        
        ResponseEntity<List<Funcionario>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Funcionario> funcionarios = response.getBody();
        assertThat(funcionarios).isNotNull();
        
        assertThat(funcionarios.size()).isEqualTo(expectedCount); 
        
        if (expectedCount > 0) {
            assertThat(funcionarios).allMatch(f -> f.getIdMercado() == idMercado);
        }
    }
    
    // -------------------------------------------------------------------------
    // TESTES PARA PUT /funcionarios/{id} (Atualizar)
    // -------------------------------------------------------------------------
    
    @ParameterizedTest
    @CsvSource({
        "Nova Senha, true",  // Com nova senha (deve hashear)
        "'', false",         // Sem nova senha (deve manter a antiga)
        "null, false"        // Sem nova senha (deve manter a antiga)
    })
    @DisplayName("PUT (Parametrizado) deve atualizar e tratar a senha corretamente")
    void deveAtualizarFuncionarioTratandoASenhaCorretamente(String novaSenhaInput, boolean senhaDeveMudar) {
        
        final String novaSenha = novaSenhaInput.equals("null") ? null : novaSenhaInput;
        final String OLD_HASH = "old-hash-abc123";
        final long FUNC_ID = 100L;
        
        Funcionario existente = new Funcionario(FUNC_ID, "Antigo Nome", "antigo@email.com", OLD_HASH, true, Cargo.Administrador, 1);
        Funcionario salvo = funcionarioRepository.save(existente);
        
        FuncionarioRequest updateRequest = new FuncionarioRequest(
                "Novo Nome", "novo@email.com", novaSenha, false, "Estoquista", 2
        );

        String url = getBaseUrl() + "/" + salvo.getId();
        RequestEntity<FuncionarioRequest> requestEntity = new RequestEntity<>(
                updateRequest, HttpMethod.PUT, URI.create(url)
        );

        ResponseEntity<Funcionario> response = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, Funcionario.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<Funcionario> dbCheck = funcionarioRepository.findById(salvo.getId());
        
        if (senhaDeveMudar) {
            assertThat(dbCheck.get().getSenha()).isNotEqualTo(OLD_HASH);
        } else {
            assertThat(dbCheck.get().getSenha()).isEqualTo(OLD_HASH); 
        }
        
        assertThat(dbCheck.get().getNome()).isEqualTo("Novo Nome");
        assertThat(dbCheck.get().getCargo()).isEqualTo(Cargo.Estoquista);
    }
    
    @Test
    @DisplayName("PUT /funcionarios/{id} deve retornar 404 Not Found quando o ID for inexistente")
    void deveRetornar404QuandoAtualizarComIdInexistente() {
        final long ID_INEXISTENTE = 999L;
        FuncionarioRequest request = new FuncionarioRequest(
                "Nome Qualquer", "email@test.com", null, true, "Estoquista", 1
        );
        
        String url = getBaseUrl() + "/" + ID_INEXISTENTE;
        RequestEntity<FuncionarioRequest> requestEntity = new RequestEntity<>(
                request, HttpMethod.PUT, URI.create(url)
        );
        
        ResponseEntity<Funcionario> response = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, Funcionario.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}