import java.io.*;
import java.util.*;

/**
 * Pipeline Thème 2 - Approche 2 :
 * - Lecture d'un graphe pondéré (non orienté) depuis un fichier texte
 * - Construction du graphe
 * - Calcul d'un arbre couvrant minimum (MST) avec Kruskal
 * - Parcours préfixe (DFS) du MST en partant de D
 * - Shortcutting : tournée = ordre DFS + retour à D
 * - Calcul de la distance totale de la tournée en utilisant les plus courts chemins
 */
public class Approche2KruskalDFS {

    // 999 représente l'absence d'arête
    private static final int INF = 999;

    // --- Structures de données ---

    static class Vertex {
        String name;
        int quantity;

        Vertex(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    static class Edge implements Comparable<Edge> {
        int u;       // index sommet 1
        int v;       // index sommet 2
        int weight;  // poids

        Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return "(" + u + "," + v + ")=" + weight;
        }
    }

    // Union-Find pour Kruskal
    static class DSU {
        int[] parent;
        int[] rank;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        boolean union(int x, int y) {
            int rx = find(x);
            int ry = find(y);
            if (rx == ry) return false;
            if (rank[rx] < rank[ry]) {
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rank[rx]++;
            }
            return true;
        }
    }

    static class Graph {
        int n;                     // nombre de sommets
        Vertex[] vertices;         // sommets + quantités
        int[][] dist;              // matrice des distances (poids directs, 999 = pas d'arête)
        List<Edge> edges;          // arêtes du graphe (u < v, weight < INF)

        Graph(int n) {
            this.n = n;
            this.vertices = new Vertex[n];
            this.dist = new int[n][n];
            this.edges = new ArrayList<>();
        }
    }

    // --- Lecture du fichier texte ---

    public static Graph readGraphFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            if (line == null) throw new IOException("Fichier vide");
            line = line.trim();
            int n = Integer.parseInt(line);

            Graph g = new Graph(n);

            // Lecture des sommets
            for (int i = 0; i < n; i++) {
                line = br.readLine();
                while (line != null && line.trim().isEmpty()) {
                    line = br.readLine(); // sauter les lignes vides éventuelles
                }
                if (line == null) throw new IOException("Format invalide: pas assez de sommets");
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 2) {
                    throw new IOException("Ligne sommet invalide: " + line);
                }
                String name = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                g.vertices[i] = new Vertex(name, quantity);
            }

            // Lecture de la matrice
            int row = 0;
            while (row < n) {
                line = br.readLine();
                if (line == null) {
                    throw new IOException("Format invalide: matrice incomplète");
                }
                line = line.trim();
                if (line.isEmpty()) continue; // ignorer lignes vides
                String[] parts = line.split("\\s+");
                if (parts.length < n) {
                    throw new IOException("Ligne de matrice trop courte: " + line);
                }
                for (int j = 0; j < n; j++) {
                    int val = Integer.parseInt(parts[j]);
                    // On normalise : INF = 999 ou plus
                    if (val >= INF) val = INF;
                    g.dist[row][j] = val;
                }
                row++;
            }

            // Construire la liste d'arêtes (graphe non orienté)
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int w = g.dist[i][j];
                    if (w > 0 && w < INF) { // il y a une arête
                        g.edges.add(new Edge(i, j, w));
                    }
                }
            }

            return g;
        }
    }

    // --- Kruskal : MST ---

    public static List<Edge> kruskalMST(Graph g) {
        List<Edge> edges = new ArrayList<>(g.edges);
        Collections.sort(edges); // tri par poids croissant

        DSU dsu = new DSU(g.n);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            if (dsu.union(e.u, e.v)) {
                mst.add(e);
                if (mst.size() == g.n - 1) break;
            }
        }

        if (mst.size() != g.n - 1) {
            System.out.println("⚠ Le graphe n'est pas connexe : MST incomplet.");
        }
        return mst;
    }

    // --- Construction de l'adjacence du MST pour DFS ---

    @SuppressWarnings("unchecked")
    public static List<Integer>[] buildMSTAdjacency(int n, List<Edge> mst) {
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
        for (Edge e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }
        return adj;
    }

    // --- DFS préfixe sur le MST ---

    public static void dfsPreorder(int u, int parent, List<Integer>[] adj, boolean[] visited, List<Integer> order, Graph g) {
        visited[u] = true;
        order.add(u);

        // Pour que l'ordre soit stable et "logique", on trie les voisins par nom de sommet
        List<Integer> neighbors = new ArrayList<>(adj[u]);
        neighbors.sort(Comparator.comparing(v -> g.vertices[v].name));

        for (int v : neighbors) {
            if (!visited[v]) {
                dfsPreorder(v, u, adj, visited, order, g);
            }
        }
    }

    // --- Floyd-Warshall pour avoir tous les plus courts chemins ---

    public static int[][] floydWarshall(Graph g) {
        int n = g.n;
        int[][] d = new int[n][n];

        // Initialisation
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = g.dist[i][j];
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (d[i][k] >= INF) continue;
                for (int j = 0; j < n; j++) {
                    if (d[k][j] >= INF) continue;
                    int viaK = d[i][k] + d[k][j];
                    if (viaK < d[i][j]) {
                        d[i][j] = viaK;
                    }
                }
            }
        }

        return d;
    }

    // --- Trouver l'index du dépôt D ---

    public static int findDepotIndex(Graph g) {
        for (int i = 0; i < g.n; i++) {
            if (g.vertices[i].name.equalsIgnoreCase("D")) {
                return i;
            }
        }
        // Si pas trouvé, on prend 0 par défaut
        return 0;
    }

    // --- Programme principal ---

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage : java Approche2KruskalDFS <fichier_graphe.txt>");
            return;
        }

        String filename = args[0];

        try {
            Graph g = readGraphFromFile(filename);
            System.out.println("Graphe chargé. Sommets :");
            for (int i = 0; i < g.n; i++) {
                System.out.println("  " + i + " : " + g.vertices[i].name +
                                   " (quantité=" + g.vertices[i].quantity + ")");
            }

            // 1) MST avec Kruskal
            List<Edge> mst = kruskalMST(g);
            System.out.println("\nArbre couvrant minimum (Kruskal) :");
            int mstWeight = 0;
            for (Edge e : mst) {
                System.out.println("  " + g.vertices[e.u].name + " -- " +
                                   g.vertices[e.v].name + "  (w=" + e.weight + ")");
                mstWeight += e.weight;
            }
            System.out.println("Poids total du MST = " + mstWeight);

            // 2) Parcours préfixe (DFS) du MST en partant du dépôt D
            int depot = findDepotIndex(g);
            List<Integer>[] adjMST = buildMSTAdjacency(g.n, mst);
            boolean[] visited = new boolean[g.n];
            List<Integer> preorder = new ArrayList<>();
            dfsPreorder(depot, -1, adjMST, visited, preorder, g);

            System.out.println("\nParcours préfixe du MST à partir de D :");
            for (int idx : preorder) {
                System.out.print(g.vertices[idx].name + " ");
            }
            System.out.println();

            // 3) Shortcutting : tournée = ordre DFS + retour à D
            List<Integer> tour = new ArrayList<>(preorder);
            if (tour.get(0) != depot) {
                // normalment le dépôt est d'abord, mais sécurité
                tour.add(0, depot);
            }
            // retour au dépôt à la fin
            tour.add(depot);

            System.out.println("\nTournée après shortcutting (ordre des sommets) :");
            for (int idx : tour) {
                System.out.print(g.vertices[idx].name + " ");
            }
            System.out.println();

            // 4) Calcul des plus courts chemins sur le graphe original
            int[][] allPairs = floydWarshall(g);

            // 5) Calcul de la distance totale de la tournée
            int totalDistance = 0;
            boolean ok = true;
            for (int i = 0; i < tour.size() - 1; i++) {
                int a = tour.get(i);
                int b = tour.get(i + 1);
                int d = allPairs[a][b];
                if (d >= INF) {
                    System.out.println("⚠ Aucun chemin entre " +
                        g.vertices[a].name + " et " + g.vertices[b].name);
                    ok = false;
                    break;
                }
                totalDistance += d;
            }

            if (ok) {
                System.out.println("\nDistance totale de la tournée = " + totalDistance);
            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
