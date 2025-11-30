import java.util.*;

public class Algo {
    private Graphe graphe;

    public Algo(Graphe graphe) {
        this.graphe = graphe;
    }

    public void resoudreHypothese1(String depot, List<String> clients) {
        System.out.println("\nHYPOTHESE 1 :");
        int distanceTotale = 0;

        for (String client : clients) {
            System.out.println("--- Client : " + client + " ---");

            ResultatDijkstra aller = graphe.calculerCheminPlusCourt(depot, client);
            System.out.println("   Aller   : " + aller.chemin + " (" + aller.distance + "m)");

            ResultatDijkstra retour = graphe.calculerCheminPlusCourt(client, depot);
            System.out.println("   Retour  : " + retour.chemin + " (" + retour.distance + "m)");

            int coutMission = aller.distance + retour.distance;
            distanceTotale += coutMission;
        }
        System.out.println("\nDISTANCE TOTALE CUMULEE (Hypothèse 1) : " + distanceTotale + "m");
    }

    public void resoudreHypothese2(String depot, List<String> clients, int capaciteMax) {
        System.out.println("\n HYPOTHESE 2 : (Capacité : " + capaciteMax + ") ===");

        List<String> aTraiter = new ArrayList<>(clients);
        int distanceTotale = 0;
        int numTournee = 1;

        while (!aTraiter.isEmpty()) {
            System.out.println("\n>> Démarrage Tournée n" + numTournee);
            String position = depot;
            int chargement = 0;
            List<String> trajetTournee = new ArrayList<>();
            trajetTournee.add(depot);

            while (chargement < capaciteMax && !aTraiter.isEmpty()) {

                String meilleurClient = null;
                int minDist = Integer.MAX_VALUE;
                List<String> cheminVersClient = null;

                for (String candidat : aTraiter) {
                    ResultatDijkstra res = graphe.calculerCheminPlusCourt(position, candidat);
                    if (res.distance < minDist) {
                        minDist = res.distance;
                        meilleurClient = candidat;
                        cheminVersClient = res.chemin;
                    }
                }

                if (meilleurClient != null) {
                    distanceTotale += minDist;
                    position = meilleurClient;
                    chargement++;

                    aTraiter.remove(meilleurClient);

                    if (cheminVersClient.size() > 1) {
                        trajetTournee.addAll(cheminVersClient.subList(1, cheminVersClient.size()));
                    }
                    System.out.println("   + Collecte à " + meilleurClient + " (Distance: " + minDist + "m)");
                }
            }

            ResultatDijkstra retour = graphe.calculerCheminPlusCourt(position, depot);
            distanceTotale += retour.distance;

            if (retour.chemin.size() > 1) {
                trajetTournee.addAll(retour.chemin.subList(1, retour.chemin.size()));
            }

            System.out.println("   -> Retour au dépôt (" + retour.distance + "m)");
            System.out.println("   Bilan Tournée : " + trajetTournee);
            numTournee++;
        }

        System.out.println("\nDISTANCE TOTALE (Hypothèse 2) : " + distanceTotale + "m");
    }
}