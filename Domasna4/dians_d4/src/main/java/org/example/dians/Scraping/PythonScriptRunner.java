package org.example.dians.Scraping;

import org.example.dians.Component.PythonRunnerFlag;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class PythonScriptRunner {


    public static void runPythonScript() {
        System.out.println("Starting Python script in background...");

        PythonRunnerFlag.flag=true;

        // Релативна патека до директориумот каде се наоѓа Python скриптата
        File workingDirectory = new File(System.getProperty("user.dir"), "Domasna4/dians_d4/src/main/python");   //TODO Voa treba se smene ako frla error file not found


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
        processBuilder.environment().put("PYTHONIOENCODING","utf-8");


        // Поставување на работниот директориум
        processBuilder.directory(workingDirectory);


        // Креирај нова нишка за да го следи процесот
        new Thread(() -> {
            try {
                // Стартувај ја скриптата во позадина
                Process process;
                try {
                    process = processBuilder.start();

                    BufferedReader reader =process.inputReader();
                    String line=reader.readLine();  //TODO neka ostane oti frla weird error
                    reader.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int exitCode = process.waitFor(); // Чека завршување на скриптата
                if (exitCode == 0) {
                    System.out.println("Python script finished successfully.");
                    PythonRunnerFlag.flag=false;

                } else {
                    System.err.println("Python script exited with code: " + exitCode);
                    BufferedReader reader= process.errorReader();
                    String line= reader.readLine();
                    while (line!=null && !line.isEmpty()){
                        System.out.println(line);
                        line= reader.readLine();
                    }
                    reader.close();

                }
            } catch (InterruptedException e) {
                System.err.println("Error while waiting for Python script to finish: " + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        System.out.println("Python script is running in the background...");
    }

    public static void runPythonScriptFundamentalAnalysis() {
        System.out.println("Starting Python script 'Fundamental Analysis' in background...");

        PythonRunnerFlag.analysis_flag=true;

        // Релативна патека до директориумот каде се наоѓа Python скриптата
        File workingDirectory = new File(System.getProperty("user.dir"), "Domasna4/dians_d4/src/main/python");        //TODO Voa treba se smene ako frla error file not found

        // Проверка дали директориумот постои и е валиден
        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new RuntimeException("Invalid directory: " + workingDirectory.getAbsolutePath());
        }


        // Патека до Python интерпретаторот (или Python3)
        //String pythonPath = System.getProperty("user.dir")+"/venv/Scripts/python.exe"; // Прилагоди ако треба

        String pythonPath="C:/Users/Ljupce/Desktop/MSE_Scraper_main/MSE_Scraper-main/Domasna3/dians/venv/Scripts/python.exe";  //TODO Voa treba se smene ako frla error file not found ama voa bara biblioteki za NLP i Nevronski mrezi
        //C:\Users\Ljupce\Desktop\MSE_Scraper_main\MSE_Scraper-main\Domasna3\dians\venv

        // Патека до Python скриптата
        File scriptFile = new File(workingDirectory, "Fundamental_processing.py");

        // Проверка дали Python скриптата постои
        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found: " + scriptFile.getAbsolutePath());
        }

        // Креирај процес за извршување на Python скриптата
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath());
        processBuilder.environment().put("PYTHONIOENCODING","utf-8");


        // Поставување на работниот директориум
        processBuilder.directory(workingDirectory);


        // Креирај нова нишка за да го следи процесот
        new Thread(() -> {
            try {
                // Стартувај ја скриптата во позадина
                Process process;
                try {
                    process = processBuilder.start();

                    BufferedReader reader =process.inputReader();
                    String line=reader.readLine();           //TODO neka ostane oti frla weird error
                    reader.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int exitCode = process.waitFor(); // Чека завршување на скриптата
                if (exitCode == 0) {
                    System.out.println("Python script 'Fundamental Analysis' finished successfully.");
                    PythonRunnerFlag.analysis_flag=false;

                } else {
                    System.err.println("Python script 'Fundamental Analysis' exited with code: " + exitCode);
                    BufferedReader reader= process.errorReader();
                    String line= reader.readLine();
                    while (line!=null && !line.isEmpty()){
                        System.out.println(line);
                        line= reader.readLine();
                    }
                    reader.close();

                }
            } catch (InterruptedException e) {
                System.err.println("Error while waiting for Python script 'Fundamental Analysis' to finish: "
                        + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        System.out.println("Python script 'Fundamental Analysis' is running in the background...");
    }

    public static void runPythonScriptLSTM() {
        System.out.println("Starting Python script 'LSTM' in background...");

        PythonRunnerFlag.lstm_flag=true;

        // Релативна патека до директориумот каде се наоѓа Python скриптата
        File workingDirectory = new File(System.getProperty("user.dir"), "Domasna4/dians_d4/src/main/python");

        // Проверка дали директориумот постои и е валиден
        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new RuntimeException("Invalid directory: " + workingDirectory.getAbsolutePath());
        }


        // Патека до Python интерпретаторот (или Python3)
        String pythonPath="C:/Users/Ljupce/Desktop/MSE_Scraper_main/MSE_Scraper-main/Domasna3/dians/venv/Scripts/python.exe";

        // Патека до Python скриптата
        File scriptFile = new File(workingDirectory, "LSTM.py");

        // Проверка дали Python скриптата постои
        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found: " + scriptFile.getAbsolutePath());
        }

        // Креирај процес за извршување на Python скриптата
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath());
        processBuilder.environment().put("PYTHONIOENCODING","utf-8");


        // Поставување на работниот директориум
        processBuilder.directory(workingDirectory);


        // Креирај нова нишка за да го следи процесот
        new Thread(() -> {
            try {
                // Стартувај ја скриптата во позадина
                Process process;
                try {
                    process = processBuilder.start();

                    BufferedReader reader =process.inputReader();
                    String line=reader.readLine();
                    reader.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int exitCode = process.waitFor(); // Чека завршување на скриптата
                if (exitCode == 0) {
                    System.out.println("Python script 'LSTM' finished successfully.");
                    PythonRunnerFlag.lstm_flag=false;

                } else {
                    System.err.println("Python script 'LSTM' exited with code: " + exitCode);
                    BufferedReader reader= process.errorReader();
                    String line= reader.readLine();
                    while (line!=null && !line.isEmpty()){
                        System.out.println(line);
                        line= reader.readLine();
                    }
                    reader.close();

                }
            } catch (InterruptedException e) {
                System.err.println("Error while waiting for Python script 'LSTM' to finish: "
                        + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        System.out.println("Python script 'LSTM' is running in the background...");
    }
}