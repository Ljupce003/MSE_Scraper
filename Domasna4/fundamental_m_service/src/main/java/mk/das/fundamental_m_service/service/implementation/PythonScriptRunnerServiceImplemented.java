package mk.das.fundamental_m_service.service.implementation;

import mk.das.fundamental_m_service.service.PythonScriptRunnerService;
import mk.das.fundamental_m_service.web.controller.Fundamental_Analysis_Controller;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


@Service
public class PythonScriptRunnerServiceImplemented implements PythonScriptRunnerService {



    @Override
    public void run_script() {

        if(!Fundamental_Analysis_Controller.script_running_flag) {

            System.out.println("Starting Python script 'Fundamental Analysis' in background...");

            // Релативна патека до директориумот каде се наоѓа Python скриптата
//            File workingDirectory = new File(System.getProperty("user.dir"), "Domasna4/fundamental_m_service/src/main/python");        //TODO Voa treba se smene ako frla error file not found
            File workingDirectory = new File(System.getProperty("user.dir"), "/src/main/python");

            // Проверка дали директориумот постои и е валиден
            if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
                throw new RuntimeException("Invalid directory: " + workingDirectory.getAbsolutePath());
            }

//            String pythonPath="C:/Users/Ljupce/Desktop/MSE_Scraper_main/MSE_Scraper-main/Domasna3/dians/venv/Scripts/python.exe";  //TODO Voa treba se smene ako frla error file not found ama voa bara biblioteki za NLP i Nevronski mrezi
            String pythonPath="python3";

            // Патека до Python скриптата
            File scriptFile = new File(workingDirectory, "Fundamental_processing.py");

            // Проверка дали Python скриптата постои
            if (!scriptFile.exists()) {
                throw new RuntimeException("Python script not found: " + scriptFile.getAbsolutePath());
            }

            // Креирај процес за извршување на Python скриптата
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptFile.getAbsolutePath());
            processBuilder.environment().put("PYTHONIOENCODING","utf-8");

            processBuilder.redirectErrorStream(true);



            // Поставување на работниот директориум
            processBuilder.directory(workingDirectory);

            // Креирај нова нишка за да го следи процесот
            new Thread(() -> {
                ArrayList<String> log_list=new ArrayList<>();
                try {
                    // Стартувај ја скриптата во позадина
                    Process process;

                    try {
                        Fundamental_Analysis_Controller.script_running_flag=true;
                        process = processBuilder.start();

                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                log_list.add(line);
                            }
                        }


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int exitCode = process.waitFor(); // Чека завршување на скриптата
                    if (exitCode == 0) {
                        System.out.println("Python script 'Fundamental Analysis' finished successfully.");
                        Fundamental_Analysis_Controller.script_last_run_time= LocalDateTime.now();

                    } else {
                        System.err.println("Python script 'Fundamental Analysis' exited with code: " + exitCode);

                        for (String s : log_list) {
                            System.out.println("PROCESS OUT:"+s);
                        }

                    }
                } catch (InterruptedException e) {
                    System.err.println("Error while waiting for Python script 'Fundamental Analysis' to finish: "
                            + e.getMessage());
                    Thread.currentThread().interrupt();
                } finally {
                    Fundamental_Analysis_Controller.script_running_flag=false;
                }
            }).start();

            System.out.println("Python script 'Fundamental Analysis' is running in the background...");
        }


    }
}
