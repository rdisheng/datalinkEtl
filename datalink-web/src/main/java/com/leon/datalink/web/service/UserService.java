
package com.leon.datalink.web.service;


import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.web.model.User;
import com.leon.datalink.core.exception.impl.AccessException;

/**
 * User service.
 *
 * @author Leon
 */
public interface UserService extends BaseService<User> {

    void updateUserPassword(String username, String password) ;

    String login(Object request) throws AccessException;

}
