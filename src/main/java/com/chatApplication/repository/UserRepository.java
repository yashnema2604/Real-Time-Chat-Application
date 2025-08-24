package com.chatApplication.repository;

import com.chatApplication.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

        public boolean existsByUsernane(String username);
        public List<User> findByIsOnlineTrue();

        @Transactional
        @Modifying
        @Query("UPDATE User u SET u.isOnline=:isOnline WHERE u.username=:username")
        public void updateUserOnlineStatus(@Param("username") String username,@Param("isOnline") boolean isOnline);


        public Optional<User>  findByUsername(String username);

}
