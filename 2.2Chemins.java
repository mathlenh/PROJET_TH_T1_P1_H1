import java.util.*;

public class Chemins {

    // DFS complet (aller + retour)
    public static ArrayList<Integer> dfsComplet(int u, int parent, ArrayList<Integer>[] adj) {
        ArrayList<Integer> marche = new ArrayList<>();
        dfs(u, parent, adj, marche);
        marche.add(u);
        return marche;
    }

    private static void dfs(int u, int parent, ArrayList<Integer>[] adj, ArrayList<Integer> marche) {
        marche.add(u);
        for (int v : adj[u])
            if (v != parent) {
                dfs(v, u, adj, marche);
                marche.add(u);
            }
    }

    // Shortcutting
    public static ArrayList<Integer> shortcutting(ArrayList<Integer> marche, int depot) {
        boolean[] vu = new boolean[marche.size()];
        ArrayList<Integer> res = new ArrayList<>();

        for (int x : marche)
            if (!res.contains(x))
                res.add(x);

        res.add(depot);
        return res;
    }

    // Dijkstra distance
    public static int dijkstraDistance(int s, int t, int[][] mat) {

        int n = mat.length;
        int[] dist = new int[n];
        boolean[] used = new boolean[n];

        Arrays.fill(dist, Graphe.INF);
        dist[s] = 0;

        for (int k = 0; k < n; k++) {

            int u = -1, best = Graphe.INF;

            for (int i = 0; i < n; i++)
                if (!used[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }

            if (u == -1) break;
            used[u] = true;

            for (int v = 0; v < n; v++)
                if (mat[u][v] != Graphe.INF)
                    dist[v] = Math.min(dist[v], dist[u] + mat[u][v]);
        }

        return dist[t];
    }

    // Dijkstra chemin complet
    public static ArrayList<Integer> dijkstraChemin(int s, int t, int[][] mat) {

        int n = mat.length;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] used = new boolean[n];

        Arrays.fill(dist, Graphe.INF);
        Arrays.fill(prev, -1);

        dist[s] = 0;

        for (int k = 0; k < n; k++) {

            int u = -1, best = Graphe.INF;

            for (int i = 0; i < n; i++)
                if (!used[i] && dist[i] < best) {
                    best = dist[i];
                    u = i;
                }

            if (u == -1) break;
            used[u] = true;

            for (int v = 0; v < n; v++)
                if (mat[u][v] != Graphe.INF) {

                    int nd = dist[u] + mat[u][v];

                    if (nd < dist[v]) {
                        dist[v] = nd;
                        prev[v] = u;
                    }
                }
        }

        ArrayList<Integer> path = new ArrayList<>();
        int cur = t;

        while (cur != -1) {
            path.add(cur);
            cur = prev[cur];
        }

        Collections.reverse(path);
        return path;
    }
}
