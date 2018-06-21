package servidor.app;

import servidor.app.service.ServidorService;

public class Servidor implements Runnable{

	private ServidorService service;
	private int porta;
	
	public Servidor(int porta) {
		// TODO Auto-generated constructor stub
		service = new ServidorService();
		this.porta = porta;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		service.conectar(porta);
	}

}
