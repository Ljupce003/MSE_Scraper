package org.example.dians.Web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dians.Scraping.CSVtoJAVA;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostControler {

    @PostMapping("/techanalysis")
    public String showTechPage(@RequestParam String sif, @RequestParam String toDate, @RequestParam String fromDate, Model model) throws Exception {
        System.out.println(sif);
        Date from;
        Date to;
        if(toDate=="" || fromDate==""){
            from=null;
            to=null;
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");

            from=dateFormat.parse(toDate);
            to=dateFormat.parse(fromDate);

        }


        List<List<Object>> csvData = CSVtoJAVA.Filter_Code(sif);
        csvData=CSVtoJAVA.Filter_Data(csvData,from,to);
//        CSVtoJAVA.print(csvData);


        // Претвори ја листата во JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String csvDataJson = null;
        try {
            csvDataJson = objectMapper.writeValueAsString(csvData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Додај го JSON-от во моделот
        model.addAttribute("csvData", csvDataJson);

//        CSVtoJAVA.print(CSVtoJAVA.Filter_Code("ALK"));

        List<String> list = new ArrayList<>();
        CSVtoJAVA.Codovi().keySet().forEach(key -> list.add(key));

        model.addAttribute("sifri",list.stream().sorted().collect(Collectors.toList()));

        return "tech_analysis"; // Thymeleaf view
    }

    @PostMapping("/fundamental")
    public String showFundamentalPage(@RequestParam String sif, @RequestParam String toDate,@RequestParam String fromDate,Model model) {
        System.out.println(sif);
        Date from;
        Date to;
        if(toDate=="" || fromDate==""){
            from=null;
            to=null;
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
            try {
                from=dateFormat.parse(toDate);
                to=dateFormat.parse(fromDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        List<List<Object>> csvData = CSVtoJAVA.Filter_Code(sif);
        csvData=CSVtoJAVA.Filter_Data(csvData,from,to);

//        CSVtoJAVA.print(csvData);
        // Претвори ја листата во JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String csvDataJson = null;
        try {
            csvDataJson = objectMapper.writeValueAsString(csvData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        // Додај го JSON-от во моделот
        model.addAttribute("csvData", csvDataJson);

        List<String> list = new ArrayList<>();
        CSVtoJAVA.Codovi().keySet().forEach(key -> list.add(key));

        model.addAttribute("sifri",list.stream().sorted().collect(Collectors.toList()));

        return "fundamental";
    }
}
