import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class Montanha {

	// Condições
	private int n_passageiros;
	private int n_carrinhos;
	
	private int fila = 0;
	
	private int[] passageiros_no_carro;
	private boolean[] carrinho_disponivel;
	
	private int prox_carrinho_embarque = 0;
	private int prox_carrinho_desembarque = 0;
	
	private long tempo_minimo = 10;
	private long tempo_maximo = 0;
	private long tempo_total = 0;
	
	private long tempo_de_espera;
	
	private long inicio_dia;
	private long fim_dia;
	private long duracao_dia;
	
	private int cont_carrinhos;
	private int cont_passageiros;
	private int[] voltas;
	
	// Variáveis de condição para sincronizar os processos
	private ReentrantLock controle;         // lock que funciona como monitor (garantindo a exlusão mútua)
	private Condition fila_embarque;        // passageiros esperando na fila de embarque
	private Condition carrinho_embarque;    // carrinho esperando o embarque dos passageiros
	private Condition no_carrinho;          // passageiros no carrinho
	private Condition fila_desembarque;     // passageiros na fila do desembarque
	private Condition carrinho_desembarque; // o carrinho espera os passageiros desembarcarem

	// Construtor
	public Montanha(int caso) {
		if (caso == 1) {
			n_passageiros = 52;
			n_carrinhos = caso;
		} else if (caso == 2) {
			n_passageiros = 92;
			n_carrinhos = caso;
		} else if (caso == 3) {
			n_passageiros = 148;
			n_carrinhos = caso;
		}
		
		cont_carrinhos = n_carrinhos;
		cont_passageiros = n_passageiros;

		// Configuração do lock com os processos
		controle = new ReentrantLock();
		fila_embarque = controle.newCondition();
		carrinho_embarque = controle.newCondition();
		no_carrinho = controle.newCondition();
		fila_desembarque = controle.newCondition();
		carrinho_desembarque = controle.newCondition();
		

		passageiros_no_carro = new int[n_carrinhos];
		carrinho_disponivel = new boolean[n_carrinhos];
		voltas = new int[n_carrinhos];
	}
	
	// Criação dos carrinhos e dos passageiros
	public void CriaCarrinhos() {

		int i;
		for (i = 0; i < n_carrinhos; i++) {
			Thread c = new Thread(new Carrinho(i, this));
			System.out.println("Carrinho " + (i + 1) + " criado");
			c.start();
		}
		System.out.println("\n");
	}
	
	public void CriaPassageiros() throws InterruptedException {

		// Randomiza o tempo de 1 a 3 segundos e a criação dos
		// passageiros ocorre dentro desse intervalo
		Random tempo = new Random();
		float segundos;
	
		int i;
		for (i = 0; i < n_passageiros; i++) {
			segundos = (tempo.nextFloat() * 2) + 1;
			Thread.sleep((long)(segundos * 1000));
			Thread p = new Thread(new Passageiro((i + 1), this));
			System.out.println("Passageiro " + (i + 1) + " chegou na fila");
			p.start();
		}
	}
	
	// Embarque do carrinho
	public boolean LiberaEmbarque(int car_id) {
		controle.lock(); // Permite que apenas 1 carrinho acesse o método por vez
		
		// Verifica se ainda há passageiros para o embarque
		if(cont_passageiros == 0) {
			cont_carrinhos--;
			System.out.println("O carrinho " + (car_id + 1) + " parou\n");
			controle.unlock();
			return false; // condição de parada do programa
		} else {
			cont_passageiros -= 4;
		}
		
		carrinho_disponivel[car_id] = true;
		
		// Acorda os passageiros da fila pra eles embarcarem
		if (fila <= 4 && fila > 0) {
			fila_embarque.signalAll(); // Se tiver até 4 passageiros, acorda todos com signalAll
		} else if (fila > 4) {
			for (int i = 0; i < 4; i++) {
				fila_embarque.signal(); // Se tiver mais de 4, acorda um por um com signal
			}
		}
		
		// Dorme esperando os passageiros embarcarem
		carrinho_embarque.awaitUninterruptibly();
		
		System.out.println("\nCarrinho " + (car_id + 1) + " saiu para o passeio\n");
		
		// Espera a sinalização dos passageiros para poder dar a volta
		
		controle.unlock(); // Libera o método para que outro carrinho possa acessar
		return true;
	}
	
	// Embarque do passageiro
	public void Embarque(int id) throws InterruptedException {
		controle.lock(); // Permite que apenas 1 passageiro acesse o método por vez
		
		long inicio;
		long fim;
		
		inicio = System.currentTimeMillis();
		
		// O passageiro espera na fila se não há carrinho disponível
		// ou se o carrinho está lotado
		if (!carrinho_disponivel[prox_carrinho_embarque] || passageiros_no_carro[prox_carrinho_embarque] == 4) {
			fila++;
			System.out.println("Passageiro " + id + " esperando o embarque\n");
			fila_embarque.awaitUninterruptibly(); // Dorme até que o carrinho o acorde
			fila--;                               // Decrementa quando acorda
		}
		
		fim = System.currentTimeMillis();
		
		tempo_de_espera = fim - inicio;
		tempo_total += tempo_de_espera;
		
		if (tempo_de_espera > tempo_maximo) tempo_maximo = tempo_de_espera;
		if (tempo_de_espera < tempo_minimo) tempo_minimo = tempo_de_espera;
		
		// Os passageiros embarcam
		passageiros_no_carro[prox_carrinho_embarque]++;
		
		System.out.println("Passageiro " + id + " esta embarcando no carrinho " + (prox_carrinho_embarque + 1) + "\n");
		
		if (passageiros_no_carro[prox_carrinho_embarque] == 4) { 
			carrinho_disponivel[prox_carrinho_embarque] = false;
			prox_carrinho_embarque = (prox_carrinho_embarque + 1) % n_carrinhos;
			Thread.sleep(1000); // Tempo do embarque
			System.out.println("\nOs 4 passageiros embarcaram\n");
			carrinho_embarque.signal(); // Acorda o carrinho para o passeio
		}
		
		no_carrinho.awaitUninterruptibly();
		
		controle.unlock(); // Libera o método para que outro passageiro possa acessar
	}
	
	// Desembarque do carrinho
	public void LiberaDesembarque(int car_id) {
		controle.lock();
		
		voltas[car_id]++;
		
		// Acorda os passageiros no carrinho depois que o passeio acaba
		for (int i = 0; i < 4; i++) {
			no_carrinho.signal();
		}
		
		// Dorme enquanto eles desembarcam
		carrinho_desembarque.awaitUninterruptibly();
		
		// Volta a ficar disponível depois de acordar
		System.out.println("\nCarrinho " + (car_id + 1) + " esta pronto para o embarque\n");
		
		// Acorda os passageiros que desembarcaram pra eles irem embora
		fila_desembarque.signalAll();
		
		controle.unlock();
	}
	
	// Desembarque do passageiro
	public void Desembarque(int id) throws InterruptedException {
		controle.lock();
		
		// Passageiros desembarcando
		passageiros_no_carro[prox_carrinho_desembarque]--;
		
		System.out.println("Passageiro " + id + " esta desembarcando do carrinho " + (prox_carrinho_desembarque + 1));
		
		if (passageiros_no_carro[prox_carrinho_desembarque] == 0) {
			prox_carrinho_desembarque = (prox_carrinho_desembarque + 1) % n_carrinhos;
			Thread.sleep(1000); // Tempo de desembarque
			System.out.println("\nOs 4 passageiros desembarcaram\n");
			carrinho_desembarque.signal();
		}
		
		fila_desembarque.awaitUninterruptibly();

		controle.unlock();
	}
	
	// Método principal
	public void Inicia() throws InterruptedException {
		
		inicio_dia = System.currentTimeMillis();
		
		CriaCarrinhos();
		CriaPassageiros();
		
		while(cont_carrinhos != 0) {
			Thread.sleep(100);
			//continue;
		}
		
		fim_dia = System.currentTimeMillis();
		
		duracao_dia = fim_dia - inicio_dia;
		
		double tempo_medio = (tempo_total/n_passageiros);
		
		double tempo_min;
		tempo_min = tempo_minimo;
		tempo_min = tempo_min/1000;
		
		double tempo_max;
		tempo_max = tempo_maximo;
		tempo_max = tempo_max/1000;
		
		double tempo_med;
		tempo_med = tempo_medio;
		tempo_med = tempo_med/1000;
		
		double utiliza;
		
		// Relatório de tempos
		System.out.println("~~~~~~~~~~~~~~~~~~~~~ Relatorio ~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("~~> Tempo minimo: " + tempo_min + "s");
		System.out.println("~~> Tempo maximo: " + tempo_max + "s");
		System.out.println("~~> Tempo medio: " + tempo_med + "s");
		
		for(int i = 0; i < n_carrinhos; i++) {
			utiliza = (voltas[i] * 10000);
			utiliza = utiliza/(duracao_dia);
			System.out.println("~~> Utilizacao do carrinho " + (i + 1) + ": " + utiliza);
		}
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
	}
	
}