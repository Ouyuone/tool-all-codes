package cc.ackman.multi.level.domain.service;

import cc.ackman.multi.level.domain.entity.User;
import cc.ackman.multi.level.domain.repository.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @CachePut(cacheNames = "users", key = "#result.id")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
//    @CacheEvict(value="users",allEntries=true) //方法调用后清空所有缓存
    //@CacheEvict(cacheNames = "users", key = "#id",beforeInvocation=true)//会在方法调用前清除缓存数据
    @CacheEvict(cacheNames = "users", key = "#id",beforeInvocation=false)//会在方法调用后清除缓存数据
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
