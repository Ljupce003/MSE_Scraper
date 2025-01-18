package mk.das.lstm_m_service.web.rest_controller;

import mk.das.lstm_m_service.service.PythonScriptRunnerService;
import mk.das.lstm_m_service.web.controller.LSTM_Analysis_Controller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;

@RestController
public class LSTM_REST_controller {

    private final PythonScriptRunnerService scriptRunnerService;

    public LSTM_REST_controller(PythonScriptRunnerService scriptRunnerService) {
        this.scriptRunnerService = scriptRunnerService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/download/processed_lstm.csv")
    public ResponseEntity<FileSystemResource> downloadFile_names() {

        File file = new File("src/main/python/Smestuvanje/processed_lstm.csv"); //TODO alter
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        // Create the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed_lstm.csv"); //TODO alter
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if(LSTM_Analysis_Controller.script_last_run_time.isBefore(LocalDateTime.now().minusHours(12))){
            scriptRunnerService.run_script();
        }

        // Return the file as a download
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/lstm-flag")
        public ResponseEntity<Boolean> getScriptRunningFlag(){
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(LSTM_Analysis_Controller.script_running_flag); // return the flag status

        }
}
