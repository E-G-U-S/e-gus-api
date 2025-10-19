package br.com.egus.api.listener;

import br.com.egus.api.events.PromocaoCriadaEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class PromocaoEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPromocaoCriada(PromocaoCriadaEvent event) {
        // Espaço para reações futuras:notificação
    }
}