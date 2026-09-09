package controller;

import model.ScannerModel;
import model.Vulnerability;
import view.ScannerView;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ScannerController {
    private ScannerModel model;
    private ScannerView view;
    private HttpClient httpClient;

    public ScannerController(ScannerModel model, ScannerView view) {
        this.model = model;
        this.view = view;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void startScan(String targetUrl, String paramName, boolean isPost) {
        view.displayWelcomeMessage();

        // 1. Scan SQLi
        for (String payload : model.getSqlPayloads()) {
            view.displayScanningProgress(targetUrl, payload);
            String response = isPost ? sendPost(targetUrl, paramName, payload) : sendGet(targetUrl, paramName, payload);

            if (response != null && isSqlVulnerable(response)) {
                System.out.println("\n[!!!] CIBLE ATTEINTE : Faille SQL exploitable trouvée avec -> " + payload + "\n");
                model.addVulnerability(new Vulnerability("Injection SQL", targetUrl, payload, paramName, "HIGH"));
            }
            else if (response != null) {
                // DEBUG AVANCÉ : On extrait ce que DVWA nous répond vraiment !
                String lower = response.toLowerCase();
                if (lower.contains("user_token")) {
                    System.out.println("   -> [ECHEC] DVWA est bloqué sur 'Impossible' (Token CSRF exigé).");
                } else if (lower.contains("<pre>")) {
                    int start = lower.indexOf("<pre>");
                    int end = lower.indexOf("</pre>", start);
                    if (start != -1 && end != -1) {
                        String extract = response.substring(start + 5, end).replace("<br />", " | ");
                        System.out.println("   -> [RETOUR SERVEUR] : " + extract.trim());
                    }
                } else {
                    System.out.println("   -> [RETOUR SERVEUR] : Page chargée, mais aucun résultat ni erreur affiché.");
                }
            }
        }

        // 2. Scan XSS (sans le debug avancé pour ne pas polluer)
        for (String payload : model.getXssPayloads()) {
            view.displayScanningProgress(targetUrl, payload);
            String response = isPost ? sendPost(targetUrl, paramName, payload) : sendGet(targetUrl, paramName, payload);

            if (response != null && isXssVulnerable(response, payload)) {
                // On garde l'alerte pour le XSS aussi si tu le souhaites !
                model.addVulnerability(new Vulnerability("Reflected XSS", targetUrl, payload, paramName, "MEDIUM"));
            }
        }

        view.displayResults(model.getVulnerabilities());
    }

    private String sendGet(String baseUrl, String paramName, String payload) {
        try {
            String encodedPayload = URLEncoder.encode(payload, StandardCharsets.UTF_8.toString());

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "?" + paramName + "=" + encodedPayload))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET();

            if (model.getSessionCookie() != null && !model.getSessionCookie().isEmpty()) {
                requestBuilder.header("Cookie", model.getSessionCookie());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 302 || response.statusCode() == 301) return "";
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    private String sendPost(String baseUrl, String paramName, String payload) {
        try {
            // Logique POST
            String encodedData = paramName + "=" + URLEncoder.encode(payload, StandardCharsets.UTF_8.toString());
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .POST(HttpRequest.BodyPublishers.ofString(encodedData));

            if (model.getSessionCookie() != null && !model.getSessionCookie().isEmpty()) {
                requestBuilder.header("Cookie", model.getSessionCookie());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 302 || response.statusCode() == 301) return "";
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSqlVulnerable(String responseBody) {
        String lowerBody = responseBody.toLowerCase();
        return lowerBody.contains("sql syntax") ||
                lowerBody.contains("mysqli_") ||
                lowerBody.contains("surname:"); 
    }

    private boolean isXssVulnerable(String responseBody, String payload) {
        return responseBody.contains(payload);
    }
}
