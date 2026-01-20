package br.com.egus.api.model.produto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promocao", schema = "mkt_auto")
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto_mercado", nullable = false)
    private ProdutoMercado produtoMercado;

    @Column(name = "id_funcionario", nullable = false)
    private Integer idFuncionario;

    @Column(name = "preco_promocional", nullable = false)
    private Double precoPromocional;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_validade", nullable = false)
    private LocalDateTime dataValidade;

    @Column(name = "descricao")
    private String descricao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ProdutoMercado getProdutoMercado() { return produtoMercado; }
    public void setProdutoMercado(ProdutoMercado produtoMercado) { this.produtoMercado = produtoMercado; }

    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }

    public Double getPrecoPromocional() { return precoPromocional; }
    public void setPrecoPromocional(Double precoPromocional) { this.precoPromocional = precoPromocional; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}