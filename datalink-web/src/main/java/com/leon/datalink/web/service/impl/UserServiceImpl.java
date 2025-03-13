

package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.impl.AccessException;
import com.leon.datalink.core.storage.DataStorage;
import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.core.utils.PasswordEncoderUtil;
import com.leon.datalink.web.model.User;
import com.leon.datalink.web.security.DatalinkAuthConfig;
import com.leon.datalink.web.security.DatalinkUserDetails;
import com.leon.datalink.web.security.JwtTokenManager;
import com.leon.datalink.web.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * datalink user service.
 *
 * @author Leon
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    /**
     * key value storage
     */
    private final DataStorage<User> dataStorage;

    @Autowired
    private JwtTokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;


    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String PARAM_USERNAME = "username";

    private static final String PARAM_PASSWORD = "password";


    public UserServiceImpl() throws Exception {

        this.dataStorage = new ObjectStorage<>(User.class);

        // read user list form storage
        if (this.dataStorage.count() <= 0) {
            // 初始化用户
            User user = new User();
            user.setUsername("admin");
            user.setPassword(PasswordEncoderUtil.encode("datalink"));
            user.setDescription("管理员");
            user.setPermissions(Lists.newArrayList("all"));
            user.setCreateTime(DateUtil.now());
            user.setSystem(true);
            this.dataStorage.put(user.getUsername(), user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new DatalinkUserDetails(this.get(username));
    }

    @Override
    public void updateUserPassword(String username, String password) {
        User user = this.get(username);
        user.setPassword(password);
        this.dataStorage.put(username, user);
    }


    @Override
    public String login(Object request) throws AccessException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = resolveToken(req);
        if (StringUtils.isBlank(token)) {
            throw new AccessException("user not found!");
        }

        try {
            tokenManager.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new AccessException("token expired!");
        } catch (Exception e) {
            throw new AccessException("token invalid!");
        }

        return token;
    }

    /**
     * Get token from header.
     */
    private String resolveToken(HttpServletRequest request) throws AccessException {
        String bearerToken = request.getHeader(DatalinkAuthConfig.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        bearerToken = request.getParameter(Constants.ACCESS_TOKEN);
        if (StringUtils.isBlank(bearerToken)) {
            String userName = request.getParameter(PARAM_USERNAME);
            String password = request.getParameter(PARAM_PASSWORD);
            bearerToken = resolveTokenFromUser(userName, password);
        }

        return bearerToken;
    }

    private String resolveTokenFromUser(String userName, String rawPassword) throws AccessException {
        String finalName;
        Authentication authenticate;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName,
                    rawPassword);
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new AccessException("unknown user!");
        }

        if (null == authenticate || StringUtils.isBlank(authenticate.getName())) {
            finalName = userName;
        } else {
            finalName = authenticate.getName();
        }

        return tokenManager.createToken(finalName);
    }


    @Override
    public User get(String username) {
        User user = this.dataStorage.get(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public void add(User user) {
        this.dataStorage.put(user.getUsername(), user);
    }

    @Override
    public void update(User user) {
        this.dataStorage.put(user.getUsername(), user);
    }

    @Override
    public void remove(String username) {
        this.dataStorage.delete(username);
    }

    @Override
    public List<User> list(User user) {
        Stream<User> stream = this.dataStorage.getValues().stream();
        if (null != user) {
            if (!StringUtils.isEmpty(user.getUsername())) {
                stream = stream.filter(s -> s.getUsername().contains(user.getUsername()));
            }
            if (!StringUtils.isEmpty(user.getDescription())) {
                stream = stream.filter(s -> s.getDescription().contains(user.getDescription()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparing(User::getCreateTime)).collect(Collectors.toList()));
    }

    @Override
    public long getCount() {
        return this.dataStorage.count();
    }

}
