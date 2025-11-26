import java.util.ArrayList;
import java.util.List;

public class Sommet {
    public final String nom;
    public final List<ArcSortant> arcsSortants;

    public Sommet(String nom) {
        this.nom = nom;
        this.arcsSortants = new ArrayList<>();
    }

    public void ajouterArc(Sommet dest, double poids) {
        arcsSortants.add(new ArcSortant(dest, poids));
    }

    @Override
    public String toString() {
        return nom;
    }
}
