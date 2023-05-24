package com.bezkoder.spring.login.controllers;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.bezkoder.spring.login.models.User;
import com.bezkoder.spring.login.repository.NotebookRepository;
import com.bezkoder.spring.login.repository.UserRepository;
import com.bezkoder.spring.login.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bezkoder.spring.login.service.FileStorageService;
import com.bezkoder.spring.login.message.ResponseFile;
import com.bezkoder.spring.login.payload.response.MessageResponse;
import com.bezkoder.spring.login.models.FileDB;

@CrossOrigin(origins = "http://34.155.164.205", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
public class FileController {

  @Autowired
  private FileStorageService storageService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CodeService codeService;

  @PostMapping("user/{user_id}/upload")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String user_id) {
    String message = "";
    try {
      storageService.storeByUser(file,user_id);

      message = "Fichier: " + file.getOriginalFilename() + " envoyé avec succès! ";
      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
    } catch (Exception e) {
      User user = userRepository.findById(Long.valueOf(user_id))
              .orElseThrow(() -> new RuntimeException("user is not found"));
      message = "Échec de l'envoi du fichier: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
    }
  }

  @PostMapping("{code}/upload")
  public ResponseEntity<MessageResponse> uploadFileByCode(@RequestParam("file") MultipartFile file, @PathVariable String code) {
    String message = "";
    try {
      storageService.storeByCode(file,code);
      codeService.usedCode(code);

      message = "Fichier: " + file.getOriginalFilename() + " envoyé avec succès! ";
      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
    } catch (Exception e) {
      message = "Échec de l'envoi du fichier: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
    }
  }

  @GetMapping("files")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
              .fromCurrentContextPath()
              .path("api/auth/download/")
              .path(dbFile.getId())
              .toUriString();

      return new ResponseFile(
              dbFile.getName(),
              fileDownloadUri,
              dbFile.getType(),
              dbFile.getData().length);
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("{user_id}/files")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ResponseFile>> getListFilesByUser(@PathVariable Long user_id) {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = null;
      List<String> filesId = storageService.getFilesOfUser(user_id);
      for(String id: filesId){
        if(Objects.equals(dbFile.getId(), id)){
          fileDownloadUri = ServletUriComponentsBuilder
                  .fromCurrentContextPath()
                  .path("api/auth/download/")
                  .path(dbFile.getId())
                  .toUriString();
          break;
        }
      }
      return new ResponseFile(
              dbFile.getName(),
              fileDownloadUri,
              dbFile.getType(),
              dbFile.getData().length);
    }).collect(Collectors.toList());

    List<ResponseFile> temps = new ArrayList<>();
    for(ResponseFile rf: files){
      if(rf.getUrl() != null){
        temps.add(rf);
      }
    }

    return ResponseEntity.status(HttpStatus.OK).body(temps);
  }

  @GetMapping("download/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
    FileDB fileDB = storageService.getFile(id);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
            .body(fileDB.getData());
  }

  @GetMapping("download/{user_id}/files")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> getFilesOfUser(@PathVariable Long user_id) throws IOException{
    List<FileDB> files = storageService.getAllFiles().collect(Collectors.toList());
    List<String> filesId = storageService.getFilesOfUser(user_id);
    List<FileDB> temps = new ArrayList<>();
    for(FileDB file: files){
      for(String fileId:filesId){
        if(Objects.equals(fileId, file.getId())){
          temps.add(file);
          break;
        }
      }
    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      for (FileDB file : temps) {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(file.getData());
        zipOutputStream.closeEntry();
      }
    }
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"userfiles.zip\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(byteArrayOutputStream.toByteArray());
  }

  @GetMapping("download/files")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> getAllFiles() throws IOException {
    List<FileDB> files = storageService.getAllFiles().collect(Collectors.toList());

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      Map<String, Integer> fileNameCount = new HashMap<>();
      for (FileDB file : files) {
        String originalFileName = file.getName();
        Integer count = fileNameCount.getOrDefault(originalFileName, 0);
        fileNameCount.put(originalFileName, count + 1);
        String fileName = originalFileName;
        if (count > 0) {
          int dotIndex = originalFileName.lastIndexOf('.');
          if (dotIndex != -1) {
            fileName = originalFileName.substring(0, dotIndex)
                    + '_' + count
                    + originalFileName.substring(dotIndex);
          } else {
            fileName = originalFileName + '_' + count ;
          }
        }

        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(file.getData());
        zipOutputStream.closeEntry();
      }
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"allfiles.zip\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(byteArrayOutputStream.toByteArray());
  }

  @DeleteMapping("files")
  public ResponseEntity<String> deleteAllFiles() {
    storageService.deleteAllFiles();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "all files deleted successfully")
            .body("all files deleted successfully");
  }

  @DeleteMapping("files/{code}")
  public ResponseEntity<String> deleteFileByCode(@PathVariable String code) {
    storageService.deleteFileByCode(code);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "all files deleted successfully")
            .body("file with " + code + " deleted successfully");
  }
}