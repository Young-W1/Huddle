package com.capstone.huddle.users.repository;

import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional <UserEntity> findByUsername(String username);

}
