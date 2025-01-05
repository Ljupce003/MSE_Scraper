package mk.das.api_gateway.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LSTM_Controller {

    public LSTM_Controller() {
    }

    @GetMapping("/lstm")
    public String showLstmPage(Model model) {
//        if(PythonRunnerFlag.flag){
//            model.addAttribute("error","Fetching data...");
//        }
        return "lstm";
    }
}
