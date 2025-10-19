package br.com.egus.api.events;

public class PrecoAlteradoEvent {
    private final int produtoMercadoId;
    private final double precoAntigo;
    private final double precoNovo;
    private final Integer idFuncionario;

    public PrecoAlteradoEvent(int produtoMercadoId, double precoAntigo, double precoNovo, Integer idFuncionario) {
        this.produtoMercadoId = produtoMercadoId;
        this.precoAntigo = precoAntigo;
        this.precoNovo = precoNovo;
        this.idFuncionario = idFuncionario;
    }

    public int getProdutoMercadoId() { return produtoMercadoId; }
    public double getPrecoAntigo() { return precoAntigo; }
    public double getPrecoNovo() { return precoNovo; }
    public Integer getIdFuncionario() { return idFuncionario; }
}