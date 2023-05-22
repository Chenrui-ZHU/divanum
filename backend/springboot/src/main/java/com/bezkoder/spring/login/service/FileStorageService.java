package com.bezkoder.spring.login.service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.bezkoder.spring.login.models.Code;
import com.bezkoder.spring.login.models.Notebook;
import com.bezkoder.spring.login.models.User;
import com.bezkoder.spring.login.repository.CodeRepository;
import com.bezkoder.spring.login.repository.NotebookRepository;
import com.bezkoder.spring.login.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bezkoder.spring.login.models.FileDB;
import com.bezkoder.spring.login.repository.FileDBRepository;

@Service
public class FileStorageService {

  @Autowired
  private FileDBRepository fileDBRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NotebookRepository notebookRepository;

  @Autowired
  private CodeRepository codeRepository;

  public void storeByUser(MultipartFile file, String user_id) throws IOException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());

    // Update file_user
    User user = userRepository.findById(Long.valueOf(user_id))
            .orElseThrow(() -> new RuntimeException("user is not found"));
    Notebook notebook = notebookRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("notebook is not found"));
    Set<Code> codes = new HashSet<>(notebook.getCodes());
    for (Code code : codes){
      if(codeRepository.findByCodeId(code.getId())==null){
        FileDB.setCode(code);
      }
    }
    fileDBRepository.save(FileDB);
  }

  public void storeByCode(MultipartFile file, String code) throws IOException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());

    // Update file_code
    Long notebook_id = notebookRepository.findIdByCode(code);
    Notebook notebook = notebookRepository.findById(notebook_id)
            .orElseThrow(() -> new RuntimeException("test"));
    Set<Code> codes = new HashSet<>(notebook.getCodes());
    for (Code code_ : codes){
      if(Objects.equals(code_.getInfo(), code)&&!code_.isUsed()){
        FileDB.setCode(code_);
      }
    }

    fileDBRepository.save(FileDB);

  }

  public FileDB getFile(String id) {
    return fileDBRepository.findById(id).orElseThrow(() -> new RuntimeException("file not found"));
  }

  public Stream<FileDB> getAllFiles() {
    return fileDBRepository.findAll().stream();
  }

  public void deleteAllFiles(){
    fileDBRepository.deleteAll();
  }

  public void deleteFileByCode(String code){
    Code code_ = codeRepository.findByInfo(code)
            .orElseThrow(() -> new RuntimeException("Error: Code is not found."));
    String file_id = fileDBRepository.findIdByInfo(code_.getId());
    FileDB fileDB = fileDBRepository.findById(file_id)
            .orElseThrow(() -> new RuntimeException("Error: File is not found."));;
    fileDBRepository.delete(fileDB);
  }

  public List<String> getFilesOfUser(Long user_id){
    return fileDBRepository.findByUser(user_id);
  }
}