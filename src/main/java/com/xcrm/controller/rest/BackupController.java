package com.xcrm.controller.rest;

import com.xcrm.service.DatabaseBackupService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/config")
public class BackupController {
    private final DatabaseBackupService backupService;

    public BackupController(DatabaseBackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/export-db")
    public ResponseEntity<Resource> exportDatabase() {
        try {
            File backupFile = backupService.createBackup();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(backupFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
