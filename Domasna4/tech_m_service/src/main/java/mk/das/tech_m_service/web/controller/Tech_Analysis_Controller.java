package mk.das.tech_m_service.web.controller;

import jakarta.annotation.PostConstruct;
import mk.das.tech_m_service.service.PythonScriptRunnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class Tech_Analysis_Controller {

    public static boolean script_running_flag=false;
    private final PythonScriptRunnerService scriptRunnerService;
    public static LocalDateTime script_last_run_time;

    public Tech_Analysis_Controller(PythonScriptRunnerService scriptRunnerService) {
        this.scriptRunnerService = scriptRunnerService;

    }

    @PostConstruct
    private void init() throws URISyntaxException {
        script_last_run_time=LocalDateTime.now();
        scriptRunnerService.run_script();
    }


    @GetMapping({"/","/index"})
    public String showIndexPage(Model model) throws URISyntaxException {

        if(script_running_flag){
            model.addAttribute("error","Tech Analysis is not finished");
        }

        LocalDateTime time_12hours_ago=LocalDateTime.now().minusHours(12);
        if(script_last_run_time.isBefore(time_12hours_ago)){
            scriptRunnerService.run_script();
        }

        return "index";
    }

    @GetMapping("/tech_analysis")
    public String showFundamentalPage(Model model) throws URISyntaxException {

        if(script_running_flag){
            model.addAttribute("error","Tech Analysis is not finished");
        }

        LocalDateTime time_12hours_ago=LocalDateTime.now().minusHours(12);
        if(script_last_run_time.isBefore(time_12hours_ago)){
            scriptRunnerService.run_script();
        }

        return "tech_analysis";
    }
}
