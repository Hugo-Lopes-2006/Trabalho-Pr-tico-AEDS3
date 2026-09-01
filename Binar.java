import java.io.IOException;
import java.io.RandomAccessFile;

public class Binar {
    public static void escrever(Objet objeto)throws IOException {
        try (RandomAccessFile fita=new RandomAccessFile("Spotiy.bin", "rw")) {
            fita.writeUTF(objeto.id);
            fita.writeUTF(objeto.artists);
            fita.writeUTF(objeto.title);
            fita.writeInt(objeto.popularity);

        }
    }
}
