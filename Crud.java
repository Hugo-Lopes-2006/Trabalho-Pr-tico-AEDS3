import java.util.Scanner;

public class Crud {

    public void create() {
        Scanner le = new Scanner(System.in);
        System.out.println("Qual o ID");
        String id = le.nextLine();
        System.out.println("Qual o nome do artista");
        String artista = le.nextLine();
        System.out.println("Qual o nome da música");
        String musica = le.nextLine();
        System.out.println("Qual a Popularidade");
        int popularidade = le.nextInt();
        Objet obj= new Objet(id, artista, musica, popularidade);

        le.close();
    }

    public void read() {

    }

    public void update() {

    }

    public void delete() {

    }
}
