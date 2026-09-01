import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Selecione a opção desejada:");
        System.out.println("1. Create");
        System.out.println("2. Read");
        System.out.println("3. Update");
        System.out.println("4. Delete");
        System.out.println("5. Ordenar");
        System.out.println("6. Sair");
        Crud crud=new Crud();

        int opcao = sc.nextInt();
        int id;
        switch (opcao) {
            case 1 -> {
                crud.create();
            }
            case 2 -> {
                System.out.println("Qual o ID do registro?");
                id=sc.nextInt();
                crud.read(id);
            }
            case 3 -> {
                System.out.println("Qual o ID do registro?");
                id=sc.nextInt();
                crud.update(id);
            }
            case 4 -> {
                System.out.println("Qual o ID do registro?");
                id=sc.nextInt();
                crud.delete(id);
            }
            case 5 -> {
            }
            case 6 -> {
            }
            default -> {
            }
        }


    }
}
