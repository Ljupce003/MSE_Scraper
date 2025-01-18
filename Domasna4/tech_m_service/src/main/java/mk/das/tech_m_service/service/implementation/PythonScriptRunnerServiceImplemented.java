package mk.das.tech_m_service.service.implementation;

import mk.das.tech_m_service.service.PythonScriptRunnerService;
import mk.das.tech_m_service.web.controller.Tech_Analysis_Controller;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@Service
public class PythonScriptRunnerServiceImplemented implements PythonScriptRunnerService {

    private final ResourceLoader resourceLoader;

    public PythonScriptRunnerServiceImplemented(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run_script() {
        if (!Tech_Analysis_Controller.script_running_flag) {
            System.out.println("Starting Python script 'Tech Analysis' in background...");

            try {
                // Load the Python script from resources
                Resource resource = resourceLoader.getResource("classpath:python/Main.py");

                if (!resource.exists()) {
                    throw new RuntimeException("Python script not found in resources");
                }

                // Extract the Python script to a temporary file
                Path tempScript = Files.createTempFile("Main", ".py");
                try (InputStream inputStream = resource.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(tempScript)) {
                    inputStream.transferTo(outputStream);
                }

                // Path to the Python executable
                String pythonPath = "python";

                // Build the process
                ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, tempScript.toAbsolutePath().toString());
                processBuilder.environment().put("PYTHONIOENCODING", "utf-8");
                processBuilder.redirectErrorStream(true);

                // Run the script in a new thread
                new Thread(() -> {
                    ArrayList<String> logList = new ArrayList<>();
                    try {
                        Tech_Analysis_Controller.script_running_flag = true;

                        Process process = processBuilder.start();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                logList.add(line);
                                System.out.println(line); // Log each output line
                            }
                        }

                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            System.out.println("Python script 'Tech Analysis' finished successfully.");
                        } else {
                            System.err.println("Python script 'Tech Analysis' exited with code: " + exitCode);
                        }
                    } catch (IOException | InterruptedException e) {
                        System.err.println("Error while running Python script: " + e.getMessage());
                        Thread.currentThread().interrupt();
                    } finally {
                        Tech_Analysis_Controller.script_running_flag = false;
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException("Failed to extract Python script", e);
            }

            System.out.println("Python script 'Tech Analysis' is running in the background...");
        }
    }
}