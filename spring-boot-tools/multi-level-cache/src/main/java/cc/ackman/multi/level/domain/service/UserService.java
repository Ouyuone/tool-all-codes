package cc.ackman.multi.level.domain.service;


import cc.ackman.multi.level.domain.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
    User saveUser(User user);
    void deleteUser(Long id);
}
