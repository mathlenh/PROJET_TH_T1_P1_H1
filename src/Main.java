import java.util.List;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // graphe fictif
        Graphe ville = new Graphe();

        ville.ajouterRoute("Depot", "A", 100);
        ville.ajouterRoute("Depot", "B", 150);
        ville.ajouterRoute("A", "B", 80);
        ville.ajouterRoute("B", "C", 50);
        ville.ajouterRoute("C", "Depot", 200);
        ville.ajouterRoute("A", "D", 120);
        ville.ajouterRoute("C", "E", 100);

        Algo solveur = new Algo(ville);
        Scanner scanner = new Scanner(System.in);
        int choix = 0;

        do {
            System.out.println("  GESTION DES DECHETS - THEME 1         ");
            System.out.println("1. Problématique 1 ");
            System.out.println("2. Problématique 2 ");
            System.out.println("3. Quitter");
            System.out.print("\nVotre choix : ");

            try {
                choix = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur");
                scanner.next();
                continue;
            }

            switch (choix) {
                case 1:
                    lancerProb1Encombrants(ville, solveur);
                    break;
                case 2:
                    lancerProb2Poubelles(solveur);
                    break;
                case 3:
                    System.out.println("Fin");
                    break;
                default:
                    System.out.println("Choix invalide");
            }

        } while (choix != 3);

        scanner.close();
    }

    private static void lancerProb1Encombrants(Graphe ville, Algo solveur) {
        System.out.println("\n [PROB 1] ");

        List<String> maisonsPossibles = new ArrayList<>(ville.getSommets());
        maisonsPossibles.remove("Depot");

        if (maisonsPossibles.isEmpty()) {
            System.out.println("Erreur");
            return;
        }

        Collections.shuffle(maisonsPossibles);
        int nbClients = Math.min(3, maisonsPossibles.size());
        List<String> clients = maisonsPossibles.subList(0, nbClients);

        System.out.println("Points de passage : " + clients);
        solveur.resoudreEncombrants("Depot", clients);

        System.out.println("\n(Entree pour le menu...)");
        try { System.in.read(); } catch(Exception e){}
    }

    // --- SOUS-METHODE POUR LA PROBLEMATIQUE 2 ---
    private static void lancerProb2Poubelles(Algo solveur) {
        System.out.println("\n [PROB 2] ");

        solveur.resoudrePoubelles("Depot");

        System.out.println("\n(Entree pour le menu)");
        try { System.in.read(); } catch(Exception e){}
    }
}