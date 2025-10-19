package br.com.egus.api.service;

import br.com.egus.api.dto.ProdutoResponse;
import br.com.egus.api.events.PrecoAlteradoEvent;
import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.repository.PromocaoRepository;
import br.com.egus.api.repository.ProdutoMercadoRepository;
import br.com.egus.api.service.MercadoLookupService;
import br.com.egus.api.service.price.PriceStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoMercadoRepository produtoMercadoRepository;
    private final PromocaoRepository promocaoRepository;
    private final List<PriceStrategy> priceStrategies;
    private final MercadoLookupService mercadoLookupService;
    private final ApplicationEventPublisher eventPublisher;

    public ProdutoService(ProdutoMercadoRepository produtoMercadoRepository,
                          PromocaoRepository promocaoRepository,
                          List<PriceStrategy> priceStrategies,
                          MercadoLookupService mercadoLookupService,
                          ApplicationEventPublisher eventPublisher) {
        this.produtoMercadoRepository = produtoMercadoRepository;
        this.promocaoRepository = promocaoRepository;
        this.priceStrategies = priceStrategies;
        this.mercadoLookupService = mercadoLookupService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarPorMercado(int mercadoId) {
        List<ProdutoMercado> itens = produtoMercadoRepository.findAllByIdMercadoFetch(mercadoId);
        LocalDateTime agora = LocalDateTime.now();

        return itens.stream().map(pm -> {
            Optional<Promocao> promoOpt = promocaoRepository.findActiveByProdutoMercado(pm.getId(), agora);
            Promocao promo = promoOpt.orElse(null);

            double precoFinal = priceStrategies.stream()
                    .filter(s -> s.supports(pm, promo))
                    .findFirst()
                    .map(s -> s.calcular(pm, promo))
                    .orElse(pm.getPreco());

            return new ProdutoResponse(
                    pm.getProduto().getId(),
                    pm.getProduto().getNome(),
                    pm.getProduto().getCategoria().getNome(),
                    pm.getProduto().getUrlImagem(),
                    pm.getProduto().getMaiorIdade(),
                    precoFinal,
                    pm.getPreco(),
                    promo != null ? promo.getPrecoPromocional() : null,
                    pm.getEstoque(),
                    mercadoLookupService.obterNome(pm.getIdMercado()),
                    pm.getId()
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarPorProduto(Long produtoId) {
        List<ProdutoMercado> itens = produtoMercadoRepository.findAllByProdutoIdFetch(produtoId);
        LocalDateTime agora = LocalDateTime.now();

        return itens.stream().map(pm -> {
            Optional<Promocao> promoOpt = promocaoRepository.findActiveByProdutoMercado(pm.getId(), agora);
            Promocao promo = promoOpt.orElse(null);

            double precoFinal = priceStrategies.stream()
                    .filter(s -> s.supports(pm, promo))
                    .findFirst()
                    .map(s -> s.calcular(pm, promo))
                    .orElse(pm.getPreco());

            return new ProdutoResponse(
                    pm.getProduto().getId(),
                    pm.getProduto().getNome(),
                    pm.getProduto().getCategoria().getNome(),
                    pm.getProduto().getUrlImagem(),
                    pm.getProduto().getMaiorIdade(),
                    precoFinal,
                    pm.getPreco(),
                    promo != null ? promo.getPrecoPromocional() : null,
                    pm.getEstoque(),
                    mercadoLookupService.obterNome(pm.getIdMercado()),
                    pm.getId()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void alterarPreco(Integer produtoMercadoId, Double novoPreco, Integer idFuncionario) {
        ProdutoMercado pm = produtoMercadoRepository.findById(produtoMercadoId)
                .orElseThrow(() -> new IllegalArgumentException("ProdutoMercado não encontrado: " + produtoMercadoId));
        Double precoAntigo = pm.getPreco();
        if (novoPreco != null && !novoPreco.equals(precoAntigo)) {
            pm.setPreco(novoPreco);
            pm.setDataAtualizacao(LocalDateTime.now());
            produtoMercadoRepository.save(pm);
            eventPublisher.publishEvent(new PrecoAlteradoEvent(pm.getId(), precoAntigo, novoPreco, idFuncionario));
        }
    }
}