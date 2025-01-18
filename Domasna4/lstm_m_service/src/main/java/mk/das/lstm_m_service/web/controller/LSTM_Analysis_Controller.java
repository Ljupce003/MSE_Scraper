package mk.das.lstm_m_service.web.controller;

import jakarta.annotation.PostConstruct;
import mk.das.lstm_m_service.service.PythonScriptRunnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LSTM_Analysis_Controller {

    public static boolean script_running_flag=false;
    private final PythonScriptRunnerService scriptRunnerService;
    public static LocalDateTime script_last_run_time;

    public LSTM_Analysis_Controller(PythonScriptRunnerService scriptRunnerService) {
        this.scriptRunnerService = scriptRunnerService;

    }

    @PostConstruct
    private void init(){
        script_last_run_time=LocalDateTime.now();
        scriptRunnerService.run_script();
    }

    @GetMapping("/lstm")
    public String showFundamentalPage(Model model) {

        if(script_running_flag){
            model.addAttribute("lstm_error","Lstm is not finished");
        }

        LocalDateTime time_12hours_ago=LocalDateTime.now().minusHours(12);

        if(script_last_run_time.isBefore(time_12hours_ago)){
            scriptRunnerService.run_script();
        }

        return "lstm";
    }
}
