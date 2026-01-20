package br.com.egus.api.service.auth;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.service.SenhaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CpfAuthenticationStrategy implements AuthenticationStrategy {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;

    public CpfAuthenticationStrategy(UsuarioRepository usuarioRepository, SenhaService senhaService) {
        this.usuarioRepository = usuarioRepository;
        this.senhaService = senhaService;
    }

    @Override
    public boolean supports(LoginRequest request) {
        return request.getCpf() != null;
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var usuario = usuarioRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CPF ou senha inválidos"));

        if (!senhaService.validarSenha(request.getSenha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CPF ou senha inválidos");
        }

        return new LoginResponse(usuario.getId(), usuario.getNome(), "USUARIO", usuario.getCpf(), usuario.getEmail(), usuario.getPreferences());
    }
}