package br.com.egus.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class FuncionarioRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O formato do email é inválido")
    private String email;

    private String senha;

    @NotNull(message = "O status 'ativo' é obrigatório")
    private Boolean ativo;

    @Pattern(
            regexp = "^(Administrador|Estoquista)$",
            message = "O cargo é obrigatório e deve ser um dos valores: Administrador, Estoquista"
    )
    private String cargo;

    @NotNull(message = "O ID do mercado é obrigatório")
    private Integer idMercado;

    public FuncionarioRequest() {}

    public FuncionarioRequest(String nome, String email, String senha, Boolean ativo, String cargo, Integer idMercado) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        this.cargo = cargo;
        this.idMercado = idMercado;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Integer getIdMercado() { return idMercado; }
    public void setIdMercado(Integer idMercado) { this.idMercado = idMercado; }
}
