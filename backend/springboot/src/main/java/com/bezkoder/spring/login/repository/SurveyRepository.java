package com.bezkoder.spring.login.repository;

import com.bezkoder.spring.login.models.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query(value = "SELECT survey_id FROM survey_code sc WHERE sc.code_id = :code_id", nativeQuery = true)
    String findByCodeId(Long code_id);

    Optional<Survey> findFirstByOrderByIdDesc();

    Optional<Survey> findByJson(String json);
}
