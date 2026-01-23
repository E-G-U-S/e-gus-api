package br.com.egus.api.service.auth;

import br.com.egus.api.dto.LoginRequest;
import br.com.egus.api.dto.LoginResponse;
import br.com.egus.api.model.pessoa.Funcionario;
import br.com.egus.api.repository.FuncionarioRepository;
import br.com.egus.api.repository.UsuarioRepository;
import br.com.egus.api.service.SupabaseAuthService;
import br.com.egus.api.service.SenhaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailAuthenticationStrategy implements AuthenticationStrategy {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;

    private final SupabaseAuthService supabaseAuthService;
    public EmailAuthenticationStrategy(FuncionarioRepository funcionarioRepository,
                                       UsuarioRepository usuarioRepository,
                                       SenhaService senhaService,
                                       SupabaseAuthService supabaseAuthService) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.senhaService = senhaService;
        this.supabaseAuthService = supabaseAuthService;
    }

    @Override
    public boolean supports(LoginRequest request) {
        return request.getEmail() != null;
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var funcionarioOpt = funcionarioRepository.findByEmail(request.getEmail());

        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();

            if (!Boolean.TRUE.equals(funcionario.getAtivo())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Funcionario inativo");
            }

            if (!senhaService.validarSenha(request.getSenha(), funcionario.getSenha())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
            }

            return new LoginResponse(
                    funcionario.getId(),
                    funcionario.getNome(),
                    "FUNCIONARIO",
                    funcionario.getCargo().name(),
                    funcionario.getIdMercado()
            );
        }

        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));

        // [MUDANÇA AQUI]: Para usuário, NÃO usamos senhaService (pois a senha no banco é NULL ou velha).
        // Usamos o Supabase para validar.
        String tokenSupabase = supabaseAuthService.loginNoSupabase(request.getEmail(), request.getSenha());
        
        if (tokenSupabase == null) {
             // Se o Supabase recusou, a senha está errada.
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }

        // Se passou, retornamos os dados do banco local
        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(), 
                "USUARIO",
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getPreferences()
        );
    }
}