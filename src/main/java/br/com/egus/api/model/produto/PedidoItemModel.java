package br.com.egus.api.model.produto;

import jakarta.persistence.*;

@Entity
@Table(name = "itempedido", schema = "mkt_auto")
public class PedidoItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoModel pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    @Column(name = "valor_item_total", nullable = false, insertable = true, updatable = true)
    private Double valorItemTotal;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PedidoModel getPedido() { return pedido; }
    public void setPedido(PedidoModel pedido) { this.pedido = pedido; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }

    public Double getSubtotal() {
        if (precoUnitario == null || quantidade == null) return 0.0;
        return precoUnitario * quantidade;
    }

    public Double getValorItemTotal() { return valorItemTotal; }
    public void setValorItemTotal(Double valorItemTotal) { this.valorItemTotal = valorItemTotal; }

}
