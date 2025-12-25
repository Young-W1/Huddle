package com.capstone.huddle.users.repository;

import com.capstone.huddle.users.model.FollowEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, UUID> {

    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);

    Optional<FollowEntity> findByFollowerAndFollowing(UserEntity follower, UserEntity following);

    @Query("SELECT f FROM FollowEntity f WHERE f.following.id = :userId")
    Page<FollowEntity> findFollowersByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f FROM FollowEntity f WHERE f.follower.id = :userId")
    Page<FollowEntity> findFollowingByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM FollowEntity f WHERE f.following = :user")
    Long countFollowers(@Param("user") UserEntity user);

    @Query("SELECT COUNT(f) FROM FollowEntity f WHERE f.follower = :user")
    Long countFollowing(@Param("user") UserEntity user);

}
