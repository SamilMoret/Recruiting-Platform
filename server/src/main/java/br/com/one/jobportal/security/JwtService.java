package br.com.one.jobportal.security;

import br.com.one.jobportal.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        log.info("JwtService inicializado com expiração de {} ms ({} dias)", 
                jwtConfig.getExpirationMs(), 
                TimeUnit.MILLISECONDS.toDays(jwtConfig.getExpirationMs()));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername());
        
        // Log para depuração
        Date expiration = extractExpiration(token);
        log.info("Novo token gerado para usuário: {}", userDetails.getUsername());
        log.info("Token expira em: {}", expiration);
        log.info("Tempo de expiração definido para: {} milissegundos", jwtConfig.getExpirationMs());
                
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtConfig.getExpirationMs());
        
        log.debug("Criando token para '{}' emitido em {} e expirando em {}", 
                subject, issuedAt, expiration);
                
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isExpired = isTokenExpired(token);
            
            // Log para depuração
            if (isExpired) {
                Date expiration = extractExpiration(token);
                log.warn("Token expirado para usuário: {}", username);
                log.warn("Data de expiração do token: {}", expiration);
                log.warn("Data atual: {}", new Date());
            }
            
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            
            if (isValid) {
                log.debug("Token válido para o usuário: {}", username);
            } else {
                log.warn("Token inválido para o usuário: {}", username);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage(), e);
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(secretKey)
                    .build();
                    
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException ex) {
            log.warn("Token expirado: {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            log.warn("Token inválido: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Erro ao processar token: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}