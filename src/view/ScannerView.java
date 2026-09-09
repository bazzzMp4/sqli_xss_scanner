package view;

import model.Vulnerability;
import java.util.List;

public class ScannerView {
    public void displayWelcomeMessage() {
        System.out.println("[*] Démarrage du moteur de scan...");
    }

    public void displayScanningProgress(String url, String payload) {
        // Optionnel : on peut commenter cette ligne si on veut un affichage totalement silencieux (style UNIX)
        System.out.println("[~] Audit de la cible avec le payload : " + payload);
    }

    public void displayResults(List<Vulnerability> vulns) {
        System.out.println("\n================ EXPORT JSON ================");
        if (vulns.isEmpty()) {
            System.out.println("[]"); // Tableau JSON vide
        } else {
            System.out.println("[");
            for (int i = 0; i < vulns.size(); i++) {
                System.out.print(vulns.get(i).toJson());
                if (i < vulns.size() - 1) {
                    System.out.println(","); // Virgule entre chaque objet
                } else {
                    System.out.println();
                }
            }
            System.out.println("]");
        }
        System.out.println("=============================================");
    }
}