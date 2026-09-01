import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

class LeitorCsv {
    public static void escrever(RandomAccessFile raf,Objeto objeto, int id)throws IOException {

        byte[] ba;//cria array de byte
        ba=objeto.toByteArray();//carrega o array de byte gerado pelo objeto
        raf.writeByte(0);//escreve lápide
        raf.writeInt(ba.length);//escreve o tamanho do array/registro
        raf.writeInt(id);//escreve id
        raf.write(ba);//escreve o registro

    }
    public static void lerCsv(String path) throws FileNotFoundException, IOException {
        File arquivoCsv = new File(path);
        Scanner sc = new Scanner(arquivoCsv);
        sc.nextLine();//primeira linha é apenas as descrições das colunas

        RandomAccessFile registros=new RandomAccessFile("spotify.bin","rw");

        registros.writeInt(0);//cabeçalho
        int id=0;

        while(sc.hasNext()){
            Objeto objeto = Objeto.parseObjeto(sc.nextLine(),id);//lê linha e cria objeto
            escrever(registros, objeto,id);//escreve objeto
            id++;
        }

        registros.seek(0);
        registros.writeInt(id-1);//atualiza cabeçalho pra o ultimo id colocado

        registros.close();
    }
}
