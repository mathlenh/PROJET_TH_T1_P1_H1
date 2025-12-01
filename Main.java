import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Graphe ville = chargerGraphe("graphe.txt");

        if (ville == null) {
            System.out.println("Impossible de charger le graphe.");
            return;
        }

        Algo solveur = new Algo(ville);
        Scanner scanner = new Scanner(System.in);
        int choix = 0;

        do {
            System.out.println("\n PROBLEMATIQUE 1 : LES ENCOMBRANTS   ");
            System.out.println("1. Hypothese 1");
            System.out.println("2. Hypothese 2");
            System.out.println("3. Quitter");
            System.out.print("\nVotre choix : ");

            try {
                choix = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur");
                scanner.next();
                continue;
            }

            if (choix == 1 || choix == 2) {
                List<String> maisons = new ArrayList<>(ville.getSommets());
                maisons.remove("Depot");

                if (maisons.isEmpty()) {
                    System.out.println("Erreur");
                    continue;
                }

                Collections.shuffle(maisons);
                int nbClients = Math.max(1, Math.min(4, maisons.size()));
                List<String> clients = maisons.subList(0, nbClients);

                System.out.println("\n--> Clients : " + clients);

                if (choix == 1) {
                    solveur.resoudreHypothese1("Depot", clients);
                } else {
                    System.out.print("Capacite du camion : ");
                    int capa = scanner.nextInt();
                    solveur.resoudreHypothese2("Depot", clients, capa);
                }

                System.out.println("\n(Entree pour continuer)");
                try { System.in.read(); } catch(Exception e){}
            }

        } while (choix != 3);

        System.out.println("Fin");
        scanner.close();
    }

    public static Graphe chargerGraphe(String nomFichier) {
        Graphe g = new Graphe();
        System.out.println("Chargement du fichier : " + nomFichier + " ...");

        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] parties = ligne.split(";");

                if (parties.length == 3) {
                    String depart = parties[0].trim();
                    String arrivee = parties[1].trim();
                    int distance = Integer.parseInt(parties[2].trim());

                    g.ajouterRoute(depart, arrivee, distance);
                    }
            }
            System.out.println("Graphe" + g.getSommets().size() + " sommets.");
            return g;

        } catch (IOException e) {
            System.out.println("Erreur " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.out.println("Erreur");
            return null;
        }
    }
}
