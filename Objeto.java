import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class Objeto {

    String track_id; //string de tamanho fixo
    String[] artists; //lista de valores com separador a definir
    String track_name; //string de tamanho variavel
    int popularity; //int ou float
    String date; //data de carga

    public Objeto(String track_id, String[] artists, String track_name, int popularity, String date) {
        this.track_id = track_id;
        this.artists = artists;
        this.track_name = track_name;
        this.popularity = popularity;
        this.date=date;
    }//construtor

    public static Objeto parseObjeto(String linha) {
        Scanner sc = new Scanner(linha);
        sc.useDelimiter(",");
        String track_id = sc.next(); //ler track_id


        String artistsLinha=sc.next();
        Scanner sc1=new Scanner(artistsLinha);
        sc1.useDelimiter(";");
        int tmp=0;
        while(sc1.hasNext()){
            tmp++;
            sc1.next();
        }
        sc1.close();
        //definir tamanho do array (lista de valores) de artists

        String[] artists=new String[tmp];
        Scanner sc2=new Scanner(artistsLinha);
        sc2.useDelimiter(";");
        for(int i=0;i<tmp;i++){
            artists[i]=sc1.next();
        }
        sc2.close();
        //preencher o array


        String track_name = sc.next(); //ler track_name
        int popularity = sc.nextInt(); //ler popularity
        sc.close();

        LocalDate dateNow = LocalDate.now();//ler a data no instante
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = dateNow.format(formatador);//formatar para string

        
        return new Objeto(track_id, artists, track_name, popularity,date);
    }//parse: recebe linha do arquivo, transforma em objeto

    //metodos get
    public String getTrack_id() {
        return track_id;
    }

    public String[] getArtists() {
        return artists;
    }

    public String getTrack_name() {
        return track_name;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getDate() {
        return date;
    }

}
