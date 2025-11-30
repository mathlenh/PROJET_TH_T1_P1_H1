import java.util.*;

public class Algo {
    private Graphe graphe;

    public Algo(Graphe graphe) {
        this.graphe = graphe;
    }

    // Pb1
    public void resoudreEncombrants(String depart, List<String> aVisiter) {
        System.out.println("\n=== THEME 1 - PROB 1 ===");

        List<String> restants = new ArrayList<>(aVisiter);
        String positionActuelle = depart;
        int distanceTotale = 0;
        List<String> itineraireGlobal = new ArrayList<>();
        itineraireGlobal.add(depart);

        while (!restants.isEmpty()) {
            String prochainPoint = null;
            int minDistance = Integer.MAX_VALUE;
            List<String> meilleurChemin = null;

            // Dijkstra
            for (String cible : restants) {
                ResultatDijkstra res = graphe.calculerCheminPlusCourt(positionActuelle, cible);
                if (res.distance < minDistance) {
                    minDistance = res.distance;
                    prochainPoint = cible;
                    meilleurChemin = res.chemin;
                }
            }

            if (prochainPoint != null) {
                System.out.println(" -> De " + positionActuelle + " vers " + prochainPoint + " (coût : " + minDistance + ")");
                if (meilleurChemin.size() > 1) {
                    itineraireGlobal.addAll(meilleurChemin.subList(1, meilleurChemin.size()));
                }

                distanceTotale += minDistance;
                positionActuelle = prochainPoint;
                restants.remove(prochainPoint);
            }
        }

        // Retour
        ResultatDijkstra retour = graphe.calculerCheminPlusCourt(positionActuelle, depart);
        System.out.println(" -> Retour au dépôt (coût : " + retour.distance + ")");
        if (retour.chemin.size() > 1) {
            itineraireGlobal.addAll(retour.chemin.subList(1, retour.chemin.size()));
        }
        distanceTotale += retour.distance;

        System.out.println("DISTANCE TOTALE : " + distanceTotale);
        System.out.println("ITINERAIRE FINAL : " + itineraireGlobal);
    }

    // Pb2
    public void resoudrePoubelles(String depart) {
        System.out.println("\n=== THEME 1 - PROB 2 ===");

        // sommets impairs
        List<String> impairs = new ArrayList<>();
        for (String s : graphe.getSommets()) {
            if (graphe.getDegre(s) % 2 != 0) {
                impairs.add(s);
            }
        }
        System.out.println("Sommets impairs : " + impairs);

        Graphe grapheEulerien = graphe.copieProfonde();

        while (!impairs.isEmpty()) {
            String u = impairs.remove(0);
            String v = null;
            int minDist = Integer.MAX_VALUE;

            for (String candidat : impairs) {
                ResultatDijkstra res = graphe.calculerCheminPlusCourt(u, candidat);
                if (res.distance < minDist) {
                    minDist = res.distance;
                    v = candidat;
                }
            }

            if (v != null) {
                impairs.remove(v);
                System.out.println("On repassera entre " + u + " et " + v);
                grapheEulerien.ajouterRoute(u, v, minDist);
            }
        }

        List<String> cycle = new ArrayList<>();
        Map<String, List<String>> adjMutable = new HashMap<>();
        for(String s : grapheEulerien.getSommets()) {
            adjMutable.put(s, new ArrayList<>());
            for(Arete a : grapheEulerien.getVoisins(s)) adjMutable.get(s).add(a.destination);
        }

        dfsEulerien(depart, adjMutable, cycle);
        Collections.reverse(cycle);

        System.out.println("TOURNEE COMPLETE : " + cycle);
    }

    private void dfsEulerien(String u, Map<String, List<String>> adj, List<String> chemin) {
        while (adj.get(u) != null && !adj.get(u).isEmpty()) {
            String v = adj.get(u).remove(0);
            adj.get(v).remove(u);
            dfsEulerien(v, adj, chemin);
        }
        chemin.add(u);
    }
}