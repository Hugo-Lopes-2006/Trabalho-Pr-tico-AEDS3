import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, Exception {
        Scanner sc = new Scanner(System.in);
        int opcao = 0;
        while(opcao != 6){
            System.out.println("Selecione a opção desejada:");
            System.out.println("1. Carregar");
            System.out.println("2. Read");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Ordenar");
            System.out.println("6. Sair");
            Crud crud=new Crud();

            opcao = sc.nextInt();
            int id;
            switch (opcao) {
                case 1 -> {
                    LeitorCsv.lerCsv("spotify.csv");
                }
                case 2 -> {
                    System.out.println("Qual o ID da musica?");
                    id=sc.nextInt();
                    crud.read(id);
                    break;
                }
                case 3 -> {
                    System.out.println("Qual o ID da musica?");
                    id=sc.nextInt();

                    System.out.println("Qual o Track ID da musica?");
                    String track_id=sc.next();

                    System.out.println("Quantos artistas?");
                    int tmp=sc.nextInt();
                    String[] artists=new String[tmp];
                    sc.nextLine();
                    for(int i=0;i<tmp;i++){
                        System.out.print("Nome do artista: ");
                        artists[i]=sc.nextLine();
                        System.out.println();
                    }

                    System.out.println("Qual o nome da musica?");
                    String track_name=sc.nextLine();

                    System.out.println("Qual a popularidade da musica?");
                    int popularity=sc.nextInt();

                    System.out.println("Que dia e hoje? (formato: dd-MM-yyyy) ");
                    String date=sc.next();

                    Objeto objeto=new Objeto(track_id, artists, track_name, popularity, date, id);

                    crud.update(objeto.getId(),objeto);
                }
                case 4 -> {
                    System.out.println("Qual o ID da musica?");
                    id=sc.nextInt();
                    crud.delete(id);
                }
                case 5 -> {
                    int numeroCaminhos,numeroRegistros;
                    System.out.println("Digite o numero de caminhos: ");
                    numeroCaminhos=sc.nextInt();
                    System.out.println("Digite o numero de registros por ordenacao em memoria primaria: ");
                    numeroRegistros=sc.nextInt();
                    System.out.println("Iniciando a ordenação externa...");
                    Ordenacao.ordenacaoExterna(numeroCaminhos, numeroRegistros);
                    System.out.println("Ordenação concluída com sucesso!");
                }
                case 6 -> {
                }
                default -> {
                }
            }
        }
        try {
            Conversor.binParaTxt("spotify.bin", "saida_original.txt");
            Conversor.binParaTxt("spotify_ordenado.bin", "saida_ordenada.txt");
            System.out.println("Conversão para TXT concluída com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao converter arquivo para TXT:");
            e.printStackTrace();
        }
        sc.close();
    }
}
