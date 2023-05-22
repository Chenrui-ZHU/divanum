package com.bezkoder.spring.login.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.bezkoder.spring.login.models.*;
import com.bezkoder.spring.login.repository.CodeRepository;
import com.bezkoder.spring.login.repository.NotebookRepository;
import com.bezkoder.spring.login.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.login.payload.request.LoginRequest;
import com.bezkoder.spring.login.payload.request.SignupRequest;
import com.bezkoder.spring.login.payload.response.UserInfoResponse;
import com.bezkoder.spring.login.payload.response.MessageResponse;
import com.bezkoder.spring.login.repository.RoleRepository;
import com.bezkoder.spring.login.repository.UserRepository;
import com.bezkoder.spring.login.security.jwt.JwtUtils;
import com.bezkoder.spring.login.security.services.UserDetailsImpl;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://34.155.164.205", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  NotebookRepository notebookRepository;

  @Autowired
  CodeRepository codeRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  CodeService codeService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
                                   userDetails.getUsername(),
                                   roles));
  }

  @PostMapping("/signup")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
                         encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    // Create new notebook for user
    Notebook notebook = new Notebook();
    notebook.setUser(user);

    // Create new codes for notebook
    Set<Code> codes = new HashSet<>();
    for (int i =0; i<10; i++){
      Code code = codeService.initCode();
      codes.add(code);
      codeRepository.save(code);
    }

    notebook.setCodes(codes);
    notebookRepository.save(notebook);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/signup_admin")
  public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(adminRole);
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/password/{user_id}/{pwd}")
  public ResponseEntity<?> updatePassword(@PathVariable String user_id, @PathVariable String pwd) {
    User u = userRepository.findById(Long.valueOf(user_id))
            .orElseThrow(() -> new RuntimeException("user not found"));
    u.setPassword(encoder.encode(pwd));
    userRepository.save(u);
    return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
  }

  @DeleteMapping("/users/{id}")
  public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long user_id) {
    Notebook notebook = notebookRepository.findById(user_id)
            .orElseThrow(() -> new RuntimeException("fail"));
    Set<Code> codes = new HashSet<>(notebook.getCodes());
    notebookRepository.delete(notebook);
    for(Code code: codes) {
      codeRepository.delete(code);
    }

    User user = userRepository.findById(user_id)
            .orElseThrow(() -> new RuntimeException("fail"));
    userRepository.delete(user);

    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }

  @DeleteMapping("/admin/{id}")
  public Map<String, Boolean> deleteAdmin(@PathVariable(value = "id") Long admin_id) {
    User admin = userRepository.findById(admin_id)
            .orElseThrow(() -> new RuntimeException("fail"));
    userRepository.delete(admin);

    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }


}
