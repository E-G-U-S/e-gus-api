package br.com.egus.api.service;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.model.produto.Categoria;
import br.com.egus.api.model.produto.Produto;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.repository.ProdutoMercadoRepository;
import br.com.egus.api.repository.PromocaoRepository;
import br.com.egus.api.service.price.PriceStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoMercadoRepository produtoMercadoRepository;

    @Mock
    private PromocaoRepository promocaoRepository;

    @Mock
    private MercadoLookupService mercadoLookupService;

    @Mock
    private PriceStrategy priceStrategy1;

    @Mock
    private PriceStrategy priceStrategy2;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        produtoService = new ProdutoService(
                produtoMercadoRepository,
                promocaoRepository,
                List.of(priceStrategy1, priceStrategy2),
                mercadoLookupService
        );
    }

    private ProdutoMercado criarProdutoMercado() {
        Categoria cat = new Categoria();
        cat.setNome("Bebidas");

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Coca-Cola");
        produto.setCategoria(cat);
        produto.setUrlImagem("img.png");
        produto.setMaiorIdade(false);

        ProdutoMercado pm = new ProdutoMercado();
        pm.setId(10);
        pm.setIdMercado(5);
        pm.setProduto(produto);
        pm.setPreco(10.0);
        pm.setEstoque(20);

        return pm;
    }

    @Test
    void listarPorMercado_quandoNaoHaPromocao_deveRetornarPrecoNormal() {
        ProdutoMercado pm = criarProdutoMercado();
        when(produtoMercadoRepository.findAllByIdMercadoFetch(5))
                .thenReturn(List.of(pm));

        when(promocaoRepository.findActiveByProdutoMercado(eq(10), any()))
                .thenReturn(Optional.empty());

        when(priceStrategy1.supports(eq(pm), isNull())).thenReturn(false);
        when(priceStrategy2.supports(eq(pm), isNull())).thenReturn(true);
        when(priceStrategy2.calcular(pm, null)).thenReturn(10.0);

        when(mercadoLookupService.obterNome(5)).thenReturn("Mercado XP");

        List<ProdutoResponse> result = produtoService.listarPorMercado(5);

        assertThat(result).hasSize(1);
        ProdutoResponse r = result.get(0);

        assertThat(r.getPrecoFinal()).isEqualTo(10.0);
        assertThat(r.getPrecoBase()).isEqualTo(10.0);
        assertThat(r.getPrecoPromocional()).isNull();
        assertThat(r.getMercadoNome()).isEqualTo("Mercado XP");
    }

    @Test
    void listarPorMercado_quandoHaPromocao_deveAplicarPrecoPromocional() {
        ProdutoMercado pm = criarProdutoMercado();
        when(produtoMercadoRepository.findAllByIdMercadoFetch(5))
                .thenReturn(List.of(pm));

        Promocao promo = new Promocao();
        promo.setId(100);
        promo.setPrecoPromocional(7.5);

        when(promocaoRepository.findActiveByProdutoMercado(eq(10), any()))
                .thenReturn(Optional.of(promo));

        when(priceStrategy1.supports(pm, promo)).thenReturn(true);
        when(priceStrategy1.calcular(pm, promo)).thenReturn(7.5);

        when(priceStrategy2.supports(pm, promo)).thenReturn(false);

        when(mercadoLookupService.obterNome(5)).thenReturn("Mercado XP");

        List<ProdutoResponse> result = produtoService.listarPorMercado(5);

        ProdutoResponse r = result.get(0);

        assertThat(r.getPrecoFinal()).isEqualTo(7.5);
        assertThat(r.getPrecoPromocional()).isEqualTo(7.5);
    }

    @Test
    void listarPorMercado_quandoNenhumaStrategySuporta_deveUsarPrecoBase() {
        ProdutoMercado pm = criarProdutoMercado();

        when(produtoMercadoRepository.findAllByIdMercadoFetch(5))
                .thenReturn(List.of(pm));

        when(promocaoRepository.findActiveByProdutoMercado(eq(10), any()))
                .thenReturn(Optional.empty());

        when(priceStrategy1.supports(pm, null)).thenReturn(false);
        when(priceStrategy2.supports(pm, null)).thenReturn(false);

        when(mercadoLookupService.obterNome(5)).thenReturn("Mercado XP");

        List<ProdutoResponse> result = produtoService.listarPorMercado(5);

        ProdutoResponse r = result.get(0);

        assertThat(r.getPrecoFinal()).isEqualTo(pm.getPreco());
    }

    @Test
    void listarPorProduto_deveRetornarItensCorretamente() {
        ProdutoMercado pm = criarProdutoMercado();

        when(produtoMercadoRepository.findAllByProdutoIdFetch(1L))
                .thenReturn(List.of(pm));

        when(promocaoRepository.findActiveByProdutoMercado(eq(10), any()))
                .thenReturn(Optional.empty());

        when(priceStrategy1.supports(pm, null)).thenReturn(false);
        when(priceStrategy2.supports(pm, null)).thenReturn(true);
        when(priceStrategy2.calcular(pm, null)).thenReturn(10.0);

        when(mercadoLookupService.obterNome(5)).thenReturn("Mercado XP");

        List<ProdutoResponse> result = produtoService.listarPorProduto(1L);

        ProdutoResponse r = result.get(0);

        assertThat(r.getMercadoNome()).isEqualTo("Mercado XP");
        assertThat(r.getPrecoFinal()).isEqualTo(10.0);
    }
}
