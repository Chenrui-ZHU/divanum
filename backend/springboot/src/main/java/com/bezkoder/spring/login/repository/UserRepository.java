package com.bezkoder.spring.login.repository;

import java.util.List;
import java.util.Optional;

import com.bezkoder.spring.login.models.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bezkoder.spring.login.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query(value = "SELECT username, id FROM users u WHERE u.id = :id", nativeQuery = true)
  String findUsernameByNotebookId(Long id);

  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);


  //
}
