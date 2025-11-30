import java.util.*;

public class Graphe {
    private Map<String, List<Arete>> adjacence = new HashMap<>();

    public void ajouterRoute(String source, String destination, int distance) {
        adjacence.putIfAbsent(source, new ArrayList<>());
        adjacence.putIfAbsent(destination, new ArrayList<>());

        adjacence.get(source).add(new Arete(destination, distance));
        adjacence.get(destination).add(new Arete(source, distance));
    }

    public List<Arete> getVoisins(String sommet) {
        return adjacence.getOrDefault(sommet, new ArrayList<>());
    }

    public Set<String> getSommets() {
        return adjacence.keySet();
    }

    public int getDegre(String sommet) {
        return adjacence.getOrDefault(sommet, Collections.emptyList()).size();
    }

    public ResultatDijkstra calculerCheminPlusCourt(String depart, String arrivee) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecesseurs = new HashMap<>();

        for (String s : adjacence.keySet()) distances.put(s, Integer.MAX_VALUE);
        distances.put(depart, 0);

        PriorityQueue<String> file = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        file.add(depart);

        while (!file.isEmpty()) {
            String u = file.poll();
            if (u.equals(arrivee)) break;
            if (distances.get(u) == Integer.MAX_VALUE) break;

            for (Arete voisin : getVoisins(u)) {
                int newDist = distances.get(u) + voisin.distance;
                if (newDist < distances.get(voisin.destination)) {
                    distances.put(voisin.destination, newDist);
                    predecesseurs.put(voisin.destination, u);
                    file.add(voisin.destination);
                }
            }
        }

        List<String> chemin = new LinkedList<>();
        String curr = arrivee;
        if (predecesseurs.containsKey(curr) || curr.equals(depart)) {
            while (curr != null) {
                chemin.add(0, curr);
                curr = predecesseurs.get(curr);
            }
        }
        return new ResultatDijkstra(distances.get(arrivee), chemin);
    }

    public Graphe copieProfonde() {
        Graphe copie = new Graphe();
        Set<String> dejaTraite = new HashSet<>();

        for (String s : this.getSommets()) {
            for (Arete a : this.getVoisins(s)) {
                if (s.compareTo(a.destination) < 0) {
                    copie.ajouterRoute(s, a.destination, a.distance);
                }
            }
        }
        return copie;
    }
}