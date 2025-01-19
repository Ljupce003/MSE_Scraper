package mk.das.api_gateway.service.implementations;

import mk.das.api_gateway.service.FileDownloadService;
import mk.das.api_gateway.web.controller.Fundamental_Analysis_Controller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;


@Service
class FileDownloadServiceImplemented implements FileDownloadService {

        private final RestTemplate restTemplate = new RestTemplate();

        public void downloadFundamentalFile(String url) {

            File fundamentals_file = new File(System.getProperty("user.dir"), "/src/main/local/channels.json");

            if (!fundamentals_file.exists()){
                fetchFileFromUrl(url);
            }
            else if(Fundamental_Analysis_Controller.fund_last_fetch_date.isBefore(LocalDateTime.now().minusHours(1))){
                fetchFileFromUrl(url);
            }




        }

    private void fetchFileFromUrl(String url) {
        try {
            // Make the GET request
            ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);

            // Save the file locally
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Resource resource = response.getBody();
                InputStream inputStream = resource.getInputStream();
                File file = new File(System.getProperty("user.dir"), "/src/main/local/channels.json"); // Change the path as needed
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File downloaded successfully: " + file.getAbsolutePath());
                Fundamental_Analysis_Controller.fund_last_fetch_date= LocalDateTime.now();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> redirectFileFromURL(String url,String filename) {

        try {
            System.out.println("Connecting to URL: " + url);
            // Open a connection to the microservice
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the file as an InputStream
                InputStream inputStream = connection.getInputStream();

                // Wrap the InputStream in a Spring InputStreamResource
                InputStreamResource resource = new InputStreamResource(inputStream);

                // Set the headers for the response
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);
                headers.add(HttpHeaders.CONTENT_TYPE, connection.getContentType());

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            System.err.println("Error fetching or forwarding the file: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}

