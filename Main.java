import java.util.*;

public class Main {
    public static void main(String[] args) {
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
            System.out.println("PROBLEMATIQUE 1 : LES ENCOMBRANTS   ");
            System.out.println("1. Hypothese 1 ");
            System.out.println("2. Hypothese 2 ");
            System.out.println("3. Quitter");
            System.out.print("\nVotre choix : ");

            try {
                choix = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur.");
                scanner.next();
                continue;
            }

            if (choix == 1 || choix == 2) {
                List<String> maisons = new ArrayList<>(ville.getSommets());
                maisons.remove("Depot");
                Collections.shuffle(maisons);
                int nbClients = Math.max(1, Math.min(4, maisons.size()));
                List<String> clients = maisons.subList(0, nbClients);

                System.out.println("\n--> Clients demandeurs : " + clients);

                if (choix == 1) {
                    solveur.resoudreHypothese1("Depot", clients);
                } else {
                    System.out.print("capacite du camion : ");
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
}