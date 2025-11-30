import java.util.*;
import java.io.*;

public class Graphe {

    public static final int INF = 999;

    private int n;                    // nombre de sommets
    private String[] noms;            // noms des sommets
    private int[] quantites;          // quantités à ramasser
    private int[][] mat;              // matrice des distances
    private int indexDepot;           // index de D
    private ArrayList<Arete> aretes;  // liste des arêtes valides

    public Graphe(String fichier) throws Exception {

        Scanner sc = new Scanner(new File(fichier));

        n = sc.nextInt();
        noms = new String[n];
        quantites = new int[n];
        mat = new int[n][n];

        for (int i = 0; i < n; i++) {
            noms[i] = sc.next();
            quantites[i] = sc.nextInt();
            if (noms[i].equals("D")) indexDepot = i;
        }

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                mat[i][j] = sc.nextInt();

        aretes = new ArrayList<>();

        // Générer la liste d’arêtes du graphe
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                if (mat[i][j] != INF && mat[i][j] != 0)
                    aretes.add(new Arete(i, j, mat[i][j]));
    }

    public int getN() { return n; }
    public String getNom(int i) { return noms[i]; }
    public int getQuantite(int i) { return quantites[i]; }
    public int[][] getMat() { return mat; }
    public int getIndexDepot() { return indexDepot; }
    public ArrayList<Arete> getAretes() { return aretes; }
}
