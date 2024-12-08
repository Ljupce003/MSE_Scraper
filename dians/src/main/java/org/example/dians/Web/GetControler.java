package org.example.dians.Web;

import org.example.dians.Scraping.CSVtoJAVA;
import org.example.dians.Scraping.PythonScriptRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller

public class GetControler {
    public GetControler() {
        PythonScriptRunner.runPythonScript();
    }

    @GetMapping("/scr")
    public String showScrPage(Model model) {
        PythonScriptRunner.runPythonScript();
        return "index";
    }

    @GetMapping("/index")
    public String showIndexPage(Model model) {

        return "index";
    }

    @GetMapping("/tech_analysis")
    public String showTechPage(Model model) {
        List<String> list = new ArrayList<>();
        CSVtoJAVA.Codovi().keySet().forEach(key -> list.add(key));
        model.addAttribute("sifri",list.stream().sorted().collect(Collectors.toList()));
        return "tech_analysis";
    }

    @GetMapping("/fundamental")
    public String showFundamentalPage(Model model) {
        List<String> list = new ArrayList<>();

        CSVtoJAVA.Codovi().keySet().forEach(key -> list.add(key));

        model.addAttribute("sifri",list.stream().sorted().collect(Collectors.toList()));
        return "fundamental";
    }

    @GetMapping("/lstm")
    public String showLstmPage(Model model) {

        return "lstm";
    }

}
