import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graphe {

    private final Map<String, Sommet> sommets;

    public Graphe() {
        this.sommets = new HashMap<>();
    }

    /**
     * Ajoute un sommet au graphe s’il n’existe pas encore.
     * @param nom nom du sommet
     * @return le sommet créé ou déjà existant
     */
    public Sommet ajouterSommet(String nom) {
        // Si le sommet existe déjà, on le retourne
        if (sommets.containsKey(nom)) {
            return sommets.get(nom);
        }

        Sommet s = new Sommet(nom);
        sommets.put(nom, s);
        return s;
    }

    /**
     * Retourne un sommet par son nom.
     */
    public Sommet getSommet(String nom) {
        return sommets.get(nom);
    }

    /**
     * Getter indispensable : permet à Dijkstra d'accéder
     * à la collection des sommets sans violer l’encapsulation.
     */
    public Collection<Sommet> getSommets() {
        return sommets.values();
    }
}
