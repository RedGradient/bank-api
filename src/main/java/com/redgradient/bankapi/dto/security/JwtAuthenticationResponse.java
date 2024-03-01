package com.redgradient.bankapi.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@Builder
//@NoArgsConstructor
@AllArgsConstructor
//@Schema(description = "Ответ c токеном доступа")
public class JwtAuthenticationResponse {
//    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;
}