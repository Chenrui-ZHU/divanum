package com.bezkoder.spring.login.repository;

import com.bezkoder.spring.login.models.Code;
import com.bezkoder.spring.login.models.Notebook;
import com.bezkoder.spring.login.models.User;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    @Query(value = "SELECT DISTINCT nc.notebook_id FROM notebook_code nc, codes c WHERE c.info = :code and c.id = nc.code_id;", nativeQuery = true)
    Long findIdByCode(String code);

    Optional<Notebook> findByUser(User user);

}
