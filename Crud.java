import java.io.FileNotFoundException;
import java.io.IOException;

class Crud {

    public void create() throws FileNotFoundException, IOException { //fazer a carga inicial com o arquivo csv

        LeitorCsv.lerCsv("spotify.csv");

    }

    public void read(int id) { //

    }

    public void update(int id) {

    }

    public void delete(int id) {

    }
}
