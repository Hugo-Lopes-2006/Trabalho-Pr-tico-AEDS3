import java.util.Scanner;

public class Objet {
    String id;
    String artists;
    String title;
    int popularity;

    public Objet(String id, String artists, String title, int popularity) {
        this.id = id;
        this.artists = artists;
        this.title = title;
        this.popularity = popularity;
    }

    public Objet parseObjet(String linha) {
        Scanner le = new Scanner(linha);
        le.useDelimiter(",");
        String id = le.next();
        String artists = le.next();
        String title = le.next();
        int popularity = le.nextInt();
        le.close();
        return new Objet(id, artists, title, popularity);
    }

    public Objet() {
        this.id = "";
        this.artists = "";
        this.title = "";
        this.popularity = 0;
    }

}
