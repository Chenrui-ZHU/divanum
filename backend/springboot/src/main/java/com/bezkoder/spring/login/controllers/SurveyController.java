package com.bezkoder.spring.login.controllers;

import com.bezkoder.spring.login.models.Survey;
import com.bezkoder.spring.login.models.SurveyResult;
import com.bezkoder.spring.login.payload.response.MessageResponse;
import com.bezkoder.spring.login.repository.SurveyResultRepository;
import com.bezkoder.spring.login.service.CodeService;
import com.bezkoder.spring.login.service.SurveyService;
import com.bezkoder.spring.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = "http://34.155.164.205", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth/survey")
public class SurveyController {

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeService codeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveSurvey(@RequestBody String json) {
//        surveyService.deleteAllSurveys();
        Survey s = new Survey(json);
        surveyService.initSurveyCodeForAll(s);
//        surveyService.saveSurvey(s);
        return ResponseEntity.ok("Survey data saved successfully");
    }

    @GetMapping("/{survey_id}")
    public ResponseEntity<String> getSurvey(@PathVariable Long survey_id) {
        String json = surveyService.findSurvey(survey_id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "Survey found")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @GetMapping("")
    public ResponseEntity<String> continueSurvey() {
        Survey s = surveyService.findLastSurvey();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "Survey found")
                .contentType(MediaType.APPLICATION_JSON)
                .body(s.getJson());
    }

    @GetMapping("/code/{surveycode}")
    public ResponseEntity<String> getSurveyByCode(@PathVariable String surveycode) {
        String json = surveyService.findSurveyByCode(surveycode);
        codeService.usedCode(surveycode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "Survey found")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @PostMapping("/results")
    public ResponseEntity<MessageResponse> saveSurveyResult(@RequestParam("results") MultipartFile survey) {
        String message = "";
        try {
            surveyService.storeSurvey(survey);

            message = "Uploaded the survey successfully: " + survey.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the survey: " + survey.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @GetMapping("/survey_result")
    public ResponseEntity<byte[]> getAllSurveys() throws IOException {
        List<SurveyResult> surveyResults = surveyService.getAllSurveyResults().collect(Collectors.toList());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (SurveyResult sr : surveyResults) {
                ZipEntry zipEntry = new ZipEntry(sr.getName());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(sr.getData());
                zipOutputStream.closeEntry();
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"allsurveys.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayOutputStream.toByteArray());
    }

    @DeleteMapping("/survey_result")
    public ResponseEntity<String> deleteAllSurveys() {
        surveyService.deleteAllSurveys();
        return ResponseEntity.ok("Survey data deleted successfully");
    }
}
