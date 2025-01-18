package mk.das.fundamental_m_service.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import mk.das.fundamental_m_service.model.Issuer;
import mk.das.fundamental_m_service.service.FileConverterService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileConverterServiceImplemented implements FileConverterService {
    @Override
    public Map<String, String> AnalysisCodes() {
        // Патека до JSON фајлот
        String filePath = System.getProperty("user.dir") + "/src/main/python/Smestuvanje/channels.json";  //TODO Voa treba se smene ako frla error file not found


        // Креирај ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> hvMap = new HashMap<>();


        try {
            // Читање на JSON како општа листа
            List<?> jsonData = objectMapper.readValue(new File(filePath), List.class);

            // Претворање во мапа
            for (Object obj : jsonData) {
                Map<?, ?> record = (Map<?, ?>) obj; // Кастирање на секој објект во мапа
                String key = (String) record.get("code");
                String value = (String) record.get("title");
                hvMap.put(key, value);
            }

        } catch (IOException e) {
            System.err.println("Грешка при читање на JSON фајлот за Фундаментална Анализа: " + e.getMessage());
            System.err.println("Current Workdir is "+System.getProperty("user.dir"));

        }

        return hvMap;
    }

    @Override
    public Issuer GetAnalysisResultByCode(String code) {
        try {

            String filePath = System.getProperty("user.dir") + "/src/main/python/Smestuvanje/channels.json"; //TODO Voa treba se smene ako frla error file not found poradi toa shto docker e druga okolina

            // Parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            Issuer[] dataArray = mapper.readValue(new File(filePath), Issuer[].class);

            // Filter by code
            Issuer returnIssuer=null;
            for (Issuer data : dataArray) {
                if (data.getCode().equals(code)) {
                    returnIssuer=data;
                    break;
                }
            }
            return returnIssuer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
