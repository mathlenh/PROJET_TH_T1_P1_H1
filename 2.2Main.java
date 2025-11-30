import java.util.*;
import java.io.*;

public class Main {

    static final int INF = 999;
    static final int CAPACITY = 10;

    static class Edge {
        int u, v, w;
        Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(new File("graphe.txt"));

        int n = sc.nextInt();
        String[] names = new String[n];
        int[] quantities = new int[n];

        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
            quantities[i] = sc.nextInt();
        }

        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                mat[i][j] = sc.nextInt();

        //--------------------------------------------------
        // Construire arêtes du graphe
        //--------------------------------------------------
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                if (mat[i][j] != INF && mat[i][j] != 0)
                    edges.add(new Edge(i, j, mat[i][j]));

        Collections.sort(edges, (a,b)->a.w-b.w);

        //--------------------------------------------------
        // Kruskal
        //--------------------------------------------------
        int[] parent = new int[n];
        for (int i=0;i<n;i++) parent[i]=i;
        ArrayList<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            int ru = find(parent,e.u);
            int rv = find(parent,e.v);
            if (ru != rv) {
                mst.add(e);
                parent[ru] = rv;
            }
        }

        //--------------------------------------------------
        // Adj du MST
        //--------------------------------------------------
        ArrayList<Integer>[] adj = new ArrayList[n];
        for (int i=0;i<n;i++) adj[i] = new ArrayList<>();
        for (Edge e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        //--------------------------------------------------
        // Trouver le dépôt
        //--------------------------------------------------
        int depot = 0;
        for (int i = 0; i < n; i++)
            if (names[i].equals("D")) depot = i;

        //--------------------------------------------------
        // DFS WALK COMPLET pour l'arbre entier
        //--------------------------------------------------
        ArrayList<Integer> walk = new ArrayList<>();
        dfsWalk(depot, -1, adj, walk);
        walk.add(depot);

        //--------------------------------------------------
        // Shortcutting
        //--------------------------------------------------
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] used = new boolean[n];

        for (int v : walk)
            if (!used[v]) { tour.add(v); used[v] = true; }
        tour.add(depot);

        //--------------------------------------------------
        // Préparer la liste des points sans D
        //--------------------------------------------------
        ArrayList<Integer> points = new ArrayList<>();
        for (int v : tour)
            if (v != depot) points.add(v);

        //--------------------------------------------------
        // Découpage selon capacité
        //--------------------------------------------------
        ArrayList<ArrayList<Integer>> realTours = splitByCapacity(points, quantities);

        //--------------------------------------------------
        // AFFICHAGE + ITINÉRAIRE DÉTAILLÉ
        //--------------------------------------------------
        System.out.println("\nDécoupage détaillé (C = " + CAPACITY + ") : ");
        for (int i = 0; i < realTours.size(); i++) {
            ArrayList<Integer> t = realTours.get(i);

            // Calcul de l'itinéraire complet dans le MST
            ArrayList<Integer> detailed = buildDetailedMSTTour(depot, t, adj);

            System.out.print("T" + (i+1) + " : ");
            for (int k = 0; k < detailed.size(); k++) {
                System.out.print(names[detailed.get(k)]);
                if (k < detailed.size()-1) System.out.print(" → ");
            }
            System.out.println();
        }
    }

    // union-find
    static int find(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    // MARCHE COMPLÈTE dans l’arbre
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

        Arrays.fill(dist, INF);
        dist[start] = 0;

        for (int k=0; k<n; k++) {
            int u=-1, best=INF;
            for (int i=0;i<n;i++)
                if (!used[i] && dist[i]<best) { best=dist[i]; u=i; }
            if (u==-1) break;
            used[u]=true;

            for (int v=0; v<n; v++)
                if (mat[u][v] != INF)
                    dist[v] = Math.min(dist[v], dist[u] + mat[u][v]);
        }
        return dist[end];
    }

    // Découpage capacité
    static ArrayList<ArrayList<Integer>> splitByCapacity(
        ArrayList<Integer> order, int[] quantities)
    {
        ArrayList<ArrayList<Integer>> tours = new ArrayList<>();
        ArrayList<Integer> cur = new ArrayList<>();
        int load = 0;

        for (int v : order) {
            int q = quantities[v];
            if (load + q > CAPACITY && !cur.isEmpty()) {
                tours.add(new ArrayList<>(cur));
                cur.clear();
                load = 0;
            }
            cur.add(v);
            load += q;
        }

        if (!cur.isEmpty())
            tours.add(cur);

        return tours;
    }

    // Construit l’itinéraire COMPLET (aller+retour) pour une tournée T
    static ArrayList<Integer> buildDetailedMSTTour(
        int depot,
        ArrayList<Integer> tourPoints,
        ArrayList<Integer>[] adj)
    {
        ArrayList<Integer> result = new ArrayList<>();
        int current = depot;

        for (int target : tourPoints) {
            ArrayList<Integer> path = pathInMST(current, target, adj);
            for (int x : path) result.add(x);
            current = target;
        }

        // Retour au dépôt
        ArrayList<Integer> back = pathInMST(current, depot, adj);
        for (int x : back) result.add(x);

        return result;
    }

    // Trouve chemin simple entre 2 sommets dans un ARBRE (MST)
    static ArrayList<Integer> pathInMST(int start, int end, ArrayList<Integer>[] adj) {
        ArrayList<Integer> path = new ArrayList<>();
        boolean[] vis = new boolean[adj.length];
        dfsPath(start, end, -1, adj, vis, path);
        return path;
    }

    static boolean dfsPath(int u, int target, int parent,
                           ArrayList<Integer>[] adj, boolean[] vis, ArrayList<Integer> path)
    {
        path.add(u);
        if (u == target) return true;

        vis[u] = true;
        for (int v : adj[u]) {
            if (!vis[v] && v != parent) {
                if (dfsPath(v, target, u, adj, vis, path))
                    return true;
            }
        }

        path.remove(path.size()-1);
        return false;
    }
}
