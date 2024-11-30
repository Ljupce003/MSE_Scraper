package com.example.diansproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class controller {


    @GetMapping("/index")
    public String showIndexPage(Model model) {

        return "index";
    }

    @GetMapping("/tech_analysis")
    public String showTechPage(Model model) {

        return "tech_analysis";
    }

    @GetMapping("/fundamental")
    public String showFundamentalPage(Model model) {

        return "fundamental";
    }

    @GetMapping("/lstm")
    public String showLstmPage(Model model) {

        return "lstm";
    }

}
