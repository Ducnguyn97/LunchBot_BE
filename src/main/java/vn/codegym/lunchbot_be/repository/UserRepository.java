// repository/UserRepository.java
package vn.codegym.lunchbot_be.repository;

import vn.codegym.lunchbot_be.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.lunchbot_be.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
    Page<User> findByRole(UserRole role, Pageable pageable);
    Optional<User> findByVerificationToken(String token);
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword% OR u.fullName LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);
}

