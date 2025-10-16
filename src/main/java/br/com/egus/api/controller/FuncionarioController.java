package br.com.egus.api.controller;

import br.com.egus.api.dto.FuncionarioRequest;
import br.com.egus.api.model.pessoa.Cargo;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.repository.FuncionarioRepository;
import br.com.egus.api.service.SenhaService;
import jakarta.validation.Valid; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final SenhaService senhaService;

    public FuncionarioController(FuncionarioRepository funcionarioRepository, SenhaService senhaService) {
        this.funcionarioRepository = funcionarioRepository;
        this.senhaService = senhaService;
    }

    @PostMapping
    public ResponseEntity<Funcionario> cadastrar(@RequestBody @Valid FuncionarioRequest request) {
        
        if (funcionarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        }
    
        Cargo cargo;
        try {
            cargo = Cargo.valueOf(request.getCargo());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); 
        }
        
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(request.getNome());
        funcionario.setEmail(request.getEmail());
        funcionario.setSenha(senhaService.gerarHash(request.getSenha())); 
        funcionario.setAtivo(request.getAtivo());
        funcionario.setCargo(cargo); 
        funcionario.setIdMercado(request.getIdMercado());

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos(@RequestParam @Valid int idMercado) {
        // Se idMercado for inválido (não enviado), o Spring já retorna 400
        List<Funcionario> funcionarios = funcionarioRepository.findByIdMercado(idMercado);
        return ResponseEntity.ok(funcionarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FuncionarioRequest request) { // @Valid no PUT

        return funcionarioRepository.findById(id).map(funcionario -> {
            
            Optional<Funcionario> funcionarioComEmail = funcionarioRepository.findByEmail(request.getEmail());
            if (funcionarioComEmail.isPresent() && !funcionarioComEmail.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            Cargo cargo;
            try {
                cargo = Cargo.valueOf(request.getCargo());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null); 
            }

            funcionario.setNome(request.getNome());
            funcionario.setEmail(request.getEmail());
            
            if (request.getSenha() != null && !request.getSenha().isBlank()) {
                funcionario.setSenha(senhaService.gerarHash(request.getSenha()));
            }
            
            funcionario.setAtivo(request.getAtivo());
            funcionario.setCargo(cargo);
            funcionario.setIdMercado(request.getIdMercado());

            Funcionario atualizado = funcionarioRepository.save(funcionario);
            return ResponseEntity.ok(atualizado);
        }).orElse(ResponseEntity.notFound().build());
    }
}