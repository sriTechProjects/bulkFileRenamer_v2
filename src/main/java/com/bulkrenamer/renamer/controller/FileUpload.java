package com.bulkrenamer.renamer.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
public class FileUpload {

    private static final Logger logger = LoggerFactory.getLogger(FileUpload.class);

    @PostMapping("/uploadFiles")
    public String handleFiles(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes) {
        if (files == null || files.length == 0) {
            redirectAttributes.addFlashAttribute("error", "No files uploaded.");
            return "error";
        }

        logger.info("Received {} files", files.length);

        // ✅ Define absolute path to store files
        String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // ✅ Ensure folder is created
        }

        List<File> savedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (!file.isEmpty()) {
                    // ✅ Save inside known upload directory
                    File tempFile = new File(uploadDir, file.getOriginalFilename());
                    file.transferTo(tempFile);
                    savedFiles.add(tempFile);
                    logger.info("Saved file: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                logger.error("Error saving file: {}", file.getOriginalFilename(), e);
                redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + file.getOriginalFilename());
                return "error";
            }
        }

        List<String> fileNames = savedFiles.stream()
                .map(File::getName)
                .collect(Collectors.toList());

        // model.addAttribute("fileNames", fileNames);
        redirectAttributes.addFlashAttribute("fileNames", fileNames);
        return "redirect:/renamer"; // Or your rename view
    }

    @PostMapping("/uploadFolder")
    public String handleFolder(@RequestParam("folderFiles") MultipartFile[] folderFiles,
            Model model, HttpSession session) {

        List<File> savedFiles = new ArrayList<>();
        String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
        File uploadDir = new File(uploadDirPath);

        if (!uploadDir.exists())
            uploadDir.mkdirs();

        for (MultipartFile file : folderFiles) {
            try {
                if (!file.isEmpty()) {
                    // ✅ Strip folder name if present
                    String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
                    File tempFile = new File(uploadDir, fileName);
                    file.transferTo(tempFile);
                    savedFiles.add(tempFile);
                    logger.info("Saved file: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                logger.error("Error saving file: {}", file.getOriginalFilename(), e);
                // redirectAttributes.addFlashAttribute("error", "Failed to upload file: " +
                // file.getOriginalFilename());
                return "error";
            }
        }

        // ✅ Store saved files in session for further operations
        session.setAttribute("uploadedFiles", savedFiles);

        List<String> fileNames = savedFiles.stream()
                .map(File::getName)
                .collect(Collectors.toList());

        model.addAttribute("fileNames", fileNames);
        return "rename"; // Show rename preview page
    }

}
