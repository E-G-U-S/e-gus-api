package br.com.egus.api.service.price;

import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromotionalPriceStrategyTest {

    private final PromotionalPriceStrategy strategy = new PromotionalPriceStrategy();

    @Test
    void testSupports_WhenPromoNotNull_ReturnsTrue() {
        ProdutoMercado pm = new ProdutoMercado();
        Promocao promo = new Promocao();

        assertTrue(strategy.supports(pm, promo));
    }

    @Test
    void testSupports_WhenPromoNull_ReturnsFalse() {
        ProdutoMercado pm = new ProdutoMercado();
        assertFalse(strategy.supports(pm, null));
    }

    @Test
    void testCalcular_ReturnsPrecoPromocional() {
        ProdutoMercado pm = new ProdutoMercado();

        Promocao promo = new Promocao();
        promo.setPrecoPromocional(9.99);

        double result = strategy.calcular(pm, promo);

        assertEquals(9.99, result);
    }
}
