

package com.leon.datalink.web.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.web.config.AuthConfig;
import com.leon.datalink.core.exception.impl.AccessException;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.service.UserService;
import com.leon.datalink.core.utils.BaseContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User related methods entry.
 *
 * @author Leon
 */
@RestController
@RequestMapping("/api/auth/user")
public class AuthController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserService userService;

    /**
     * Get user info.
     *
     * @return Current login user info
     */
    @GetMapping("/info")
    public Object getUsersInfo() {
        return userService.get((String) BaseContextUtil.get(Constants.USERNAME));
    }


    /**
     * Login to datalink
     *
     * <p>This methods uses username and password to require a new token.
     *
     * @param username username of user
     * @param password password
     * @param response http response
     * @param request  http request
     * @return new token of the user
     * @throws AccessException if user info is incorrect
     */
    @PostMapping("/login")
    public Object login(@RequestParam String username, @RequestParam String password, HttpServletResponse response,
                        HttpServletRequest request) throws AccessException {

        String token = userService.login(request);
        response.addHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER, DatalinkAuthConfig.TOKEN_PREFIX + token);

        ObjectNode result = JacksonUtils.createEmptyJsonNode();
        result.put(Constants.ACCESS_TOKEN, token);
        result.put(Constants.TOKEN_TTL, authConfig.getTokenValidityInSeconds());
        return result;
    }


}
