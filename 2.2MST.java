import java.util.*;

public class MST {

    private ArrayList<Arete> mst;
    private ArrayList<Integer>[] adj;

    public MST(Graphe g) {

        ArrayList<Arete> aretes = new ArrayList<>(g.getAretes());
        aretes.sort((a, b) -> a.w - b.w);

        mst = new ArrayList<>();

        int n = g.getN();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        for (Arete a : aretes) {
            int ru = find(parent, a.u);
            int rv = find(parent, a.v);
            if (ru != rv) {
                mst.add(a);
                parent[ru] = rv;
            }
        }

        // Construire la liste d’adjacence
        adj = new ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new ArrayList<>();

        for (Arete a : mst) {
            adj[a.u].add(a.v);
            adj[a.v].add(a.u);
        }
    }

    private int find(int[] parent, int x) {
        while (parent[x] != x)
            x = parent[x];
        return x;
    }

    public ArrayList<Integer>[] getAdj() {
        return adj;
    }

    public ArrayList<Arete> getMST() {
        return mst;
    }
}
