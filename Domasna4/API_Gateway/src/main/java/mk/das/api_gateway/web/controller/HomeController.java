package mk.das.api_gateway.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    public HomeController() {}

    @GetMapping(path = {"/index","/"})
    public String showIndexPage(Model model) {
//        if(PythonRunnerFlag.flag){
//           model.addAttribute("error","Fetching data...");
//        }
//        System.out.println(PythonRunnerFlag.flag);

        return "index";
    }
}
