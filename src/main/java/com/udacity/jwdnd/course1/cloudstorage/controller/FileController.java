package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import com.udacity.jwdnd.course1.cloudstorage.storage.StorageFileNotFoundException;
import com.udacity.jwdnd.course1.cloudstorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/file")
public class FileController {
    private final StorageService storageService;
    private final FileService fileService;
    private final UserService userService;

    @Autowired
    public FileController(StorageService storageService, FileService fileService, UserService userService){
        this.storageService = storageService;
        this.fileService = fileService;
        this.userService = userService;
    }


    @PostMapping()
    public String handleFileUpload(Authentication authentication,@RequestParam("fileUpload") MultipartFile file, Model model) {
        String createFileError = null;
        String username = authentication.getName();
        User user = userService.getUser(username);

        if(createFileError == null){
            int rowsAdded = fileService.createFile(file, user.getUserId());
            if(rowsAdded < 0){
                createFileError = "Something went wrong in creating the file " + file.getOriginalFilename();
            }
        }

        if(createFileError == null){
            model.addAttribute("success", true);
        }else{
            model.addAttribute("error", createFileError);
        }

        return "result";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
