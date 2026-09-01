import java.io.IOException;
import java.io.RandomAccessFile;

public class Binar {
    public static void escrever(Objeto objeto)throws IOException {
        try (RandomAccessFile fita=new RandomAccessFile("Spotiy.bin", "rw")) {
            fita.writeUTF(objeto.track_id);
            fita.writeUTF(objeto.artists);
            fita.writeUTF(objeto.track_name);
            fita.writeInt(objeto.popularity);

        }
    }
}
