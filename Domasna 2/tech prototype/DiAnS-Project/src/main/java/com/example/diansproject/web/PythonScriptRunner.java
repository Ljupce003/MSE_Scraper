package com.example.diansproject.web;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Component
public class PythonScriptRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void runPythonScript() {
        try {
            // Print the working directory to debug path issues
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + workingDir);

            // Run Python script
            String command = "python ../MSE_Scraper/Domasna1/Filtri/Main.py";
            Process process = Runtime.getRuntime().exec(command);

            // Capture standard output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Output: " + line);
            }

            // Capture error output from the Python script
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script finished successfully.");
            } else {
                System.err.println("Python script failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error executing Python script.");
        }
    }
}
