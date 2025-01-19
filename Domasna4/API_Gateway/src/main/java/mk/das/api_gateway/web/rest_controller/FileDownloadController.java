package mk.das.api_gateway.web.rest_controller;

import mk.das.api_gateway.service.FileDownloadService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileDownloadController {
    private final FileDownloadService downloadService;

    public FileDownloadController(FileDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @CrossOrigin("/*")
    @GetMapping("/download/mega-data.csv")
    public ResponseEntity<InputStreamResource> downloadFile_csv() {
        return this.downloadService.redirectFileFromURL("http://technical-microservice:8091/download/mega-data.csv","mega-data.csv");
    }



    @CrossOrigin("/*")
    @GetMapping("/download/issuer_names.json")
    public ResponseEntity<InputStreamResource> downloadFile_json() {

        return this.downloadService.redirectFileFromURL("http://technical-microservice:8091/download/issuer_names.json","issuer_names.json");
    }

}
