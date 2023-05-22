package com.bezkoder.spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bezkoder.spring.login.models.FileDB;

import java.util.List;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

    @Query(value = "SELECT file_id FROM files f INNER JOIN file_code fc WHERE fc.code_id = :code_id", nativeQuery = true)
    String findIdByInfo(Long code_id);

    @Query(value = "SELECT fc.file_id FROM file_code fc WHERE fc.code_id IN (SELECT nc.code_id FROM notebook_code nc WHERE nc.notebook_id = (select notebook_id from user_notebook where user_id=:user_id))", nativeQuery = true)
    List<String> findByUser(Long user_id);

}
