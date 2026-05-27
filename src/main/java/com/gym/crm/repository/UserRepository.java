package com.gym.crm.repository;

import com.gym.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.username FROM User u WHERE u.username LIKE concat(:username, '%')")
    List<String> findUsernamesStartingWith(String username);

}
