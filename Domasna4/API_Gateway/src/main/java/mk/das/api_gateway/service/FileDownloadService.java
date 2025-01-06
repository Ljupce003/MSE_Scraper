package mk.das.api_gateway.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface FileDownloadService {

    public void downloadFundamentalFile(String url);
    public ResponseEntity<InputStreamResource> redirectFileFromURL(String url,String filename);

}
