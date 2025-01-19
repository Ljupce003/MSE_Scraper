package mk.das.api_gateway.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class Tech_Analysis_Controller {

    public Tech_Analysis_Controller() {}

    @GetMapping("/tech_analysis")
    public String showTechPage(Model model) {


        return "tech_analysis";
    }
}
