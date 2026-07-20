package com.myapp.myapp.repositories;

import com.myapp.myapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Security üçün istifadəçini username-ə görə bazadan tapacaq metod
    Optional<User> findByUsername(String username);
}