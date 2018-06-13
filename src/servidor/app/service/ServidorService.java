package servidor.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cliente.app.util.Cliente;

public class ServidorService {

	private int porta;
	private ServerSocket serverSocket;
	private Socket socket;
	private HashMap<String, Cliente> mapOnlines = new HashMap<>();
	private ArrayList<Cliente> prontos = new ArrayList<>();
	private ArrayList<Cliente> sorteio = new ArrayList<>();
	private ArrayList<Cliente> ordem;
	private HashMap<String, JSONObject> mapPacotes = new HashMap<>();
	private boolean iniciado = false;
	private boolean acertaram = false;
	private String palavra;

	public void conectar(int p) {
		this.porta = p;
		try {
			serverSocket = new ServerSocket(porta);

			while (true) {
				socket = serverSocket.accept();
				new Thread(new ListenerSocket(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Cliente> getMapOnlines() {
		return mapOnlines;
	}

	public void setMapOnlines(HashMap<String, Cliente> mapOnlines) {
		this.mapOnlines = mapOnlines;
	}

	private class ListenerSocket implements Runnable {

		private PrintWriter output;
		private BufferedReader input;
		private Socket sock;
		private Cliente cliente;

		public ListenerSocket(Socket s) {
			try {
				sock = s;
				this.output = new PrintWriter(s.getOutputStream(), true);
				this.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			JSONObject jsonObject;
			try {
				String s;
				while ((s = input.readLine()) != null) {
					jsonObject = new JSONObject(s);
					if (cliente != null) {
						System.out.println("Recebeu de " + cliente.getNome() + ": " + jsonObject.toString());
						cliente.setTempoUltimoPacote(System.currentTimeMillis());
					} else
						System.out.println("Recebeu de " + sock.getInetAddress() + ":" + sock.getPort() + " :"
								+ jsonObject.toString());
					switch (jsonObject.getInt("id")) {
					case 1:
						boolean isConnect = connect(jsonObject, output);
						if (isConnect) {
							cliente = new Cliente(jsonObject.getString("nome"), output, sock.getInetAddress(),
									sock.getPort());
							cliente.setTempoUltimoPacote(System.currentTimeMillis());
							mapOnlines.put(jsonObject.getString("nome"), cliente);
							sendOnlines();
						}
						break;
					case 4:
						disconnect(cliente);
						break;
					case 5:
						chatAll(jsonObject, cliente);
						break;
					case 6:
						chatPrivate(jsonObject, cliente);
						break;
					case 8:
						prontoJogo(cliente);
						break;
					case 11:
						palavraEscolhida(jsonObject, cliente);
						break;
					case 13:
						confereLetra(jsonObject, cliente);
						break;
					case 16:
						chutaPalavra(jsonObject, cliente);
						break;
					case 19:
						palavraVerificada(jsonObject, cliente);
						break;
					}
				}
			} catch (IOException e) {
				try {
					disconnect(cliente);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		private boolean connect(JSONObject json, PrintWriter output) throws JSONException, IOException {
			JSONObject jsonObject = new JSONObject();
			if (mapOnlines.size() < 6 && !mapOnlines.containsKey(json.getString("nome")) && !iniciado) {
				jsonObject.put("id", 2);
				jsonObject.put("conectou", true);
				System.out.println("Enviou para " + json.getString("nome") + ": " + jsonObject.toString());
				output.println(jsonObject.toString());
				return true;
			} else {
				jsonObject.put("id", 2);
				jsonObject.put("conectou", false);
				if (mapOnlines.size() >= 5)
					jsonObject.put("motivo", "Numero de clientes excedido");
				else if (mapOnlines.containsKey(json.getString("nome")))
					jsonObject.put("motivo", "Nome já existente");
				else
					jsonObject.put("motivo", "Jogo já iniciado");
				System.out.println("Enviou para " + json.getString("nome") + ": " + jsonObject.toString());
				output.println(jsonObject.toString());
				return false;
			}
		}

		private void disconnect(Cliente cliente) throws JSONException {
			HashMap<String, Cliente> novos = new HashMap<>();
			for (Map.Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				if (!kv.getValue().getOutput().equals(output))
					novos.put(kv.getKey(), kv.getValue());
				else {
					if (iniciado) {
						ordem.remove(cliente);
						sorteio.remove(cliente);
					}
				}
			}
			prontos.remove(cliente);
			if (iniciado) {
				desconexao(cliente);
			}
			mapOnlines = novos;

			if (mapOnlines.size() < 2)
				iniciado = false;

			sendOnlines();
		}

		private void send(JSONObject json, Cliente cliente) throws IOException {
			System.out.println("Enviou para " + cliente.getNome() + ": " + json.toString());
			cliente.getOutput().println(json.toString());
		}

		private void sendAll(JSONObject json, Cliente cliente) throws JSONException {
			for (Map.Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				if (!kv.getValue().equals(cliente)) {
					System.out.println("Enviou para " + kv.getKey() + ": " + json.toString());
					kv.getValue().getOutput().println(json.toString());
				}
			}
		}

		private void sendOnlines() throws JSONException {
			Set<String> setNames = new HashSet<String>();
			for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				setNames.add(kv.getKey());
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 3);
			jsonObject.put("qtdClientes", mapOnlines.size());
			jsonObject.put("nome", setNames);
			for (Map.Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				System.out.println("Enviou para " + kv.getKey() + ": " + jsonObject.toString());
				kv.getValue().getOutput().println(jsonObject.toString());

			}
		}

		private void prontoJogo(Cliente cliente) throws JSONException {
			cliente.setPronto(true);
			prontos.add(cliente);
			iniciaJogo();
		}

		private void iniciaJogo() throws JSONException {
			boolean key;
			if (prontos.size() == mapOnlines.size() && prontos.size() > 1) {
				key = true;
			} else {
				key = false;
			}

			if (key) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", 9);
				System.out.println("todos prontos, inicia jogo");
				sendAll(jsonObject, null);
				iniciado = true;
				sorteio();
			}
		}

		private void sorteio() throws JSONException {
			ArrayList<Cliente> s = new ArrayList<Cliente>(mapOnlines.values());
			Random r = new Random();
			ordem = new ArrayList<>();

			while (ordem.size() < mapOnlines.size()) {
				int numSorteado = r.nextInt(mapOnlines.size());
				if (!ordem.contains(s.get(numSorteado))) {
					s.get(numSorteado).setOrdem(numSorteado);
					s.get(numSorteado).setErros(6 / mapOnlines.size());
					ordem.add(s.get(numSorteado));
				}
			}
			for (Cliente cliente : ordem) {
				//System.out.println(cliente.getNome() + " " + cliente.getOrdem());
				sorteio.add(cliente);
			}
			System.out.println("Mestre: " + ordem.get(0).getNome());
			selecionaMestre(ordem.get(0));
		}

		private void selecionaMestre(Cliente cliente) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 10);

			try {
				send(jsonObject, cliente);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private ArrayList<String> enviaVez(Cliente cliente) {
			ArrayList<String> setNames = new ArrayList<String>();
			for (int i = 0; i < ordem.size(); i++) {
				if (!ordem.get(i).equals(cliente))
					setNames.add(ordem.get(i).getNome());
			}
			return setNames;
		}

		private void palavraEscolhida(JSONObject json, Cliente cliente) throws JSONException {
			palavra = json.getString("palavra");
			acertaram = false;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 12);
			jsonObject.put("TamanhoPalavra", json.getString("palavra").length());
			jsonObject.put("vez", enviaVez(cliente));

			sendAll(jsonObject, null);

		}

		private void chatAll(JSONObject json, Cliente cliente) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				if (kv.getValue().equals(cliente))
					jsonObject.put("emissor", kv.getKey());
			}

			jsonObject.put("id", 7);
			jsonObject.put("broadcast", true);
			jsonObject.put("mensagem", json.get("mensagem"));

			sendAll(jsonObject, cliente);
		}

		private void chatPrivate(JSONObject json, Cliente cliente) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				if (kv.getValue().equals(cliente))
					jsonObject.put("emissor", kv.getKey());
			}

			jsonObject.put("id", 7);
			jsonObject.put("broadcast", false);
			jsonObject.put("mensagem", json.get("mensagem"));

			try {
				send(jsonObject, mapOnlines.get(json.getString("destinatario")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void desconexao(Cliente cliente) throws JSONException {
			if (!ordem.isEmpty()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", 21);
				jsonObject.put("vez", enviaVez(ordem.get(0)));

				sendAll(jsonObject, cliente);
			}
		}

		private void confereLetra(JSONObject json, Cliente cliente) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 14);
			jsonObject.put("letra", json.get("letra"));
			if (palavra.indexOf(json.getInt("letra")) != -1)
				jsonObject.put("correto", true);
			else {
				jsonObject.put("correto", false);
				cliente.setErros(cliente.getErros() - 1);
				if (cliente.getErros() == 0) {
					ordem.remove(cliente);
					desconexao(cliente);
				}
			}

			try {
				send(jsonObject, cliente);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (ordem.size() < 2)
				verificaPalavra();

		}

		private void chutaPalavra(JSONObject json, Cliente cliente) throws JSONException {
			if (json.getString("palavra").equals(palavra)) {
				acertaram = true;
				cliente.setAcertou(true);
				verificaPalavra();
			} else {
				JSONObject jsonObject = new JSONObject();
				ordem.remove(cliente);
				jsonObject.put("id", 17);
				jsonObject.put("palavra", json.getString("palavra"));
				if (ordem.size() < 2)
					verificaPalavra();
				else {
					jsonObject.put("vez", enviaVez(ordem.get(0)));

					sendAll(jsonObject, null);
				}
			}

		}

		private void verificaPalavra() throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 18);
			jsonObject.put("palavra", palavra);
			jsonObject.put("nome", ordem.get(0).getNome());

			sendAll(jsonObject, ordem.get(0));
		}

		private void palavraVerificada(JSONObject json, Cliente cliente) throws JSONException {
			boolean key = true;
			mapPacotes.put(cliente.getNome(), json);
			if (mapPacotes.size() >= (mapOnlines.size() - 1)) {
				for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
					if (!mapPacotes.containsKey(kv.getKey()) && !kv.getValue().equals(ordem.get(0))) {
						System.out.println("Entrou");
						System.out.println(kv.getKey() + " " + kv.getValue().getOrdem() + " "
								+ !mapPacotes.containsKey(kv.getKey()));
						key = false;
					}
				}
				if (key) {
					// Seta opiniao da palavra
					for (Entry<String, JSONObject> kv : mapPacotes.entrySet()) {
						mapOnlines.get(kv.getKey()).setAceita(kv.getValue().getBoolean("verificado"));
					}
					// Verifica qtde de aceito
					int ac = 0;
					for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
						if (kv.getValue().isAceita())
							ac++;
					}
					// Verifica se qtde é maior
					boolean palavraAc = false;
					if ((mapPacotes.size() - ac) <= ac)
						palavraAc = true;

					// Limpa mapa de pacotes
					mapPacotes.clear();

					// Se a palavra for aceita, fa Contagem de pontos
					if (palavraAc)
						contaPontos();
					// contagem de pontos

					// Cria pacote de retorno
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", 20);
					jsonObject.put("palavraAceita", palavraAc);
					ArrayList<String> nomes = new ArrayList<>();
					ArrayList<Integer> pontos = new ArrayList<>();
					for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
						nomes.add(kv.getKey());
						pontos.add(kv.getValue().getPontos());
					}
					JSONObject clientes = new JSONObject();
					clientes.put("nomes", nomes);
					clientes.put("pontos", pontos);
					jsonObject.put("clientes", clientes);

					sendAll(jsonObject, null);
					novaRodada();
				}
			}
		}

		private void alteraOrdem() {
			ordem.clear();
			for (int i = 1; i < sorteio.size(); i++) {
				sorteio.get(i).setOrdem(i - 1);
				sorteio.get(i).setErros(6 / mapOnlines.size());
				sorteio.get(i).setAcertou(false);
				ordem.add(sorteio.get(i));
			}
			sorteio.get(0).setOrdem(sorteio.size() - 1);
			ordem.add(sorteio.get(0));

			sorteio.clear();

			for (Cliente cliente : ordem) {
				sorteio.add(cliente);
			}
		}

		private void novaRodada() throws JSONException {
			boolean key = true;
			for (Entry<String, Cliente> kv : mapOnlines.entrySet()) {
				if (kv.getValue().getPontos() >= 5)
					key = false;
			}
			if (key) {
				alteraOrdem();
				System.out.println("Mestre: " + ordem.get(0).getNome());
				selecionaMestre(ordem.get(0));
			}
		}

		private void contaPontos() {
			if (acertaram) {
				for (Cliente cliente : sorteio) {
					if (cliente.isAcertou()) {
						cliente.setPontos(cliente.getPontos() + 1);
					}
				}
			} else {
				ordem.get(0).setPontos(ordem.get(0).getPontos() + 2);
			}

		}

	}

}
