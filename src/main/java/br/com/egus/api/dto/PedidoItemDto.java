package br.com.egus.api.dto;

public class PedidoItemDto {

    private Integer produtoMercadoId;
    private Integer quantidade;
    private Double precoUnitario;

    public PedidoItemDto() {
    }

    public PedidoItemDto(Integer produtoMercadoId, Integer quantidade, Double precoUnitario) {
        this.produtoMercadoId = produtoMercadoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Integer getProdutoMercadoId() { return produtoMercadoId; }
    public void setProdutoMercadoId(Integer produtoMercadoId) { this.produtoMercadoId = produtoMercadoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }

    public Double getSubtotal() {
        if (precoUnitario == null || quantidade == null) return 0.0;
        return precoUnitario * quantidade;
    }
}
