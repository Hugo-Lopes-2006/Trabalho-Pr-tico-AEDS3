import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class LeitorCsv {
    public void lerCsv(String path) throws FileNotFoundException, IOException {
        File arquivo = new File(path);
        Scanner sc = new Scanner(arquivo);
        sc.nextLine(); //primeira linha é lixo

        while(sc.hasNext()){
            Objeto objeto = Objeto.parseObjeto(sc.nextLine());
        }
        
    }
}
