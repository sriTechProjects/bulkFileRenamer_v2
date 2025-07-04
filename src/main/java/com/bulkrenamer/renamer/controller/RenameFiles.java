package com.bulkrenamer.renamer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bulkrenamer.renamer.service.FileRenameService;

@Controller
public class RenameFiles {
    private final FileRenameService fileRenameService;

    public RenameFiles(FileRenameService fileRenameService) {
        this.fileRenameService = fileRenameService;
    }

    @GetMapping("/renamer")
    public String renamer() {
        return "rename";
    }

    @PostMapping("/renameFiles")
    public ResponseEntity<Resource> renameFiles(@RequestParam("renameMethod") int renameMethod,
            @RequestParam("attachment") String attachment) {
        try {
            File uploadDir = new File(System.getProperty("user.dir") + File.separator + "uploads");
            File[] files = uploadDir.listFiles();

            if (files == null || files.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            fileRenameService.renameFiles(List.of(files), renameMethod, attachment);

            File[] renamedFiles = uploadDir.listFiles();

            File zipFile = File.createTempFile("renamed_files_", ".zip");

            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for (File file : renamedFiles) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        zipOut.putNextEntry(new ZipEntry(file.getName()));
                        fis.transferTo(zipOut);
                        zipOut.closeEntry();
                    }
                }
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            ResponseEntity<Resource> response = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=renamed_files.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipFile.length())
                    .body(resource);

            // Delete all files in uploads AFTER preparing the response
            for (File file : renamedFiles) {
                file.delete();
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                    .body(null);
        }
    }
}
