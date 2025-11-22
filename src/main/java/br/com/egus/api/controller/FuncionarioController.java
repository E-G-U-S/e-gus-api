package br.com.egus.api.controller;

import br.com.egus.api.dto.FuncionarioRequest;
import br.com.egus.api.model.pessoa.Cargo;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.repository.FuncionarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return funcionarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody FuncionarioRequest request) {
        Funcionario funcionario = new Funcionario(
                null,
                request.getNome(),
                request.getEmail(),
                request.getSenha(),
                request.getAtivo(),
                Cargo.valueOf(request.getCargo()),
                request.getIdMercado()
        );
        funcionarioRepository.save(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioRequest request) {

        return funcionarioRepository.findById(id)
                .map(f -> {
                    f.setNome(request.getNome());
                    f.setEmail(request.getEmail());
                    f.setSenha(request.getSenha());
                    f.setAtivo(request.getAtivo());
                    f.setCargo(Cargo.valueOf(request.getCargo()));
                    f.setIdMercado(request.getIdMercado());
                    funcionarioRepository.save(f);
                    return ResponseEntity.ok(f);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (funcionarioRepository.existsById(id)) {
            funcionarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
