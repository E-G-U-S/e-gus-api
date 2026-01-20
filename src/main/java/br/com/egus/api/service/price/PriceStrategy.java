package br.com.egus.api.service.price;

import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;

public interface PriceStrategy {
    boolean supports(ProdutoMercado pm, Promocao promo);
    double calcular(ProdutoMercado pm, Promocao promo);
}