class Carrinho implements Runnable {

	private int identity;
	private Montanha mont;

	// Construtor
	public Carrinho(int id, Montanha m) {
		identity = id;
		mont = m;
	}

	// Run com os métodos executados pelo carrinho + a volta na montanha
	public void run() {
		try {
			while(mont.LiberaEmbarque(identity)) {
				Thread.sleep(10000); // passeio
				mont.LiberaDesembarque(identity);
			}
		} catch (InterruptedException e) {}
	}

}