import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Insira qual caso deseja executar: ");
		Scanner numero_caso = new Scanner(System.in);
		int n = numero_caso.nextInt();
		System.out.println("\n");

		Montanha m = new Montanha(n);
		try {
			m.Inicia();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}