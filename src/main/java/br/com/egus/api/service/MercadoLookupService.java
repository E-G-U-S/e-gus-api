package br.com.egus.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class MercadoLookupService {

    @PersistenceContext
    private EntityManager entityManager;

    public String obterNome(Integer idMercado) {
        if (idMercado == null) {
            return null;
        }
        // Consulta direta na tabela correta
        return buscarNomePorTabela("mkt_auto.mercados", idMercado);
    }

    private String buscarNomePorTabela(String tabela, Integer idMercado) {
        try {
            Object result = entityManager
                    .createNativeQuery("SELECT nome FROM " + tabela + " WHERE id_mercado = :idMercado")
                    .setParameter("idMercado", idMercado)
                    .getSingleResult();
            return result != null ? result.toString() : null;
        } catch (NoResultException e) {
            return null;
        } catch (RuntimeException e) {
            // Inclui erros como tabela inexistente; retornamos null para tentar alternativa
            return null;
        }
    }
}