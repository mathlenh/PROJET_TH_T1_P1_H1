import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;
    static final int CAPACITE = 10;

    // Structure d’arête simple
    static class Arete {
        int u, v, w;
        Arete(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(new File("graphe.txt"));

        // -------------------------------
        // Lecture du fichier texte
        // -------------------------------
        int n = sc.nextInt();
        String[] noms = new String[n];
        int[] quantites = new int[n];

        for (int i = 0; i < n; i++) {
            noms[i] = sc.next();
            quantites[i] = sc.nextInt();
        }

        int[][] matrice = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrice[i][j] = sc.nextInt();

        // -------------------------------
        // Construction de la liste des arêtes du graphe
        // -------------------------------
        ArrayList<Arete> aretes = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                if (matrice[i][j] != INF && matrice[i][j] != 0)
                    aretes.add(new Arete(i, j, matrice[i][j]));

        // Tri par poids (Kruskal)
        Collections.sort(aretes, (a, b) -> a.w - b.w);

        // -------------------------------
        // Kruskal : Arbre couvrant minimum
        // -------------------------------
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        ArrayList<Arete> mst = new ArrayList<>();

        for (Arete a : aretes) {
            int ru = trouver(parent, a.u);
            int rv = trouver(parent, a.v);
            if (ru != rv) {
                mst.add(a);
                parent[ru] = rv;
            }
        }

        System.out.println("=== Arbre couvrant minimum (Kruskal) ===");
        for (Arete a : mst)
            System.out.println(noms[a.u] + " -- " + noms[a.v] + " (" + a.w + ")");

        // -------------------------------
        // Construire adjacency du MST
        // -------------------------------
        ArrayList<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new ArrayList<>();

        for (Arete a : mst) {
            adj[a.u].add(a.v);
            adj[a.v].add(a.u);
        }

        // Trouver le dépôt D
        int depot = 0;
        for (int i = 0; i < n; i++)
            if (noms[i].equals("D")) depot = i;

        // -------------------------------
        // Parcours DFS complet (aller-retour)
        // -------------------------------
        ArrayList<Integer> marcheComplete = new ArrayList<>();
        dfsMarche(depot, -1, adj, marcheComplete);
        marcheComplete.add(depot);

        // -------------------------------
        // Shortcutting (on retire les doublons)
        // -------------------------------
        ArrayList<Integer> tourSansDoublons = new ArrayList<>();
        boolean[] vu = new boolean[n];

        for (int x : marcheComplete) {
            if (!vu[x]) {
                tourSansDoublons.add(x);
                vu[x] = true;
            }
        }
        tourSansDoublons.add(depot);

        // -------------------------------
        // Liste des points à collecter (sans D)
        // -------------------------------
        ArrayList<Integer> points = new ArrayList<>();
        for (int x : tourSansDoublons)
            if (x != depot) points.add(x);

        // -------------------------------
        // ORDONNANCEMENT PAR PROXIMITÉ AU DÉPÔT
        // (Optimisation voulue pour faire T1 = P1, P2)
        // -------------------------------
        points = ordonnerSelonDistance(depot, points, matrice);

        // -------------------------------
        // Découpage selon la capacité
        // -------------------------------
        ArrayList<ArrayList<Integer>> tournees = decouperParCapacite(points, quantites);

        System.out.println("\n=== Découpage (collecte) ===");
        for (int i = 0; i < tournees.size(); i++) {
            System.out.print("T" + (i + 1) + " collecte : ");
            for (int p : tournees.get(i))
                System.out.print(noms[p] + " ");
            System.out.println();
        }

        // -------------------------------
        // Itinéraires optimisés SANS passages inutiles
        // (via Dijkstra chemin complet)
        // -------------------------------
        System.out.println("\n=== Itinéraires optimisés (sans traversées inutiles) ===");

        for (int i = 0; i < tournees.size(); i++) {

            ArrayList<Integer> t = tournees.get(i);

            ArrayList<Integer> itin = construireItinOptimise(depot, t, matrice);

            System.out.print("T" + (i + 1) + " : ");
            for (int k = 0; k < itin.size(); k++) {
                System.out.print(noms[itin.get(k)]);
                if (k < itin.size() - 1) System.out.print(" → ");
            }
            System.out.println();
        }
    }

    // ------------------------------------------------------
    // Union-Find
    // ------------------------------------------------------
    static int trouver(int[] parent, int x) {
        while (parent[x] != x)
            x = parent[x];
        return x;
    }

    // ------------------------------------------------------
    // DFS marche complète (aller-retour)
    // ------------------------------------------------------
    static void dfsMarche(int u, int pere, ArrayList<Integer>[] adj, ArrayList<Integer> marche) {
        marche.add(u);
        for (int v : adj[u])
            if (v != pere) {
                dfsMarche(v, u, adj, marche);
                marche.add(u);
            }
    }

    // ------------------------------------------------------
    // Dijkstra (retourne distance)
    // ------------------------------------------------------
    static int dijkstraDistance(int s, int t, int[][] mat) {
        int n = mat.length;
        int[] dist = new int[n];
        boolean[] utilise = new boolean[n];

        Arrays.fill(dist, INF);
        dist[s] = 0;

        for (int k = 0; k < n; k++) {
            int u = -1, best = INF;
            for (int i = 0; i < n; i++)
                if (!utilise[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }
            if (u == -1) break;

            utilise[u] = true;

            for (int v = 0; v < n; v++)
                if (mat[u][v] != INF)
                    dist[v] = Math.min(dist[v], dist[u] + mat[u][v]);
        }

        return dist[t];
    }

    // ------------------------------------------------------
    // Dijkstra pour obtenir le CHEMIN complet
    // ------------------------------------------------------
    static ArrayList<Integer> dijkstraChemin(int s, int t, int[][] mat) {
        int n = mat.length;
        int[] dist = new int[n];
        int[] precedent = new int[n];
        boolean[] utilise = new boolean[n];

        Arrays.fill(dist, INF);
        Arrays.fill(precedent, -1);
        dist[s] = 0;

        for (int k = 0; k < n; k++) {
            int u = -1, best = INF;
            for (int i = 0; i < n; i++)
                if (!utilise[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }
            if (u == -1) break;

            utilise[u] = true;

            for (int v = 0; v < n; v++)
                if (mat[u][v] != INF) {
                    int nd = dist[u] + mat[u][v];
                    if (nd < dist[v]) {
                        dist[v] = nd;
                        precedent[v] = u;
                    }
                }
        }

        ArrayList<Integer> chemin = new ArrayList<>();
        int cur = t;
        while (cur != -1) {
            chemin.add(cur);
            cur = precedent[cur];
        }
        Collections.reverse(chemin);
        return chemin;
    }

    // ------------------------------------------------------
    // Ordonner les points selon leur distance au dépôt
    // ------------------------------------------------------
    static ArrayList<Integer> ordonnerSelonDistance(int depot, ArrayList<Integer> points, int[][] mat) {
        ArrayList<Integer> copie = new ArrayList<>(points);
        copie.sort((a, b) -> dijkstraDistance(depot, a, mat) - dijkstraDistance(depot, b, mat));
        return copie;
    }

    // ------------------------------------------------------
    // Découpage capacité (nouvelle logique)
    // ------------------------------------------------------
    static ArrayList<ArrayList<Integer>> decouperParCapacite(ArrayList<Integer> points, int[] quantites) {

        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        ArrayList<Integer> courant = new ArrayList<>();
        int charge = 0;

        for (int p : points) {
            int q = quantites[p];

            if (charge + q > CAPACITE && !courant.isEmpty()) {
                res.add(new ArrayList<>(courant));
                courant.clear();
                charge = 0;
            }

            courant.add(p);
            charge += q;
        }

        if (!courant.isEmpty())
            res.add(courant);

        return res;
    }

    // ------------------------------------------------------
    // Construire un itinéraire OPTIMISÉ (Dijkstra)
    // ------------------------------------------------------
    static ArrayList<Integer> construireItinOptimise(int depot, ArrayList<Integer> points, int[][] mat) {

        ArrayList<Integer> result = new ArrayList<>();
        int courant = depot;

        for (int p : points) {
            ArrayList<Integer> segment = dijkstraChemin(courant, p, mat);
            for (int x : segment) if (result.isEmpty() || x != result.get(result.size()-1))
                result.add(x);
            courant = p;
        }

        // Retour au dépôt
        ArrayList<Integer> segmentRetour = dijkstraChemin(courant, depot, mat);
        for (int x : segmentRetour)
            if (result.isEmpty() || x != result.get(result.size()-1))
                result.add(x);

        return result;
    }
}
