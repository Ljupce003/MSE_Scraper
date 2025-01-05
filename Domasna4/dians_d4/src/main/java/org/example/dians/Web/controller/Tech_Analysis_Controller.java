package org.example.dians.Web.controller;

import org.example.dians.Component.PythonRunnerFlag;
import org.example.dians.Scraping.PythonScriptRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class Tech_Analysis_Controller {

    public Tech_Analysis_Controller() {
        PythonScriptRunner.runPythonScript();
    }

    @GetMapping("/tech_analysis")
    public String showTechPage(Model model) {

        if(PythonRunnerFlag.flag){
            model.addAttribute("error","Fetching data...");
        }
        return "tech_analysis";
    }
}
