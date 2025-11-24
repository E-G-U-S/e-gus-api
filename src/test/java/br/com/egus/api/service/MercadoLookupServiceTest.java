package br.com.egus.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MercadoLookupServiceTest {

    private MercadoLookupService service;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        service = new MercadoLookupService();

        try {
            var field = MercadoLookupService.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            fail("Falha ao injetar EntityManager");
        }
    }

    @Test
    void deveRetornarNullQuandoIdForNull() {
        assertNull(service.obterNome(null));
    }

    @Test
    void deveRetornarNomeQuandoEncontrado() {
        Query query = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("idMercado"), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn("Mercado XPTO");

        String nome = service.obterNome(10);

        assertEquals("Mercado XPTO", nome);
    }

    @Test
    void deveRetornarNullQuandoNaoEncontrar() {
        Query query = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("idMercado"), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        String nome = service.obterNome(999);

        assertNull(nome);
    }

    @Test
    void deveRetornarNullQuandoDerErroNaQuery() {
        when(entityManager.createNativeQuery(anyString()))
                .thenThrow(new RuntimeException("Erro na tabela"));

        String nome = service.obterNome(1);

        assertNull(nome);
    }
}
