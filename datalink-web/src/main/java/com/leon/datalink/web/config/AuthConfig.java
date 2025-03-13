

package com.leon.datalink.web.config;

import com.leon.datalink.web.filter.JwtAuthenticationTokenFilter;
import com.leon.datalink.web.security.JwtTokenManager;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * auth filter config.
 *
 * @author Leon
 */
@Configuration
public class AuthConfig {

    @Autowired
    private JwtTokenManager tokenProvider;

    private long tokenValidityInSeconds = 18000;

    public byte[] secretKeyBytesDecoders;

    @Bean
    public FilterRegistrationBean authFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthenticationTokenFilter());
        registration.addUrlPatterns("/*");
        registration.setName("authFilter");
        registration.setOrder(6);
        
        return registration;
    }
    
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(tokenProvider);
    }

    public byte[] getSecretKeyBytesDecoders() {
        if (secretKeyBytesDecoders == null) {
            secretKeyBytesDecoders = Decoders.BASE64.decode("SecretKey012345678901234567890123456789012345678901234567890123456789");
        }
        return secretKeyBytesDecoders;
    }

    public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }
}
