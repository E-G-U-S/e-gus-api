package br.com.egus.api.model.produto;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido", schema = "mkt_auto")
public class PedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_cupom")
    private Integer idCupom;

    @Column(name = "id_mercado", nullable = false)
    private Integer idMercado;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItemModel> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "cpf", insertable = false, updatable = false)
    private br.com.egus.api.model.pessoa.Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
    private br.com.egus.api.model.pessoa.Usuario usuario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

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

    public List<PedidoItemModel> getItens() { return itens; }
    public void setItens(List<PedidoItemModel> itens) {
        this.itens.clear();
        if (itens != null) this.itens.addAll(itens);
    }

    public void addItem(PedidoItemModel item) {
        item.setPedido(this);
        this.itens.add(item);
    }

    public void removeItem(PedidoItemModel item) {
        item.setPedido(null);
        this.itens.remove(item);
    }

}
