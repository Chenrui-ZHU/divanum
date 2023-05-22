package com.bezkoder.spring.login.repository;

import com.bezkoder.spring.login.models.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {

    Optional<Code> findByInfo(String info);

    @Query(value = "SELECT code_id FROM codes c INNER JOIN file_code fc WHERE fc.code_id = :code_id", nativeQuery = true)
    Long findByCodeId(Long code_id);

    @Query(value = "SELECT distinct c.info FROM codes c, notebook_code nc WHERE nc.notebook_id = (select notebook_id from user_notebook where user_id=:user_id) and nc.code_id = c.id", nativeQuery = true)
    List<String> getInfoByUserId(Long user_id);
}
