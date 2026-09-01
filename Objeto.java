import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


class Objeto {

    private String track_id; //string de tamanho fixo
    private String[] artists; //lista de valores com separador a definir
    private String track_name; //string de tamanho variavel
    private int popularity; //int ou float
    private String date; //data de carga

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


        String tmpString1=sc.next();
        String artistsLinha="";
        if(tmpString1.charAt(0)=='"' && tmpString1.charAt(tmpString1.length()-1)!='"'){//se possuir ',' dentro do campo
            while(tmpString1.charAt(tmpString1.length()-1)!='"'){//até a string terminar com "
                tmpString1+=','+sc.next();//junta a string com a próxima
            }
            for(int i=1;i<tmpString1.length()-1;i++){
                artistsLinha+=tmpString1.charAt(i);//corta as aspas do começo e fim
            }
        }else{//caso contrario, a string nao possui virgulas dentro do campo e ja esta correta
            artistsLinha=tmpString1;
        }
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
            artists[i]=sc2.next();
        }
        sc2.close();
        //preencher o array


        String tmpString = sc.next(); //ler track_name
        String track_name="";
        if(tmpString.charAt(0)=='"' && tmpString.charAt(tmpString.length()-1)!='"' ){//se possuir ',' dentro do campo
            while(tmpString.charAt(tmpString.length()-1)!='"'){//até a string terminar com "
                tmpString+=','+sc.next();//junta a string com a próxima
            }
            for(int i=1;i<tmpString.length()-1;i++){
                track_name+=tmpString.charAt(i);//corta as aspas do começo e fim
            }
        }else{//caso contrario, a string nao possui virgulas dentro do campo e ja esta correta
            track_name=tmpString;
        }

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

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(track_id);//escreve track_id

        int tmp=artists.length;//define quantidade de nomes a serem escritos
        dos.writeInt(tmp);//escreve o total de nomes
        for(int i=0;i<tmp;i++){//itera por todos os nomes
            dos.writeUTF(artists[i]);//escreve o nome da posição i
        }

        dos.writeUTF(track_name);//escreve track_name
        dos.writeInt(popularity);//escreve_popoularity
        dos.writeUTF(date);//escreve date

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis=new DataInputStream(bais);

        track_id=dis.readUTF();

        int tmp=dis.readInt();
        for(int i=0;i<tmp;i++){
            artists[i]=dis.readUTF();
        }

        track_name=dis.readUTF();
        popularity=dis.readInt();
        date=dis.readUTF();
    }

}
