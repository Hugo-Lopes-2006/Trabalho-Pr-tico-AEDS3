import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class LeitorCsv {
    public void lerCsv(String path) throws FileNotFoundException, IOException {
        File arquivo = new File(path);
        Scanner le = new Scanner(arquivo);
        if(le.hasNextLine()){
            le.nextLine();
        }
        Objet objeto = new Objet();
        objeto = objeto.parseObjet(le.nextLine());
        Binar salvando= new Binar();
        salvando.escrever(objeto);
        le.close();
    }
}
