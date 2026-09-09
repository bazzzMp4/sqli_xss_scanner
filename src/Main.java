import controller.ScannerController;
import model.ScannerModel;
import view.ScannerView;

public class Main {
    public static void main(String[] args) {
        ScannerModel model = new ScannerModel();
        ScannerView view = new ScannerView();

        // 1. Définition des cookies de session pour DVWA
        String myCookies = "PHPSESSID=votre_valeur_ici; security=low";
        model.setSessionCookie(myCookies);

        ScannerController controller = new ScannerController(model, view);

        // 2. Scan d'une page interne protégée (ex: XSS Reflected) : à décommenter pour un scanner xss
        //String targetUrl = "http://localhost/vulnerabilities/xss_r/";
        //controller.startScan(targetUrl, "name", false);

        // 2. Scan de la page SQL Injection
        String targetUrl = "http://localhost/vulnerabilities/sqli/";

        controller.startScan(targetUrl, "Submit=Submit&id", false);
    }
}
