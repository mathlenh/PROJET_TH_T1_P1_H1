import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;

    // Structure simple d'arête
    static class Edge {
        int u, v, w;
        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) throws Exception {

        // Lecture du fichier graphe.txt
        Scanner sc = new Scanner(new File("graphe.txt"));

        int n = sc.nextInt();
        String[] names = new String[n];
        int[] quantities = new int[n];

        // Lire les sommets + quantités
        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
            quantities[i] = sc.nextInt();
        }

        // Lire la matrice des distances
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = sc.nextInt();
            }
        }

        // Construire liste des arêtes
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (mat[i][j] != INF && mat[i][j] != 0) {
                    edges.add(new Edge(i, j, mat[i][j]));
                }
            }
        }

        // Trier les arêtes par poids
        Collections.sort(edges, (a,b) -> a.w - b.w);

        // Union-Find pour Kruskal
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        ArrayList<Edge> mst = new ArrayList<>();

        // --- Kruskal ---
        for (Edge e : edges) {
            int ru = find(parent, e.u);
            int rv = find(parent, e.v);
            if (ru != rv) {
                mst.add(e);
                parent[ru] = rv;
            }
            if (mst.size() == n-1) break;
        }

        System.out.println("Arbre couvrant minimum (Kruskal) :");
        for (Edge e : mst) {
            System.out.println(names[e.u] + " -- " + names[e.v] + " (" + e.w + ")");
        }

        // Construire l'adjacence du MST
        ArrayList<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();

        for (Edge e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        // Trouver le dépôt D
        int depot = 0;
        for (int i = 0; i < n; i++) {
            if (names[i].equals("D")) depot = i;
        }

        // DFS préfixe
        boolean[] vis = new boolean[n];
        ArrayList<Integer> order = new ArrayList<>();
        dfs(depot, adj, vis, order);

        System.out.println("\nParcours préfixe :");
        for (int idx : order) System.out.print(names[idx] + " ");
        System.out.println();

        // Shortcutting = ajouter retour à D
        order.add(depot);

        System.out.println("\nTournée finale :");
        for (int idx : order) System.out.print(names[idx] + " ");
        System.out.println();

        // --- Calcul de la distance totale AVEC DIJKSTRA ---
        int total = 0;
        for (int i = 0; i < order.size() - 1; i++) {
            int a = order.get(i);
            int b = order.get(i+1);

            int d = dijkstra(a, b, mat);
            total += d;
        }

        System.out.println("\nDistance totale = " + total);
    }

    // Trouver la racine (Union-Find simple)
    static int find(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    // DFS préfixe sur le MST
    static void dfs(int u, ArrayList<Integer>[] adj, boolean[] vis, ArrayList<Integer> order) {
        vis[u] = true;
        order.add(u);
        for (int v : adj[u]) {
            if (!vis[v]) dfs(v, adj, vis, order);
        }
    }

    // --- Dijkstra simple pour trouver distance a→b ---
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
            int u = -1;
            int best = INF;
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
                    dist[v] = Math.min(dist[v], dist[u] + mat[u][v]);
                }
            }
        }

        return dist[end];
    }
}
