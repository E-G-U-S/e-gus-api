package br.com.egus.api.service.price;

import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPriceStrategyTest {

    private final DefaultPriceStrategy strategy = new DefaultPriceStrategy();

    @Test
    void testSupports_AlwaysTrue() {
        ProdutoMercado pm = new ProdutoMercado();
        Promocao promo = new Promocao();

        assertTrue(strategy.supports(pm, promo));
        assertTrue(strategy.supports(pm, null));
        assertTrue(strategy.supports(null, promo));
    }

    @Test
    void testCalcular_ReturnsProdutoMercadoPreco() {
        ProdutoMercado pm = new ProdutoMercado();
        pm.setPreco(25.50);

        double result = strategy.calcular(pm, null);

        assertEquals(25.50, result);
    }
}
