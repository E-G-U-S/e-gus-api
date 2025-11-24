package br.com.egus.api.service;

import br.com.egus.api.dto.ProdutoBaseResponse;
import br.com.egus.api.model.produto.Categoria;
import br.com.egus.api.model.produto.Produto;
import br.com.egus.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoBaseServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoBaseService produtoBaseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Produto criarProduto(Long id) {
        Categoria categoria = new Categoria();
        categoria.setId(10);
        categoria.setNome("Bebidas");

        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Cerveja");
        produto.setCategoria(categoria);
        produto.setUrlImagem("img.png");
        produto.setMaiorIdade(true);
        produto.setCodigoBarras("123");

        return produto;
    }

    // -------------------------------------------------------------------------------------

    @Test
    void listarTodos_retornaListaMapeadaCorretamente() {
        Produto produto = criarProduto(1L);

        when(produtoRepository.findAllFetchCategoria())
                .thenReturn(List.of(produto));

        List<ProdutoBaseResponse> result = produtoBaseService.listarTodos();

        assertEquals(1, result.size());
        ProdutoBaseResponse dto = result.get(0);

        assertEquals(1L, dto.getId());
        assertEquals("Cerveja", dto.getNome());
        assertEquals("Bebidas", dto.getCategoria());
        assertEquals(10, dto.getCategoriaId());
        assertEquals("img.png", dto.getImagemUrl());
        assertEquals(true, dto.getMaiorIdade());
        assertEquals("123", dto.getCodigoBarras());

        verify(produtoRepository, times(1)).findAllFetchCategoria();
    }

    // -------------------------------------------------------------------------------------

    @Test
    void buscarPorId_existente_retornaProdutoMapeado() {
        Produto produto = criarProduto(2L);

        when(produtoRepository.findById(2L))
                .thenReturn(Optional.of(produto));

        Optional<ProdutoBaseResponse> opt = produtoBaseService.buscarPorId(2L);

        assertTrue(opt.isPresent());
        ProdutoBaseResponse dto = opt.get();

        assertEquals(2L, dto.getId());
        assertEquals("Cerveja", dto.getNome());
        assertEquals("Bebidas", dto.getCategoria());
        assertEquals(10, dto.getCategoriaId());
        assertEquals("img.png", dto.getImagemUrl());
        assertEquals(true, dto.getMaiorIdade());
        assertEquals("123", dto.getCodigoBarras());
    }

    // -------------------------------------------------------------------------------------

    @Test
    void buscarPorId_inexistente_retornaOptionalVazio() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProdutoBaseResponse> opt = produtoBaseService.buscarPorId(99L);

        assertFalse(opt.isPresent());
    }
}
