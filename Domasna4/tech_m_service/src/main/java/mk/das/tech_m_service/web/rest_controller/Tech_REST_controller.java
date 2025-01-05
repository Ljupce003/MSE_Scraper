package mk.das.tech_m_service.web.rest_controller;

import mk.das.tech_m_service.service.PythonScriptRunnerService;
import mk.das.tech_m_service.web.controller.Tech_Analysis_Controller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;

@RestController
public class Tech_REST_controller {

    private final PythonScriptRunnerService scriptRunnerService;

    public Tech_REST_controller(PythonScriptRunnerService scriptRunnerService) {
        this.scriptRunnerService = scriptRunnerService;
    }

    @GetMapping("/download/mega-data.csv")
    public ResponseEntity<FileSystemResource> downloadTechResult() {
        // Replace with the actual path to the generated CSV file
        File file = new File("Domasna4/lstm_m_service/src/main/python/Smestuvanje/processed_lstm.csv"); //TODO alter this after script finishes
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed_lstm.csv"); //TODO alter this after script finishes
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if(Tech_Analysis_Controller.script_last_run_time.isBefore(LocalDateTime.now().minusHours(12))){
            scriptRunnerService.run_script();
        }

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }


    @GetMapping("/download/issuer_names.json")
    public ResponseEntity<FileSystemResource> downloadFile_json() {
        // Replace with the actual path to the generated CSV file
        File file = new File("Domasna4/tech_m_service/src/main/python/Smestuvanje/issuer_names.json");
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


    @GetMapping("/download/names.json")
    public ResponseEntity<FileSystemResource> getIssuerNames() {
        // Replace with the actual path to the generated CSV file
        File file = new File("Domasna4/tech_m_service/src/main/python/Smestuvanje/names.json");
        if (!file.exists()) {
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
