package br.com.egus.api.model.produto;

import jakarta.persistence.*;

@Entity
@Table(name = "produto", schema = "mkt_auto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "codigo_barras", unique = true)
    private String codigoBarras;

    @Column(name = "is_maior_idade", nullable = false)
    private Boolean maiorIdade;

    @Column(name = "url_imagem")
    private String urlImagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public Boolean getMaiorIdade() { return maiorIdade; }
    public void setMaiorIdade(Boolean maiorIdade) { this.maiorIdade = maiorIdade; }

    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}