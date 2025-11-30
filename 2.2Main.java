import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;

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

        // tri par poids
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

        System.out.println("\nParcours complet DFS (aller + retour dans le MST) :");
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

        // SHORTCUTTING : on garde chaque sommet la première fois
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] used = new boolean[n];

        for (int v : walk) {
            if (!used[v]) {
                tour.add(v);
                used[v] = true;
            }
        }
        // et on revient au dépôt
        tour.add(depot);

        System.out.println("\n\nTournée finale après shortcutting :");
        for (int i = 0; i < tour.size(); i++) {
            System.out.print(names[tour.get(i)]);
            if (i < tour.size() - 1) System.out.print(" → ");
        }

        // calcul distance totale AVEC DIJKSTRA
        int total = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int a = tour.get(i);
            int b = tour.get(i+1);
            int d = dijkstra(a, b, mat);
            total += d;
        }

        System.out.println("\n\nDistance totale = " + total);
    }

    // union-find simple
    static int find(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    // DFS WALK : aller + retour (comme dans ton raisonnement manuel)
    static void dfsWalk(int u, int parent, ArrayList<Integer>[] adj, ArrayList<Integer> walk) {
        walk.add(u); // on entre dans le sommet

        for (int v : adj[u]) {
            if (v != parent) {
                dfsWalk(v, u, adj, walk); // descente
                walk.add(u); // retour vers le parent
            }
        }
    }

    // DIJKSTRA simple pour trouver la distance réelle
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
}
