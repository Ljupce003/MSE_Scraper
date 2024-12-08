package org.example.dians.Scraping;

import java.io.IOException;

public class PythonScriptRunner {

    public static void runPythonScript() {
        System.out.println("Running Python script in background...");

        // Патека до Python интерпретаторот (или Python3)
        String pythonPath = "python";  // Може да биде "python3" во зависност од твојата конфигурација
        String scriptPath = "C:/Users/ljupc/IdeaProjects/dians/src/main/python/Main.py";  // Патека до твојата Python скрипта

        // Креирај процес за да ја стартуваш Python скриптата
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

        // Постави работна директорија ако е потребно
        processBuilder.directory(new java.io.File("src/main/python"));
        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
