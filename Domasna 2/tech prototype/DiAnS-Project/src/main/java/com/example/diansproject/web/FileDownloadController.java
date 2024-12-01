package com.example.diansproject.web;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileDownloadController {

    @GetMapping("/download/mega-data.csv")
    public ResponseEntity<FileSystemResource> downloadFile_csv() {
        // Replace with the actual path to the generated CSV file
        File file = new File("../MSE_Scraper/shared/mega-data.csv");
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mega-data.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

    @GetMapping("/download/issuer_names.json")
    public ResponseEntity<FileSystemResource> downloadFile_json() {
        // Replace with the actual path to the generated CSV file
        File file = new File("../MSE_Scraper/shared/issuer_names.json");
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=issuer_names.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }
}
