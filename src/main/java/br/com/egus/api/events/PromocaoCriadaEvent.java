package br.com.egus.api.events;

import br.com.egus.api.model.produto.Promocao;

public class PromocaoCriadaEvent {
    private final Promocao promocao;

    public PromocaoCriadaEvent(Promocao promocao) {
        this.promocao = promocao;
    }

    public Promocao getPromocao() {
        return promocao;
    }
}