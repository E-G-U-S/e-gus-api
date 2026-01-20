package br.com.egus.api.service;

import br.com.egus.api.dto.PedidoDto;
import br.com.egus.api.dto.PedidoItemDto;
import br.com.egus.api.model.produto.PedidoItemModel;
import br.com.egus.api.model.produto.PedidoModel;
import br.com.egus.api.model.produto.ProdutoMercado;
import br.com.egus.api.repository.PedidoRepository;
 
import br.com.egus.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final br.com.egus.api.repository.ProdutoMercadoRepository produtoMercadoRepository;
    private final br.com.egus.api.repository.ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         br.com.egus.api.repository.ProdutoMercadoRepository produtoMercadoRepository,
                         br.com.egus.api.repository.ProdutoRepository produtoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoMercadoRepository = produtoMercadoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoDto criarPedido(PedidoDto dto) {

        // 1 — Tenta popular idUsuario caso não tenha sido informado
        if (dto.getIdUsuario() == null) {
            // tenta obter do principal autenticado (se disponível)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                Object principal = auth.getPrincipal();
                if (principal instanceof br.com.egus.api.model.pessoa.Usuario) {
                    dto.setIdUsuario(((br.com.egus.api.model.pessoa.Usuario) principal).getId());
                } else if (principal instanceof UserDetails) {
                    String username = ((UserDetails) principal).getUsername();
                    // assume username is email
                    usuarioRepository.findByEmail(username).ifPresent(u -> dto.setIdUsuario(u.getId()));
                }
            }
        }

        // valida obrigatoriedade de idUsuario
        if (dto.getIdUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idUsuario é obrigatório");
        }

        Integer resolvedMercadoId = resolveIdMercado(dto);
        if (resolvedMercadoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idMercado é obrigatório");
        }

        // Verifica se usuário existe (busca por id)
        usuarioRepository.findById(dto.getIdUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        // 2 — Cria Pedido e adiciona itens
        PedidoModel pedido = new PedidoModel();
        // armazenamos o id do usuário em idUsuario (coluna id_usuario)
        pedido.setIdUsuario(dto.getIdUsuario());
        pedido.setIdCupom(dto.getIdCupom());
        pedido.setIdMercado(resolvedMercadoId);
        pedido.setDataHora(dto.getDataHora() == null ? LocalDateTime.now() : dto.getDataHora());

        // map items to entities and attach before saving so cascade persists items
        if (dto.getItens() != null) {
            for (PedidoItemDto itemDto : dto.getItens()) {
                PedidoItemModel item = new PedidoItemModel();

                ProdutoMercado pm = null;

                if (itemDto.getProdutoMercadoId() != null) {
                    pm = produtoMercadoRepository.findById(itemDto.getProdutoMercadoId())
                            .orElseThrow(() -> new IllegalArgumentException("ProdutoMercado não encontrado: " + itemDto.getProdutoMercadoId()));
                    item.setProduto(pm.getProduto());
                    if (pedido.getIdMercado() != null && !pedido.getIdMercado().equals(pm.getIdMercado())) {
                        throw new IllegalArgumentException("ProdutoMercado não pertence ao mercado do pedido: " + pm.getId());
                    }
                } else if (itemDto.getProdutoId() != null) {
                    var produto = produtoRepository.findById(itemDto.getProdutoId())
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDto.getProdutoId()));
                    item.setProduto(produto);
                    var listaPm = produtoMercadoRepository.findAllByProdutoIdFetch(produto.getId());
                    pm = listaPm.stream().filter(x -> pedido.getIdMercado().equals(x.getIdMercado())).findFirst()
                            .orElse(null);
                    if (pm == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto não disponível no mercado informado");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe produtoMercadoId ou produtoId em cada item");
                }

                item.setQuantidade(itemDto.getQuantidade());
                Double precoUnit = itemDto.getPrecoUnitario();
                if (precoUnit == null) {
                    precoUnit = pm != null ? pm.getPreco() : null;
                }
                if (precoUnit == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço do item não informado e não encontrado para o mercado");
                }
                item.setPrecoUnitario(precoUnit);

                Double valorItem = (item.getPrecoUnitario() == null || item.getQuantidade() == null) ? 0.0
                        : item.getPrecoUnitario() * item.getQuantidade();
                item.setValorItemTotal(valorItem);

                if (pm != null) {
                    int estoqueAtual = pm.getEstoque() == null ? 0 : pm.getEstoque();
                    int quantidade = item.getQuantidade() == null ? 0 : item.getQuantidade();
                    if (quantidade > estoqueAtual) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para produtoMercado: " + pm.getId());
                    }
                    pm.setEstoque(estoqueAtual - quantidade);
                    pm.setDataAtualizacao(LocalDateTime.now());
                }
                pedido.addItem(item);
            }
        }

            // compute total if not provided (use valorItemTotal to match DB field)
            double soma = dto.getValorTotal() == null ? pedido.getItens().stream().mapToDouble(i -> i.getValorItemTotal() == null ? 0.0 : i.getValorItemTotal()).sum() : dto.getValorTotal();
        pedido.setValorTotal(soma);

        PedidoModel savedPedido = pedidoRepository.save(pedido);

        return toDto(savedPedido);
    }

    public Optional<PedidoDto> buscarPorId(Integer id) {
        return pedidoRepository.findById(id).map(this::toDto);
    }

    private PedidoDto toDto(PedidoModel pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(pedido.getId() == null ? null : pedido.getId().longValue());
        dto.setIdUsuario(pedido.getIdUsuario());
        dto.setIdCupom(pedido.getIdCupom());
        dto.setIdMercado(pedido.getIdMercado());
        dto.setDataHora(pedido.getDataHora());
        dto.setValorTotal(pedido.getValorTotal());

        List<PedidoItemDto> itens = pedido.getItens() == null ? List.of()
                : pedido.getItens().stream().map(it -> {
                    PedidoItemDto itemDto = new PedidoItemDto();
                    if (it.getProduto() != null) itemDto.setProdutoMercadoId(findProdutoMercadoIdFor(it.getProduto().getId(), pedido.getIdMercado()));
                    itemDto.setQuantidade(it.getQuantidade());
                    itemDto.setPrecoUnitario(it.getPrecoUnitario());
                    return itemDto;
                }).collect(Collectors.toList());

        dto.setItens(itens);

        return dto;
    }

    private Integer findProdutoMercadoIdFor(Long produtoId, Integer mercadoId) {
        if (produtoId == null || mercadoId == null) return null;
        var list = produtoMercadoRepository.findAllByProdutoIdFetch(produtoId);
        return list.stream().filter(pm -> mercadoId.equals(pm.getIdMercado())).findFirst().map(ProdutoMercado::getId).orElse(null);
    }

    private Integer resolveIdMercado(PedidoDto dto) {
        Integer id = dto.getIdMercado();
        if (id != null) {
            var pmOpt = produtoMercadoRepository.findById(id);
            if (pmOpt.isPresent()) {
                return pmOpt.get().getIdMercado();
            }
            return id;
        }
        if (dto.getItens() != null) {
            for (PedidoItemDto i : dto.getItens()) {
                if (i.getProdutoMercadoId() != null) {
                    var pm = produtoMercadoRepository.findById(i.getProdutoMercadoId()).orElse(null);
                    if (pm != null) return pm.getIdMercado();
                }
            }
        }
        return null;
    }
}

