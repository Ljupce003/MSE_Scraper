package mk.das.fundamental_m_service.service;

import mk.das.fundamental_m_service.model.Issuer;

import java.util.Map;

public interface FileConverterService {

    public Map<String, String> AnalysisCodes();

    public Issuer GetAnalysisResultByCode(String code);
}
