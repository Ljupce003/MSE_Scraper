package mk.das.api_gateway.web.controller;


import mk.das.api_gateway.service.FileDownloadService;

import mk.das.api_gateway.model.Issuer;
import mk.das.api_gateway.utilities.CSVtoJAVA;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Fundamental_Analysis_Controller {
    private final FileDownloadService fileDownloadService;

    public static LocalDateTime fund_last_fetch_date=LocalDateTime.now();

    public Fundamental_Analysis_Controller(FileDownloadService fileDownloadService) {

        this.fileDownloadService = fileDownloadService;
    }


    @GetMapping("/fundamental")
    public String showFundamentalPage(Model model) {

        List<String> list = new ArrayList<>(CSVtoJAVA.AnalysisCodes().keySet());


        this.fileDownloadService.downloadFundamentalFile("http://fundamental-microservice:8092/download/result");


        model.addAttribute("codes_dropdown",list.stream().sorted().collect(Collectors.toList()));
        return "fundamental";
    }




    @PostMapping("/fundamental")
    public String showFundamentalPage(@RequestParam String code, Model model) {
        System.out.println(code);

        this.fileDownloadService.downloadFundamentalFile("http://fundamental-microservice:8092/download/result");

        Issuer result= CSVtoJAVA.GetAnalysisResultByCode(code);

        List<String> list = new ArrayList<>(CSVtoJAVA.AnalysisCodes().keySet());

        model.addAttribute("codes_dropdown",list.stream().sorted().collect(Collectors.toList()));
        model.addAttribute("issuer",result);

        return "fundamental";
    }


}



