package org.example.dians.Web.controller;

import org.example.dians.Component.PythonRunnerFlag;
import org.example.dians.Scraping.PythonScriptRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LSTM_Controller {

    public LSTM_Controller() {
        PythonScriptRunner.runPythonScriptLSTM();
    }

    @GetMapping("/lstm")
    public String showLstmPage(Model model) {
        if(PythonRunnerFlag.flag){
            model.addAttribute("error","Fetching data...");
        }
        return "lstm";
    }
}
