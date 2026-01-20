package br.com.egus.api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PedidoDto {

    private Long id;
    private Long idUsuario;
    private Integer idCupom;
    private Integer idMercado;
    private LocalDateTime dataHora;
    private Double valorTotal;
    private List<PedidoItemDto> itens = new ArrayList<>();

    public PedidoDto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdCupom() { return idCupom; }
    public void setIdCupom(Integer idCupom) { this.idCupom = idCupom; }

    public Integer getIdMercado() { return idMercado; }
    public void setIdMercado(Integer idMercado) { this.idMercado = idMercado; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public List<PedidoItemDto> getItens() { return itens; }
    public void setItens(List<PedidoItemDto> itens) { this.itens = itens == null ? new ArrayList<>() : itens; }
}
