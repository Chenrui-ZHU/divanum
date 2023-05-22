package com.bezkoder.spring.login.service;

import com.bezkoder.spring.login.models.Code;
import com.bezkoder.spring.login.models.FileDB;
import com.bezkoder.spring.login.models.Notebook;
import com.bezkoder.spring.login.models.User;
import com.bezkoder.spring.login.repository.NotebookRepository;
import com.bezkoder.spring.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotebookRepository notebookRepository;

    public Stream<User> getAllUsers() {
        return userRepository.findAll().stream();
    }

    public Stream<Notebook> getAllNBs() {
        return notebookRepository.findAll().stream();
    }

}
