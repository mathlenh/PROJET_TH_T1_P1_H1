import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;

    static class Edge {
        int u, v, w;
        Edge(int u, int v, int w) {
            this.u = u; this.v = v; this.w = w;
        }
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(new File("graphe.txt"));

        int n = sc.nextInt();             // nombre de sommets
        String[] names = new String[n];   // noms des sommets
        int[] quant = new int[n];         // quantités

        // Lire sommets + quantités
        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
            quant[i] = sc.nextInt();
        }

        // Lire matrice
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = sc.nextInt();
            }
        }

        // Construire la liste des arêtes
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (mat[i][j] != INF && mat[i][j] != 0) {
                    edges.add(new Edge(i, j, mat[i][j]));
                }
            }
        }

        // Trier les arêtes selon poids
        Collections.sort(edges, (a,b)->a.w - b.w);

        // --- Kruskal ---
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        ArrayList<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            int ru = find(parent, e.u);
            int rv = find(parent, e.v);
            if (ru != rv) {
                mst.add(e);
                parent[ru] = rv;
            }
            if (mst.size() == n-1) break;
        }

        System.out.println("Arbre couvrant minimum :");
        for (Edge e : mst) {
            System.out.println(names[e.u] + " -- " + names[e.v] + " ("+e.w+")");
        }

        // Construire l’adjacence du MST
        ArrayList<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (Edge e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        // Trouver l’index de D
        int depot = 0;
        for (int i = 0; i<n; i++) {
            if (names[i].equals("D")) depot = i;
        }

        // DFS préfixe
        boolean[] vis = new boolean[n];
        ArrayList<Integer> order = new ArrayList<>();
        dfs(depot, adj, vis, order);

        System.out.println("\nParcours préfixe :");
        for (int idx : order) System.out.print(names[idx] + " ");
        System.out.println();

        // shortcutting = retirer les sommets déjà vus → ici DFS les visite déjà 1 fois
        // donc juste retour au dépôt :
        order.add(depot);

        System.out.println("\nTournée finale :");
        for (int idx : order) System.out.print(names[idx] + " ");
        System.out.println();

        // Calcul distance totale
        int total = 0;
        for (int i = 0; i < order.size()-1; i++) {
            int a = order.get(i);
            int b = order.get(i+1);

            if (mat[a][b] == INF) {
                System.out.println("Pas de route directe entre " + names[a] + " et " + names[b]);
                return;
            }
            total += mat[a][b];
        }

        System.out.println("\nDistance totale = " + total);
    }

    // Trouver racine Union-Find
    static int find(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    // DFS préfixe
    static void dfs(int u, ArrayList<Integer>[] adj, boolean[] vis, ArrayList<Integer> order) {
        vis[u] = true;
        order.add(u);
        for (int v : adj[u]) {
            if (!vis[v]) dfs(v, adj, vis, order);
        }
    }
}
