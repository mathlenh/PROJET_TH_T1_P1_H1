import java.util.*;

public class Tournees {

    // Triée selon la distance au dépôt
    public static ArrayList<Integer> ordonnerSelonDepot(
            Graphe g, ArrayList<Integer> points)
    {
        ArrayList<Integer> res = new ArrayList<>(points);

        res.sort((a, b) ->
                Chemins.dijkstraDistance(g.getIndexDepot(), a, g.getMat())
                        - Chemins.dijkstraDistance(g.getIndexDepot(), b, g.getMat())
        );

        return res;
    }

    // Découpage selon capacité
    public static ArrayList<ArrayList<Integer>> decouper(
            ArrayList<Integer> points, Graphe g)
    {
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();

        ArrayList<Integer> courant = new ArrayList<>();
        int charge = 0;

        for (int p : points) {

            int q = g.getQuantite(p);

            if (charge + q > Main.CAPACITE && !courant.isEmpty()) {
                res.add(courant);
                courant = new ArrayList<>();
                charge = 0;
            }

            courant.add(p);
            charge += q;
        }

        if (!courant.isEmpty())
            res.add(courant);

        return res;
    }

    // Construction itinéraire optimisé
    public static ArrayList<Integer> itinOptimise(
            int depot, ArrayList<Integer> points, int[][] mat)
    {
        ArrayList<Integer> res = new ArrayList<>();
        int courant = depot;

        for (int p : points) {
            ArrayList<Integer> segment = Chemins.dijkstraChemin(courant, p, mat);

            for (int x : segment)
                if (res.isEmpty() || x != res.get(res.size()-1))
                    res.add(x);

            courant = p;
        }

        ArrayList<Integer> retour =
                Chemins.dijkstraChemin(courant, depot, mat);

        for (int x : retour)
            if (res.isEmpty() || x != res.get(res.size()-1))
                res.add(x);

        return res;
    }
}
