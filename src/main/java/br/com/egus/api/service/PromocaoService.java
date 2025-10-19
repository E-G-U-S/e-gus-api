package br.com.egus.api.service;

import br.com.egus.api.events.PromocaoCriadaEvent;
import br.com.egus.api.model.produto.Promocao;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.repository.PromocaoRepository;
import br.com.egus.api.repository.ProdutoMercadoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProdutoMercadoRepository produtoMercadoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PromocaoService(PromocaoRepository promocaoRepository,
                           ProdutoMercadoRepository produtoMercadoRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.promocaoRepository = promocaoRepository;
        this.produtoMercadoRepository = produtoMercadoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Promocao criarPromocao(Integer produtoMercadoId,
                                  Integer idFuncionario,
                                  Double precoPromocional,
                                  LocalDateTime dataValidade,
                                  String descricao) {
        ProdutoMercado pm = produtoMercadoRepository.findById(produtoMercadoId)
                .orElseThrow(() -> new IllegalArgumentException("ProdutoMercado não encontrado: " + produtoMercadoId));
        Promocao p = new Promocao();
        p.setProdutoMercado(pm);
        p.setIdFuncionario(idFuncionario);
        p.setPrecoPromocional(precoPromocional);
        p.setDataInicio(LocalDateTime.now());
        p.setDataValidade(dataValidade);
        p.setDescricao(descricao);
        Promocao saved = promocaoRepository.save(p);
        eventPublisher.publishEvent(new PromocaoCriadaEvent(saved));
        return saved;
    }
}