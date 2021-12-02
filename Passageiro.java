class Passageiro implements Runnable {

	private int identity;
	private Montanha mont;

	// Construtor
	public Passageiro(int id, Montanha m) {
		identity = id;
		mont = m;
	}

	// Run com os métodos executados pelo passageiro
	public void run() {
		try {
			mont.Embarque(identity);
			mont.Desembarque(identity);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}