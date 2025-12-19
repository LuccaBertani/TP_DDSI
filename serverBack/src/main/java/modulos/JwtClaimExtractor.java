package modulos;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtClaimExtractor {

    public static String getUsernameFromToken(Jwt token){
        Claims  claims = (Claims) token.getBody();
        return claims.getSubject();
    }

    public static String getRefreshToken(Jwt token){
        Claims claims = (Claims) token.getBody();
        return claims.get("refreshToken").toString();
    }

    public static String getToken(Jwt token){
        Claims claims = (Claims) token.getBody();
        return claims.get("token").toString();
    }

    private final String SECRET_BASE64 = "TuClaveSecretaMuyLargaYSegura_Base64EncodedString";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error al validar o decodificar el token JWT: " + e.getMessage());
        }
    }
    public void accederAParametros(String jwtToken) {
        Claims claims = extractClaims(jwtToken);

        String subject = claims.getSubject();
        Date expiration = claims.getExpiration();

        String rol = claims.get("rol_principal", String.class);
        List<String> rolesList = claims.get("roles", List.class);
    }
}
