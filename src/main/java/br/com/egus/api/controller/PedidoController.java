package br.com.egus.api.controller;

import br.com.egus.api.dto.PedidoDto;
import br.com.egus.api.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDto> criar(@RequestBody PedidoDto request) {
        PedidoDto criado = pedidoService.criarPedido(request);

        if (criado == null)
            return ResponseEntity.badRequest().build();

        URI location = URI.create("/pedidos/" + criado.getId());
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> buscar(@PathVariable Integer id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
