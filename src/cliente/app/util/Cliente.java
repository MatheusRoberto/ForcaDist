package cliente.app.util;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Calendar;

public class Cliente {
	private String nome;
	private PrintWriter output;
	private InetAddress ip;
	private int porta;
	private boolean pronto;
	private int ordem;
	private boolean aceita;
	private int pontos;
	private int erros;
	private boolean acertou;
	private long tempoUltimoPacote;
	private Calendar tempoEspera;
	private int njogados;
	private boolean salvo;
	
	public Cliente() {
		// TODO Auto-generated constructor stub
	}
	
	public Cliente(String nome, PrintWriter output, InetAddress ip, int porta) {
		// TODO Auto-generated constructor stub
		setNome(nome);
		setOutput(output);
		setIp(ip);
		setPorta(porta);
		setPronto(false);
		setOrdem(0);
		setAceita(false);
		setPontos(0);
		setAcertou(false);
		setNjogados(0);
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public PrintWriter getOutput() {
		return output;
	}
	public void setOutput(PrintWriter output) {
		this.output = output;
	}
	public boolean isPronto() {
		return pronto;
	}
	public void setPronto(boolean pronto) {
		this.pronto = pronto;
	}
	public int getOrdem() {
		return ordem;
	}
	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	public boolean isAceita() {
		return aceita;
	}
	public void setAceita(boolean aceita) {
		this.aceita = aceita;
	}
	public int getPontos() {
		return pontos;
	}
	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public int getErros() {
		return erros;
	}

	public void setErros(int erros) {
		this.erros = erros;
	}

	public long getTempoUltimoPacote() {
		return tempoUltimoPacote;
	}

	public void setTempoUltimoPacote(long tempoUltimoPacote) {
		this.tempoUltimoPacote = tempoUltimoPacote;
	}

	public Calendar getTempoEspera() {
		return tempoEspera;
	}

	public void setTempoEspera(Calendar tempoEspera) {
		this.tempoEspera = tempoEspera;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public boolean isAcertou() {
		return acertou;
	}

	public void setAcertou(boolean acertou) {
		this.acertou = acertou;
	}
	
	public int getNjogados() {
		return njogados;
	}

	public void setNjogados(int njogados) {
		this.njogados = njogados;
	}

	public boolean isSalvo() {
		return salvo;
	}

	public void setSalvo(boolean salvo) {
		this.salvo = salvo;
	}

	public void tempoJogada() {
		Calendar c = Calendar.getInstance();
		//System.out.println(c.getTime());
		c.add(Calendar.SECOND, 15);
		//System.out.println(c.getTime());
		setTempoEspera(c);
	}
	
	public void tempoMestre() {
		Calendar c = Calendar.getInstance();
		//System.out.println(c.getTime());
		c.add(Calendar.SECOND, 60);
		//System.out.println(c.getTime());
		setTempoEspera(c);
	}
	
	public boolean tempoExcedido() {
		return getTempoEspera().getTimeInMillis() >= System.currentTimeMillis();
	}
}
