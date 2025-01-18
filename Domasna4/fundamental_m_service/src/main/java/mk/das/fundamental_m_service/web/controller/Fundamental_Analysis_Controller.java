package mk.das.fundamental_m_service.web.controller;

import jakarta.annotation.PostConstruct;
import mk.das.fundamental_m_service.model.Issuer;
import mk.das.fundamental_m_service.service.FileConverterService;
import mk.das.fundamental_m_service.service.PythonScriptRunnerService;
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
public class Fundamental_Analysis_Controller {

    public static boolean script_running_flag=false;
    private final PythonScriptRunnerService scriptRunnerService;
    private final FileConverterService converterService;
    public static LocalDateTime script_last_run_time;

    public Fundamental_Analysis_Controller(PythonScriptRunnerService scriptRunnerService, FileConverterService converterService) {
        this.scriptRunnerService = scriptRunnerService;
        this.converterService = converterService;

    }

    @PostConstruct
    private void init(){
        script_last_run_time=LocalDateTime.now();
        scriptRunnerService.run_script();
    }

    @GetMapping("/fundamental")
    public String showFundamentalPage(Model model) {
//        if(PythonRunnerFlag.flag){
//            model.addAttribute("error","Fetching data...");
//        }
        if(script_running_flag){
            model.addAttribute("analysis_error","Fundamental Analysis is not finished");
        }
        List<String> list = new ArrayList<>(converterService.AnalysisCodes().keySet());

        LocalDateTime time_12hours_ago=LocalDateTime.now().minusHours(12);
        if(script_last_run_time.isBefore(time_12hours_ago)){
            scriptRunnerService.run_script();
        }

        model.addAttribute("codes_dropdown",list.stream().sorted().collect(Collectors.toList()));
        return "fundamental";
    }


    @PostMapping("/fundamental")
    public String showFundamentalPage(@RequestParam String code, Model model) {
        System.out.println(code);
//        if(PythonRunnerFlag.flag){
//            model.addAttribute("error","Python is Running");
//        }
        if(script_running_flag){
            model.addAttribute("analysis_error","Fundamental Analysis is not finished");
        }

        Issuer result= converterService.GetAnalysisResultByCode(code);

        List<String> list = new ArrayList<>(converterService.AnalysisCodes().keySet());

        model.addAttribute("codes_dropdown",list.stream().sorted().collect(Collectors.toList()));
        model.addAttribute("issuer",result);


        LocalDateTime time_12hours_ago=LocalDateTime.now().minusHours(12);
        if(script_last_run_time.isBefore(time_12hours_ago)){
            scriptRunnerService.run_script();
        }

        return "fundamental";
    }
}
