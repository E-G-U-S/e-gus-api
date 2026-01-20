package br.com.egus.api.service;

import br.com.egus.api.dto.ProdutoBaseResponse;
import br.com.egus.api.model.produto.Produto;
import br.com.egus.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoBaseService {

    private final ProdutoRepository produtoRepository;

    public ProdutoBaseService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoBaseResponse> listarTodos() {
        List<Produto> produtos = produtoRepository.findAllFetchCategoria();
        return produtos.stream().map(p -> new ProdutoBaseResponse(
                p.getId(),
                p.getNome(),
                p.getCategoria().getNome(),
                p.getCategoria().getId(),
                p.getUrlImagem(),
                p.getMaiorIdade(),
                p.getCodigoBarras()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProdutoBaseResponse> buscarPorId(Long id) {
        return produtoRepository.findById(id).map(p -> new ProdutoBaseResponse(
                p.getId(),
                p.getNome(),
                p.getCategoria().getNome(),
                p.getCategoria().getId(),
                p.getUrlImagem(),
                p.getMaiorIdade(),
                p.getCodigoBarras()
        ));
    }
}