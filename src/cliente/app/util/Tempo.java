package cliente.app.util;

public class Tempo extends Thread{

	private Cliente cliente;
	
	public Tempo(Cliente c) {
		// TODO Auto-generated constructor stub
		this.cliente = c;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
}
