package org.example.dians.Scraping;

import org.example.dians.Component.PythonRunnerFlag;

import java.io.File;
import java.io.IOException;

public class PythonScriptRunner {


    public static void runPythonScript() {
        System.out.println("Starting Python script in background...");

        PythonRunnerFlag.flag=true;

        // Релативна патека до директориумот каде се наоѓа Python скриптата
        File workingDirectory = new File(System.getProperty("user.dir"), "src/main/python");

        // Проверка дали директориумот постои и е валиден
        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new RuntimeException("Invalid directory: " + workingDirectory.getAbsolutePath());
        }

        // Патека до Python интерпретаторот (или Python3)
        String pythonPath = "python"; // Прилагоди ако треба

        // Патека до Python скриптата
        File scriptFile = new File(workingDirectory, "Main.py");

        // Проверка дали Python скриптата постои
        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found: " + scriptFile.getAbsolutePath());
        }

        // Креирај процес за извршување на Python скриптата
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath());

        // Поставување на работниот директориум
        processBuilder.directory(workingDirectory);


        // Креирај нова нишка за да го следи процесот
        new Thread(() -> {
            try {
                // Стартувај ја скриптата во позадина
                Process process = null;
                try {
                    process = processBuilder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int exitCode = process.waitFor(); // Чека завршување на скриптата
                if (exitCode == 0) {
                    System.out.println("Python script finished successfully.");
                    PythonRunnerFlag.flag=false;

                } else {
                    System.err.println("Python script exited with code: " + exitCode);
                }
            } catch (InterruptedException e) {
                System.err.println("Error while waiting for Python script to finish: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }).start();

        System.out.println("Python script is running in the background...");
    }
}