public class Main {
    public static void main(String[] args) {

        Graphe g = new Graphe();

        // Création des sommets (ex : centre de traitement + intersections)
        Sommet CT = g.ajouterSommet("Centre");
        Sommet A = g.ajouterSommet("A");
        Sommet B = g.ajouterSommet("B");
        Sommet C = g.ajouterSommet("C");
        Sommet Maison = g.ajouterSommet("Maison");

        // Ajout des routes (arcs pondérés)
        CT.ajouterArc(A, 4);
        CT.ajouterArc(B, 2);
        A.ajouterArc(C, 3);
        B.ajouterArc(A, 1);
        B.ajouterArc(C, 4);
        C.ajouterArc(Maison, 5);

        // Lancement de Dijkstra
        Dijkstra.calculer(g, CT);

        // Chemin vers la maison
        System.out.println("Distance minimale : " +
                Dijkstra.distances.get(Maison) + " km");

        System.out.println("Chemin : " +
                Dijkstra.reconstruireChemin(Maison));
    }
}
