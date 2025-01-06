package mk.das.api_gateway.web.rest_controller;

import mk.das.api_gateway.service.FileDownloadService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileDownloadController {
    private final FileDownloadService downloadService;

    public FileDownloadController(FileDownloadService downloadService) {
        this.downloadService = downloadService;
    }
    //private static final long MAX_WAIT_TIME = 10000;

    @GetMapping("/download/mega-data.csv")
    public ResponseEntity<InputStreamResource> downloadFile_csv() {
        return this.downloadService.redirectFileFromURL("http://localhost:8091/download/mega-data.csv","mega-data.csv");
    }

//    @GetMapping("/download/processed_lstm.csv")
//    public ResponseEntity<FileSystemResource> downloadFile_lstm() {
//        // Replace with the actual path to the generated CSV file
//        File file = new File("Domasna3/dians/src/main/python/Smestuvanje/processed_lstm.csv");
//        if (!file.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//        // Create the response headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed_lstm.csv");
//        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//        // Return the file as a download
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(new FileSystemResource(file));
//    }


    @GetMapping("/download/issuer_names.json")
    public ResponseEntity<InputStreamResource> downloadFile_json() {

        return this.downloadService.redirectFileFromURL("http://localhost:8091/download/issuer_names.json","issuer_names.json");
    }
//
//    @GetMapping("/download/names.json")
//    public ResponseEntity<FileSystemResource> downloadFile_names() {
//        // Replace with the actual path to the generated CSV file
//        File file = new File("Domasna3/dians/src/main/python/Smestuvanje/names.json");
//        if (!file.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//        // Create the response headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=names.json");
//        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//        // Return the file as a download
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(new FileSystemResource(file));
//    }
}
