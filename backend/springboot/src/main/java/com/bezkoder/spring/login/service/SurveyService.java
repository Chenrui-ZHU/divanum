package com.bezkoder.spring.login.service;

import com.bezkoder.spring.login.models.*;
import com.bezkoder.spring.login.repository.NotebookRepository;
import com.bezkoder.spring.login.repository.SurveyRepository;
import com.bezkoder.spring.login.repository.SurveyResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private CodeService codeService;

    @Autowired
    private UserService userService;
    
    public void saveSurvey(Survey survey){
        surveyRepository.save(survey);
    }

    public String findSurvey(Long survey_id){
        Survey s = surveyRepository.findById(survey_id)
                .orElseThrow(() -> new RuntimeException("survey not found"));
        return s.getJson();
    }

    public Survey findLastSurvey(){
        return surveyRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("survey not found"));
    }

    public String findSurveyByCode(String surveycode){
        String code_id = String.valueOf(codeService.getCodeByInfo(surveycode).getId());
        String survey_id = surveyRepository.findByCodeId(Long.valueOf(code_id));
        Survey s = surveyRepository.findById(Long.valueOf(survey_id))
                .orElseThrow(() -> new RuntimeException("survey not found"));
        return s.getJson();
    }

    public void storeSurvey(MultipartFile survey) throws IOException {
        String surveyName = StringUtils.cleanPath(Objects.requireNonNull(survey.getOriginalFilename()));
        SurveyResult sr = new SurveyResult(surveyName,survey.getContentType(), survey.getBytes());
        surveyResultRepository.save(sr);
    }

    public Stream<SurveyResult> getAllSurveyResults(){ return surveyResultRepository.findAll().stream();}

    public void deleteAllSurveys(){surveyRepository.deleteAll();}
    public void initSurveyCodeForAll(Survey s){
        Code code = codeService.initCode();
        code.setInfo("SURVEY"+code.getInfo());
        codeService.saveCode(code);
        for(Notebook nb: userService.getAllNBs().collect(Collectors.toList())){
            Set<Code> codes = nb.getCodes();
            codes.add(code);
            nb.setCodes(codes);
            notebookRepository.save(nb);
        }
        s.setCode(code);
        surveyRepository.save(s);
    }
}
