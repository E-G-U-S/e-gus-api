package br.com.egus.api.dto;

public class ProdutoBaseResponse {
    private Long id;
    private String nome;
    private String categoria;
    private Integer categoriaId;
    private String imagemUrl;
    private Boolean maiorIdade;
    private String codigoBarras;

    public ProdutoBaseResponse(Long id, String nome, String categoria, Integer categoriaId,
                               String imagemUrl, Boolean maiorIdade, String codigoBarras) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.categoriaId = categoriaId;
        this.imagemUrl = imagemUrl;
        this.maiorIdade = maiorIdade;
        this.codigoBarras = codigoBarras;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public Integer getCategoriaId() { return categoriaId; }
    public String getImagemUrl() { return imagemUrl; }
    public Boolean getMaiorIdade() { return maiorIdade; }
    public String getCodigoBarras() { return codigoBarras; }
}