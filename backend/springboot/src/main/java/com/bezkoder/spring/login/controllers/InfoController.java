package com.bezkoder.spring.login.controllers;

import com.bezkoder.spring.login.message.ResponseUser;
import com.bezkoder.spring.login.models.Code;
import com.bezkoder.spring.login.models.ERole;
import com.bezkoder.spring.login.models.Role;
import com.bezkoder.spring.login.models.User;
import com.bezkoder.spring.login.repository.CodeRepository;
import com.bezkoder.spring.login.service.CodeService;
import com.bezkoder.spring.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://34.155.164.205", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/info")
public class InfoController {

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeService codeService;

    @PostMapping("")
    public void init(){

    }

    @GetMapping("/codes")
    public ResponseEntity<?> getAllCodes(){
        List<Code> codes = codeService.getAllCodes().collect(Collectors.toList());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "all codes")
                .body(codes);
    }

    @GetMapping("{user_id}/codes")
    public ResponseEntity<?> getCodesByUser(@PathVariable String user_id){
        List<String> codes = codeRepository.getInfoByUserId(Long.valueOf(user_id));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "all codes of " + user_id)
                .body(codes);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers(){
        List<User> users = userService.getAllUsers().collect(Collectors.toList());
        List<ResponseUser> temps = new ArrayList<>();
        for(User u: users){
            if(u.getRoles().size()==1){
                temps.add(new ResponseUser(u.getId(),u.getUsername()));
            }
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "all users")
                .body(temps);
    }
}
