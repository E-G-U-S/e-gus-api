package br.com.egus.api.model.pessoa;

import jakarta.persistence.*;

@Entity
@Table(name = "funcionario", schema = "mkt_auto")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    private Cargo cargo;

    @Column(name = "id_mercado")
    private Integer idMercado;

    public Funcionario() {}

    public Funcionario(Long id, String nome, String email, String senha,
                       Boolean ativo, Cargo cargo, Integer idMercado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        this.cargo = cargo;
        this.idMercado = idMercado;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public Integer getIdMercado() { return idMercado; }
    public void setIdMercado(Integer idMercado) { this.idMercado = idMercado; }
}
