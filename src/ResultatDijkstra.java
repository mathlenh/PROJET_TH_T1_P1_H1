import java.util.List;

public class ResultatDijkstra {
    int distance;
    List<String> chemin;

    public ResultatDijkstra(int distance, List<String> chemin) {
        this.distance = distance;
        this.chemin = chemin;
    }
}