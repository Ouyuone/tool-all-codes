package cc.ackman.multi.level;

import cc.ackman.multi.level.domain.entity.User;
import cc.ackman.multi.level.domain.repository.UserRepository;
import cc.ackman.multi.level.domain.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/15 16:02:06
 */
@SpringBootTest
public class MultiLevelTest {

    @SpyBean
    private UserRepository userRepository;
    
    @SpyBean
    private UserService userService;
    
    @Test
    public void test() {
        User user = new User();
        user.setId(1L);
        user.setEmail("mock-email");
        user.setName("mock-name");
        
        //为保证每次执行测试的正确性，先删缓存
        Mockito.doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        
        AtomicInteger count = new AtomicInteger(0);
        //添加缓存
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (count.getAndAdd(1) == 0) {
                    return Optional.of(user);
                }
                User user = new User();
                user.setId(1L);
                user.setEmail("mock-email1");
                user.setName("mock-name1");
                return Optional.of(user);
            }
        }).when(userRepository).findById(1L);
        Optional<User> userOpt = userService.getUserById(1L);
        
        //从缓存中获取user
        Optional<User> userOpt1 = userService.getUserById(1L);
        
        Assertions.assertEquals(userOpt.get(),userOpt1.get());
        Assertions.assertEquals(userOpt.get(),user);
        
        Optional<User> userOpt2 = userService.getUserById(1L);
        
        Assertions.assertEquals(userOpt2.get(),user);
        
        //删除缓存
        userService.deleteUser(1L);
    }
}
