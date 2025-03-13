

package com.leon.datalink.web.filter;


import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.BaseContextUtil;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.security.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.AccessException;


/**
 * jwt auth token filter.
 *
 * @author Leon
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenManager tokenManager;

    public JwtAuthenticationTokenFilter(JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String jwt = resolveToken(request);

        if (StringUtils.isNotBlank(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                this.tokenManager.validateToken(jwt);
            } catch (Exception e) {
                throw new AccessException(e.getMessage());
            }
            Authentication authentication = this.tokenManager.getAuthentication(jwt);
            User user = (User) authentication.getPrincipal();
            BaseContextUtil.set(Constants.USERNAME, user.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            BaseContextUtil.remove();
        }
    }

    /**
     * Get token from header.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        String jwt = request.getParameter(Constants.ACCESS_TOKEN);
        if (StringUtils.isNotBlank(jwt)) {
            return jwt;
        }
        return null;
    }
}
