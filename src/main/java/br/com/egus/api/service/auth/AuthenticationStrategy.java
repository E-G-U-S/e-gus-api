package br.com.egus.api.service.auth;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;

public interface AuthenticationStrategy {
    boolean supports(LoginRequest request);
    LoginResponse authenticate(LoginRequest request);
}