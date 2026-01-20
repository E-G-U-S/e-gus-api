package br.com.egus.api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaServiceTest {

    private final SenhaService senhaService = new SenhaService();

    @Test
    void gerarHash_deveGerarHashDiferenteDaSenhaEmTexto() {
        // Arrange
        String senha = "123456";

        // Act
        String hash = senhaService.gerarHash(senha);

        // Assert
        assertNotNull(hash);
        assertNotEquals(senha, hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "Hash deve ser BCrypt");
    }

    @Test
    void validarSenha_quandoSenhaCorreta_deveRetornarTrue() {
        // Arrange
        String senha = "minhaSenhaTop";
        String hash = senhaService.gerarHash(senha);

        // Act
        boolean resultado = senhaService.validarSenha(senha, hash);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void validarSenha_quandoSenhaIncorreta_deveRetornarFalse() {
        // Arrange
        String senhaCorreta = "abc123";
        String hash = senhaService.gerarHash(senhaCorreta);

        // Act
        boolean resultado = senhaService.validarSenha("errada", hash);

        // Assert
        assertFalse(resultado);
    }
}
