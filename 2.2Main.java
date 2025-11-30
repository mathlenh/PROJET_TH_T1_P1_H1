import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;
    static final int CAPACITY = 10; // capacité du camion

    // structure d’arête simple
    static class Edge {
        int u, v, w;
        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) throws Exception {

        // Lecture du fichier
        Scanner sc = new Scanner(new File("graphe.txt"));

        int n = sc.nextInt();
        String[] names = new String[n];
        int[] quantities = new int[n];

        // lecture des sommets + quantités
        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
            quantities[i] = sc.nextInt();
        }

        // lecture matrice des distances
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = sc.nextInt();
            }
        }

        // construire liste des arêtes du graphe
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (mat[i][j] != INF && mat[i][j] != 0) {
                    edges.add(new Edge(i, j, mat[i][j]));
                }
            }
        }

        // tri par poids (Kruskal)
        Collections.sort(edges, (a,b) -> a.w - b.w);

        // union-find pour Kruskal
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        ArrayList<Edge> mst = new ArrayList<>();

        // KRUSKAL
        for (Edge e : edges) {
            int ru = find(parent, e.u);
            int rv = find(parent, e.v);
            if (ru != rv) {
                mst.add(e);
                parent[ru] = rv;
            }
            if (mst.size() == n - 1) break;
        }

        System.out.println("Arbre couvrant minimum (Kruskal) :");
        for (Edge e : mst) {
            System.out.println(names[e.u] + " -- " + names[e.v] + " (" + e.w + ")");
        }

        // construire adj du MST
        ArrayList<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();

        for (Edge e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        // trouver index du dépôt D
        int depot = 0;
        for (int i = 0; i < n; i++) {
            if (names[i].equals("D")) depot = i;
        }

        // PARCOURS COMPLET (aller + retour)
        ArrayList<Integer> walk = new ArrayList<>();
        dfsWalk(depot, -1, adj, walk);

        System.out.println("\nParcours complet DFS (aller + retour) :");
        for (int i = 0; i < walk.size(); i++) {
            System.out.print(names[walk.get(i)]);
            if (i < walk.size() - 1) System.out.print(" → ");
        }

        // Ajouter retour final au dépôt
        walk.add(depot);

        System.out.println("\n\nAvec retour final :");
        for (int i = 0; i < walk.size(); i++) {
            System.out.print(names[walk.get(i)]);
            if (i < walk.size() - 1) System.out.print(" → ");
        }

        // SHORTCUTTING : ne garder qu’une fois chaque sommet
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] used = new boolean[n];

        for (int v : walk) {
            if (!used[v]) {
                tour.add(v);
                used[v] = true;
            }
        }
        // retour au dépôt
        tour.add(depot);

        System.out.println("\n\nTournée finale après shortcutting :");
        for (int i = 0; i < tour.size(); i++) {
            System.out.print(names[tour.get(i)]);
            if (i < tour.size() - 1) System.out.print(" → ");
        }

        // --- Préparer la liste des points SANS les D pour le découpage capacité ---
        ArrayList<Integer> tourPoints = new ArrayList<>();
        for (int v : tour) {
            if (v != depot) { // on enlève tous les D
                tourPoints.add(v);
            }
        }

        // --- Découpage en tournées (capacités) ---
        ArrayList<ArrayList<Integer>> realTours = splitByCapacity(tourPoints, quantities);

        System.out.println("\n\nDécoupage par capacité (C = " + CAPACITY + ") :");
        for (int i = 0; i < realTours.size(); i++) {
            System.out.print("T" + (i+1) + " : D → ");
            for (int v : realTours.get(i)) {
                System.out.print(names[v] + " → ");
            }
            System.out.println("D");
        }

        // --- Distance par tournée ---
        System.out.println("\nDistance de chaque tournée :");
        for (int i = 0; i < realTours.size(); i++) {
            ArrayList<Integer> t = realTours.get(i);
            int dist = 0;

            // D -> premier point
            dist += dijkstra(depot, t.get(0), mat);

            // entre les points
            for (int j = 0; j < t.size() - 1; j++) {
                dist += dijkstra(t.get(j), t.get(j+1), mat);
            }

            // dernier point -> D
            dist += dijkstra(t.get(t.size()-1), depot, mat);

            System.out.println("T" + (i+1) + " = " + dist);
        }
    }

    // union-find simple corrigé
    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            x = parent[x];
        }
        return x;
    }

    // DFS WALK : aller + retour dans le MST
    static void dfsWalk(int u, int parent, ArrayList<Integer>[] adj, ArrayList<Integer> walk) {
        walk.add(u);

        for (int v : adj[u]) {
            if (v != parent) {
                dfsWalk(v, u, adj, walk);
                walk.add(u);
            }
        }
    }

    // DIJKSTRA simple
    static int dijkstra(int start, int end, int[][] mat) {
        int n = mat.length;
        int[] dist = new int[n];
        boolean[] used = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            used[i] = false;
        }
        dist[start] = 0;

        for (int k = 0; k < n; k++) {
            int u = -1, best = INF;
            for (int i = 0; i < n; i++) {
                if (!used[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }
            }
            if (u == -1) break;
            used[u] = true;

            for (int v = 0; v < n; v++) {
                if (mat[u][v] != INF) {
                    if (dist[u] + mat[u][v] < dist[v]) {
                        dist[v] = dist[u] + mat[u][v];
                    }
                }
            }
        }

        return dist[end];
    }

    // Découpage en tournées selon capacité
    static ArrayList<ArrayList<Integer>> splitByCapacity(
            ArrayList<Integer> order, int[] quantities)
    {
        ArrayList<ArrayList<Integer>> tours = new ArrayList<>();
        ArrayList<Integer> current = new ArrayList<>();
        int load = 0;

        for (int v : order) {
            int q = quantities[v];

            if (load + q > CAPACITY && !current.isEmpty()) {
                tours.add(new ArrayList<>(current));
                current.clear();
                load = 0;
            }

            current.add(v);
            load += q;
        }

        if (!current.isEmpty()) {
            tours.add(current);
        }

        return tours;
    }
}
