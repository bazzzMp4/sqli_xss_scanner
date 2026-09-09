package model;

import java.util.ArrayList;
import java.util.List;

public class ScannerModel {
    private List<Vulnerability> vulnerabilities;
    private List<String> sqlPayloads;
    private List<String> xssPayloads;
    private String sessionCookie;

    public ScannerModel() {
        this.vulnerabilities = new ArrayList<>();
        this.sessionCookie = "";

        // Payloads SQL Injection (ciblent les bases de données)
        this.sqlPayloads = new ArrayList<>();
        this.sqlPayloads.add("' OR 1=1--");
        this.sqlPayloads.add("admin' #");
        this.sqlPayloads.add("' UNION SELECT null, null--");

        // Payloads Cross-Site Scripting (ciblent le navigateur du client)
        this.xssPayloads = new ArrayList<>();
        this.xssPayloads.add("<script>alert('XSS')</script>");
        this.xssPayloads.add("\"><img src=x onerror=alert(1)>");
    }

    // Gestion des vulnérabilités
    public void addVulnerability(Vulnerability v) {
        vulnerabilities.add(v);
    }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    // Récupération des payloads
    public List<String> getSqlPayloads() {
        return sqlPayloads;
    }

    public List<String> getXssPayloads() {
        return xssPayloads;
    }

    // Gestion du cookie de session
    public void setSessionCookie(String cookie) {
        this.sessionCookie = cookie;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }
}