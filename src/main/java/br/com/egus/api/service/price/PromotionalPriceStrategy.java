package br.com.egus.api.service.price;

import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
public class PromotionalPriceStrategy implements PriceStrategy {
    @Override
    public boolean supports(ProdutoMercado pm, Promocao promo) {
        return promo != null;
    }

    @Override
    public double calcular(ProdutoMercado pm, Promocao promo) {
        return promo.getPrecoPromocional();
    }
}