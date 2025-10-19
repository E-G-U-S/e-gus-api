package br.com.egus.api.listener;

import br.com.egus.api.events.PrecoAlteradoEvent;
import br.com.egus.api.model.produto.HistoricoPreco;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.repository.HistoricoPrecoRepository;
import br.com.egus.api.repository.ProdutoMercadoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.time.LocalDateTime;

@Component
public class HistoricoPrecoListener {

    private final HistoricoPrecoRepository historicoPrecoRepository;
    private final ProdutoMercadoRepository produtoMercadoRepository;

    public HistoricoPrecoListener(HistoricoPrecoRepository historicoPrecoRepository,
                                  ProdutoMercadoRepository produtoMercadoRepository) {
        this.historicoPrecoRepository = historicoPrecoRepository;
        this.produtoMercadoRepository = produtoMercadoRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPrecoAlterado(PrecoAlteradoEvent event) {
        ProdutoMercado pm = produtoMercadoRepository.findById(event.getProdutoMercadoId()).orElse(null);
        if (pm == null) {
            return;
        }
        HistoricoPreco h = new HistoricoPreco();
        h.setProdutoMercado(pm);
        h.setPrecoAntigo(event.getPrecoAntigo());
        h.setPrecoNovo(event.getPrecoNovo());
        h.setDataAlteracao(LocalDateTime.now());
        h.setIdFuncionario(event.getIdFuncionario());
        historicoPrecoRepository.save(h);
    }
}