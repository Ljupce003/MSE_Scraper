package mk.das.tech_m_service.web.rest_controller;

import mk.das.tech_m_service.service.PythonScriptRunnerService;
import mk.das.tech_m_service.web.controller.Tech_Analysis_Controller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@RestController
public class Tech_REST_controller {

    private final PythonScriptRunnerService scriptRunnerService;

    public Tech_REST_controller(PythonScriptRunnerService scriptRunnerService) {
        this.scriptRunnerService = scriptRunnerService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/download/mega-data.csv")
    public ResponseEntity<FileSystemResource> downloadTechResult() throws URISyntaxException {
        // Replace with the actual path to the generated CSV file
        File file = new File("src/main/python/Smestuvanje/mega-data.csv"); //TODO alter this after script finishes
        if (!file.exists()) {
            System.out.println("mega-data.csv file not found");
            System.out.println(System.getProperty("user.dir"));
            return ResponseEntity.notFound().build();
        }
        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mega-data.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if(Tech_Analysis_Controller.script_last_run_time.isBefore(LocalDateTime.now().minusHours(12))){
            scriptRunnerService.run_script();
        }

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }



    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/download/issuer_names.json")
    public ResponseEntity<FileSystemResource> downloadFile_json() {
        // Replace with the actual path to the generated CSV file
        File file = new File("src/main/python/Smestuvanje/issuer_names.json");
        if (!file.exists()) {
            System.out.println("issuer_names.json file not found");
            System.out.println(System.getProperty("user.dir"));
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


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/download/names.json")
    public ResponseEntity<FileSystemResource> getIssuerNames() {
        // Replace with the actual path to the generated CSV file
        File file = new File("src/main/python/Smestuvanje/names.json");
        if (!file.exists()) {
            System.out.println("names.json file not found");
            System.out.println(System.getProperty("user.dir"));
            return ResponseEntity.notFound().build();
        }
        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=names.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/tech-flag")
        public ResponseEntity<Boolean> getScriptRunningFlag(){
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Tech_Analysis_Controller.script_running_flag); // return the flag status

        }
}
