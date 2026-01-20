package br.com.egus.api.dto;

public class ProdutoResponse {
    private Long id;
    private String nome;
    private String categoria;
    private String imagemUrl;
    private Boolean maiorIdade;
    private Double precoFinal;
    private Double precoBase;
    private Double precoPromocional;
    private Integer estoque;
    private String mercadoNome;
    private Integer produtoMercadoId;

    public ProdutoResponse(Long id, String nome, String categoria, String imagemUrl, Boolean maiorIdade,
                           Double precoFinal, Double precoBase, Double precoPromocional,
                           Integer estoque, String mercadoNome, Integer produtoMercadoId) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.maiorIdade = maiorIdade;
        this.precoFinal = precoFinal;
        this.precoBase = precoBase;
        this.precoPromocional = precoPromocional;
        this.estoque = estoque;
        this.mercadoNome = mercadoNome;
        this.produtoMercadoId = produtoMercadoId;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getImagemUrl() { return imagemUrl; }
    public Boolean getMaiorIdade() { return maiorIdade; }
    public Double getPrecoFinal() { return precoFinal; }
    public Double getPrecoBase() { return precoBase; }
    public Double getPrecoPromocional() { return precoPromocional; }
    public Integer getEstoque() { return estoque; }
    public String getMercadoNome() { return mercadoNome; }
    public Integer getProdutoMercadoId() { return produtoMercadoId; }
}