import java.util.*;

public class Dijkstra {

    public static Map<Sommet, Double> distances;
    public static Map<Sommet, Sommet> predecesseurs;

    public static void calculer(Graphe g, Sommet source) {
        distances = new HashMap<>();
        predecesseurs = new HashMap<>();
        PriorityQueue<Sommet> file = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        // Initialisation
        for (Sommet s : g.getSommets()) {
            distances.put(s, Double.POSITIVE_INFINITY);
            predecesseurs.put(s, null);
        }
        distances.put(source, 0.0);

        file.add(source);

        while (!file.isEmpty()) {
            Sommet courant = file.poll();

            for (ArcSortant arc : courant.arcsSortants) {
                Sommet voisin = arc.destination;
                double nouvelleDistance = distances.get(courant) + arc.poids;

                if (nouvelleDistance < distances.get(voisin)) {
                    distances.put(voisin, nouvelleDistance);
                    predecesseurs.put(voisin, courant);

                    file.remove(voisin);
                    file.add(voisin);
                }
            }
        }
    }

    public static List<Sommet> reconstruireChemin(Sommet destination) {
        List<Sommet> chemin = new ArrayList<>();
        Sommet courant = destination;

        while (courant != null) {
            chemin.add(courant);
            courant = predecesseurs.get(courant);
        }
        Collections.reverse(chemin);
        return chemin;
    }
}

puvlnvba
