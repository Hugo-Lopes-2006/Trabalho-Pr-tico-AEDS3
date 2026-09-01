import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

class LeitorCsv {
    public static void escrever(RandomAccessFile raf,Objeto objeto)throws IOException {

        byte[] ba;
        ba=objeto.toByteArray();
        raf.writeInt(ba.length);
        raf.write(ba);

    }
    public static void lerCsv(String path) throws FileNotFoundException, IOException {
        File arquivoCsv = new File(path);
        Scanner sc = new Scanner(arquivoCsv);
        sc.nextLine();//primeira linha é apenas as descrições das colunas

        RandomAccessFile registros=new RandomAccessFile("spotify.bin","rw");

        while(sc.hasNext()){
            Objeto objeto = Objeto.parseObjeto(sc.nextLine());
            escrever(registros, objeto);
        }

        registros.close();
    }
}
