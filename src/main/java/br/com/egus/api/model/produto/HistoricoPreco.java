package br.com.egus.api.model.produto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_preco", schema = "mkt_auto")
public class HistoricoPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto_mercado", nullable = false)
    private ProdutoMercado produtoMercado;

    @Column(name = "preco_antigo", nullable = false)
    private Double precoAntigo;

    @Column(name = "preco_novo", nullable = false)
    private Double precoNovo;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "id_funcionario", nullable = false)
    private Integer idFuncionario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ProdutoMercado getProdutoMercado() { return produtoMercado; }
    public void setProdutoMercado(ProdutoMercado produtoMercado) { this.produtoMercado = produtoMercado; }

    public Double getPrecoAntigo() { return precoAntigo; }
    public void setPrecoAntigo(Double precoAntigo) { this.precoAntigo = precoAntigo; }

    public Double getPrecoNovo() { return precoNovo; }
    public void setPrecoNovo(Double precoNovo) { this.precoNovo = precoNovo; }

    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime dataAlteracao) { this.dataAlteracao = dataAlteracao; }

    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }
}