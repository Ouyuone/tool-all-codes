package cc.ackman.multi.level.domain.repository;


import cc.ackman.multi.level.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
