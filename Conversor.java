import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class Conversor {

    public static void binParaTxt(String arquivoBin, String arquivoTxt) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(arquivoBin, "r");
        PrintWriter pw = new PrintWriter(new FileWriter(arquivoTxt));

        pw.println("Ultimo ID (Cabecalho): " + raf.readInt());
        pw.println("=========================================");

        while (raf.getFilePointer() < raf.length()) {
            byte lapide = raf.readByte();
            int tamTotal = raf.readInt();
            byte[] ba = new byte[tamTotal];
            raf.read(ba);

            if (lapide == 0) {
                Objeto obj = new Objeto();
                obj.fromByteArray(ba);

                pw.println("ID: " + obj.getId());
                pw.println("Track ID: " + obj.getTrack_id());
                pw.println("Nome: " + obj.getTrack_name());
                pw.println("Popularidade: " + obj.getPopularity());
                pw.println("Data: " + obj.getDate());

                pw.print("Artistas: ");
                for (String art : obj.getArtists()) {
                    pw.print(art + " | ");
                }
                pw.println("\n-----------------------------------------");
            }
        }

        pw.close();
        raf.close();
    }
}
