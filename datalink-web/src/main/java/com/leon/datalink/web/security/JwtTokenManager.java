

package com.leon.datalink.web.security;

import com.leon.datalink.web.config.AuthConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * JWT token manager.
 *
 * @author Leon
 */
@Component
public class JwtTokenManager {
    
    private static final String AUTHORITIES_KEY = "auth";
    
    @Autowired
    private AuthConfig authConfig;
    
    /**
     * Create token.
     *
     * @param authentication auth info
     * @return token
     */
    public String createToken(Authentication authentication) {
        return createToken(authentication.getName());
    }
    
    /**
     * Create token.
     *
     * @param userName auth info
     * @return token
     */
    public String createToken(String userName) {
        
        long now = System.currentTimeMillis();
        
        Date validity;
        validity = new Date(now + authConfig.getTokenValidityInSeconds() * 1000L);
        
        Claims claims = Jwts.claims().setSubject(userName);
        return Jwts.builder().setClaims(claims).setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(authConfig.getSecretKeyBytesDecoders()), SignatureAlgorithm.HS256).compact();
    }
    
    /**
     * Get auth Info.
     *
     * @param token token
     * @return auth info
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(authConfig.getSecretKeyBytesDecoders()).build()
                .parseClaimsJws(token).getBody();
        
        List<GrantedAuthority> authorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList((String) claims.get(AUTHORITIES_KEY));
        
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
    
    /**
     * validate token.
     *
     * @param token token
     */
    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(authConfig.getSecretKeyBytesDecoders()).build().parseClaimsJws(token);
    }
    
}
