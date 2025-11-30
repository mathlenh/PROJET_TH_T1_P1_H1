import java.util.*;
import java.io.*;

public class Main {

    public static final int CAPACITE = 10;

    public static void main(String[] args) throws Exception {

        // 1) Chargement du graphe
        Graphe g = new Graphe("graphe.txt");

        // 2) Construction du MST
        MST mst = new MST(g);

        System.out.println("=== Arbre couvrant minimum (ACPM / MST) ===");
        for (Arete a : mst.getMST()) {
            System.out.println(g.getNom(a.u) + " -- " + g.getNom(a.v) + " (" + a.w + ")");
        }

        // 3) Parcours DFS complet dans le MST (marche aller-retour)
        var marche = Chemins.dfsComplet(
                g.getIndexDepot(),
                -1,
                mst.getAdj()
        );

        // 4) Shortcutting : on garde l’ordre de première visite
        var tour = Chemins.shortcutting(marche, g.getIndexDepot());

        // 5) Liste des points à collecter (on enlève D)
        ArrayList<Integer> points = new ArrayList<>();
        for (int x : tour)
            if (x != g.getIndexDepot())
                points.add(x);

        // 6) Ordonner selon la distance au dépôt
        points = Tournees.ordonnerSelonDepot(g, points);

        // 7) Découper selon la capacité
        var tournees = Tournees.decouper(points, g);

        // === AFFICHAGE DU DÉCOUPAGE PAR CAPACITÉ ===
        System.out.println("\n=== Découpage par capacité (collecte) ===");
        for (int i = 0; i < tournees.size(); i++) {
            System.out.print("T" + (i + 1) + " collecte : ");
            int charge = 0;

            for (int p : tournees.get(i)) {
                System.out.print(g.getNom(p) + "(" + g.getQuantite(p) + ") ");
                charge += g.getQuantite(p);
            }
            System.out.println(" | charge = " + charge + "/" + CAPACITE);
        }

        // === ITINÉRAIRES OPTIMISÉS ===
        System.out.println("\n=== Tournées optimisées (itinéraire + distance) ===");

        for (int i = 0; i < tournees.size(); i++) {

            var t = tournees.get(i);

            // Itinéraire détaillé via Dijkstra
            var itin = Tournees.itinOptimise(
                    g.getIndexDepot(),
                    t,
                    g.getMat()
            );

            System.out.print("T" + (i + 1) + " : ");
            for (int k = 0; k < itin.size(); k++) {
                System.out.print(g.getNom(itin.get(k)));
                if (k < itin.size() - 1) System.out.print(" → ");
            }

            // Calcul de la distance totale
            int distanceTotale = 0;
            for (int k = 0; k < itin.size() - 1; k++) {
                int a = itin.get(k);
                int b = itin.get(k + 1);
                distanceTotale += Chemins.dijkstraDistance(a, b, g.getMat());
            }

            System.out.println("   (distance = " + distanceTotale + ")");
        }
    }
}
