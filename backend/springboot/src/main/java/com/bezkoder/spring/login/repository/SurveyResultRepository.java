package com.bezkoder.spring.login.repository;

import com.bezkoder.spring.login.models.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
}