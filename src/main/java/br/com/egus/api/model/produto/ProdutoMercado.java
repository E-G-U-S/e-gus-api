package br.com.egus.api.model.produto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto_mercado", schema = "mkt_auto")
public class ProdutoMercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "id_mercado", nullable = false)
    private Integer idMercado;

    @Column(name = "preco", nullable = false)
    private Double preco;

    @Column(name = "estoque")
    private Integer estoque;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getIdMercado() { return idMercado; }
    public void setIdMercado(Integer idMercado) { this.idMercado = idMercado; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}