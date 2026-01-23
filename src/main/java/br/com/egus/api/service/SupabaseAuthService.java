package br.com.egus.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Map;

@Service
public class SupabaseAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key.anon}") // Use a chave ANON aqui, configure no properties
    private String supabaseKey;

    public String loginNoSupabase(String email, String senha) {
        var client = RestClient.create();

        try {
            // Chama a API de Auth do Supabase (endpoint oficial de login)
            var response = client.post()
                    .uri(supabaseUrl + "/auth/v1/token?grant_type=password")
                    .header("apikey", supabaseKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "email", email,
                            "password", senha
                    ))
                    .retrieve()
                    .toEntity(Map.class);

            // Se der certo, pega o Access Token ou o ID do usuário
            Map<String, Object> body = response.getBody();
            // Retorna o Access Token para o Java usar ou passar para frente
            return (String) body.get("access_token");

        } catch (HttpClientErrorException e) {
            // Se o Supabase recusar a senha, lançamos exceção
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}