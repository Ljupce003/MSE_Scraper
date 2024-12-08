package org.example.dians.Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CSVtoJAVA {
// ne lafi
// tate e tuka
//

    public static List<List<Object>> Filter_Code(String cod){
        String filePath = "src/main/python/Smestuvanje/mega-data.csv";
        List<List<Object>> csvData = new ArrayList<>();
        if (cod.equals("")){
            return csvData;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Прочитај го секој ред
            while ((line = br.readLine()) != null) {
                // Подели го редот на делови според запирка (или друг сепаратор ако е потребно)
                    //ALK,31.7.2015,"4.851,00",,,"4.877,31","0,00",0,0,0
                    //ALK,30.7.2015,"4.851,00","4.899,00","4.851,00","4.877,31","0,56",134,653.560,653.560
                line = line.replace("\",,,\"", "\",# ,\"")   // Замена за празни полиња помеѓу запирки
                        .replace(",\"", "#")             // Замена за почеток на вредност во наводници
                        .replace("\",\"", "#")           // Замена за вредности помеѓу запирки
                        .replace("\",", "#");            // Замена за запирки по вредностите

                // Испечатете ја променетата линија за да видите како изгледа
//                System.out.println("linija-> " + line);

                // Поделба на редот според новиот сепаратор #
                String[] templeit = line.split("#");
                List<Object> row = new ArrayList<>();

                String[] cod_data=templeit[0].split(",");
                if(cod_data[0].equals(cod)){
                    row.add(cod_data[0]);
                    row.add(cod_data[1]);

                    row.add(templeit[1].replace("\"",""));
                    row.add(templeit[2].replace("\"",""));//max
                    row.add(templeit[3].replace("\"",""));//min

//                    if(templeit[2].replace("\"","").equals("")){
//                        row.add(templeit[1].replace("\"",""));
//                    }else {
//                        row.add(templeit[2].replace("\"",""));//max
//                    }
//
//                    if(templeit[3].replace("\"","").equals("")){
//                        row.add(templeit[1].replace("\"",""));
//                    }else {
//                        row.add(templeit[3].replace("\"",""));//min
//                    }

                    row.add(templeit[4].replace("\"",""));
                    row.add(templeit[5]);

                    String[] kol_value_1_2 = templeit[6].split(",");
                    row.add(kol_value_1_2[0]);
                    row.add(kol_value_1_2[1]);
                    row.add(kol_value_1_2[2]);

                    csvData.add(row);

                }



                //           0             1           2           3           4         5            6
                //    ALK,20.11.2020 # 12.499,00 # 12.499,00 # 12.499,00 # 12.499,00 # 0,70 # 29,362.471,362.471
                //    ALK,26.11.2020 # 12.598,00 #           #           # 12.598,22 # 0,00 # 0,0,0



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvData;
    }
    public static void print(List<List<Object>> csvData){
        // Испечатете ги податоците
        for (List<Object> rowData : csvData) {
            System.out.println(rowData);
        }
    }
    public static Map<String, String> Codovi(){

        // Патека до JSON фајлот
        String filePath = "src/main/python/Smestuvanje/names.json";

        // Креирај ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> hvMap = new HashMap<>();


        try {
            // Читање на JSON како општа листа
            List<?> jsonData = objectMapper.readValue(new File(filePath), List.class);

            // Претворање во мапа
            for (Object obj : jsonData) {
                Map<?, ?> record = (Map<?, ?>) obj; // Кастирање на секој објект во мапа
                String key = (String) record.get("Шифра на ХВ");
                String value = (String) record.get("Опис на ХВ");
                hvMap.put(key, value);
            }

        } catch (IOException e) {
            System.err.println("Грешка при читање на JSON фајлот: " + e.getMessage());
        }

        return hvMap;
    }


    public static  List<List<Object>> Filter_Data( List<List<Object>> list,Date from,Date to ){
        if(from==null || to==null){
            return list;
        }

        List<List<Object>> novaData = new ArrayList<>();
        for (List<Object> rowData : list) {
            String parsData= rowData.get(1).toString();




//            System.out.println("pars data "+parsData);

            Date date = new Date(0,0,0);
            // Форматот на датумот
            SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");

            try {
                // Парсирање на датумот
                date = dateFormat.parse(parsData);

                // Испечати го резултатот
//                System.out.println("Parsed date: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(date.before(from) && date.after(to)){

                novaData.add(rowData);
//                System.out.println("Data "+date.toString());
//                System.out.println("From "+from);
//                System.out.println("To "+to+"\n");

            }

        }
        return novaData;
    }
}
