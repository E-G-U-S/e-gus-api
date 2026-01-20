package br.com.egus.api.service;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.service.auth.AuthenticationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class AutenticaService {

    private final List<AuthenticationStrategy> strategies;

    public AutenticaService(List<AuthenticationStrategy> strategies) {
        this.strategies = strategies;
    }

    public LoginResponse login(LoginRequest request) {
        return strategies.stream()
                .filter(s -> s.supports(request))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "É necessário informar email ou CPF"))
                .authenticate(request);
    }
}
