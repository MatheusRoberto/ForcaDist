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

public class ServidorService extends Thread {

	private int porta;
	private ServerSocket serverSocket;
	private Socket socket;
	private HashMap<String, PrintWriter> mapOnlines = new HashMap<>();
	private ArrayList<PrintWriter> prontos = new ArrayList<>();
	private ArrayList<String> sorteio;
	private ArrayList<Integer> ordem;
	private boolean iniciado = false;
	private int rodada = 0;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<String, PrintWriter> getMapOnlines() {
		return mapOnlines;
	}

	public void setMapOnlines(HashMap<String, PrintWriter> mapOnlines) {
		this.mapOnlines = mapOnlines;
	}

	private class ListenerSocket implements Runnable {

		private PrintWriter output;
		private BufferedReader input;

		public ListenerSocket(Socket s) {
			// TODO Auto-generated constructor stub
			try {
				this.output = new PrintWriter(s.getOutputStream(), true);
				this.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JSONObject jsonObject;
			try {
				String s;
				while ((s = input.readLine()) != null) {
					jsonObject = new JSONObject(s);
					System.out.println("Recebeu: "+jsonObject.toString());
					switch (jsonObject.getInt("id")) {
					case 1:
						boolean isConnect = connect(jsonObject, output);
						if (isConnect) {
							mapOnlines.put((String) jsonObject.get("nome"), output);
							sendOnlines();
						}
						break;
					case 4:
						disconnect(output);
						break;
					case 5:
						chatAll(jsonObject, output);
						break;
					case 6:
						chatPrivate(jsonObject, output);
						break;
					case 8:
						prontoJogo(output);
						break;
					case 11:
						palavraEscolhida(jsonObject, output);
						break;
					case 13:
						confereLetra(jsonObject, output);
						break;
					}
				}
			} catch (IOException e) {
				// TODO: handle exception
				try {
					disconnect(output);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private boolean connect(JSONObject json, PrintWriter output) throws JSONException, IOException {
			JSONObject jsonObject = new JSONObject();
			if (mapOnlines.size() < 6 && !mapOnlines.containsKey(json.getString("nome")) && !iniciado) {
				jsonObject.put("id", 2);
				jsonObject.put("conectou", true);
				send(jsonObject, output);
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
				send(jsonObject, output);
				return false;
			}
		}

		private void disconnect(PrintWriter output) throws JSONException {
			HashMap<String, PrintWriter> novos = new HashMap<>();
			for (Map.Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				if (!kv.getValue().equals(output))
					novos.put(kv.getKey(), kv.getValue());
				else {
					int rem = -1;
					int idex = 0;
					if (iniciado) {
						for (int i = 0; i < ordem.size(); i++) {
							if (ordem.get(i) > rem) {
								rem = ordem.get(i);
								idex = i;
							}
						}
						ordem.remove(idex);
						sorteio.remove(kv.getKey());
					}
				}
			}
			prontos.remove(output);
			if (iniciado) {
				rodada--;
				desconexao(mapOnlines.get(sorteio.get(ordem.get(rodada))));
			}
			mapOnlines = novos;

			if (mapOnlines.size() < 2)
				iniciado = false;

			sendOnlines();
		}

		private void send(JSONObject json, PrintWriter output) throws IOException {
			System.out.println("Enviou: "+json.toString());
			output.println(json.toString());
		}

		private void sendAll(JSONObject json, PrintWriter output) throws JSONException {
			for (Map.Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				if (!kv.getValue().equals(output)) {
					System.out.println("Enviou para "+kv.getKey()+": "+json.toString());
					kv.getValue().println(json.toString());
				}
			}
		}

		private void sendOnlines() throws JSONException {
			Set<String> setNames = new HashSet<String>();
			for (Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				setNames.add(kv.getKey());
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 3);
			jsonObject.put("qtdClientes", mapOnlines.size());
			jsonObject.put("nome", setNames);
			for (Map.Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				System.out.println("Enviou para "+kv.getKey()+": "+jsonObject.toString());
				kv.getValue().println(jsonObject.toString());

			}
		}

		private void prontoJogo(PrintWriter output) throws JSONException {
			prontos.add(output);
			iniciaJogo();
			// System.out.println(prontos);
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
				rodada = mapOnlines.size() - 1;
				sorteio();
			}
		}

		private void sorteio() throws JSONException {
			sorteio = new ArrayList<String>(mapOnlines.keySet());
			Random r = new Random();
			ordem = new ArrayList<>();

			while (ordem.size() < mapOnlines.size()) {
				int numSorteado = r.nextInt(mapOnlines.size());
				if (!ordem.contains(numSorteado))
					ordem.add(numSorteado);
			}
			System.out.println("Mestre: "+sorteio.get(ordem.get(rodada)));
			selecionaMestre(mapOnlines.get(sorteio.get(ordem.get(rodada))));
		}

		private void selecionaMestre(PrintWriter outputStream) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 10);

			try {
				send(jsonObject, outputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private ArrayList<String> enviaVez(PrintWriter output) {
			ArrayList<String> setNames = new ArrayList<String>();
			for (int i = 0; i < ordem.size(); i++) {
				if (!mapOnlines.get(sorteio.get(ordem.get(i))).equals(output))
					setNames.add(sorteio.get(ordem.get(i)));
			}
			return setNames;
		}

		private void palavraEscolhida(JSONObject json, PrintWriter output) throws JSONException {
			palavra = json.getString("palavra");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 12);
			jsonObject.put("TamanhoPalavra", json.getString("palavra").length());
			jsonObject.put("vez", enviaVez(output));

			sendAll(jsonObject, null);

		}

		private void chatAll(JSONObject json, PrintWriter output) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			for (Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				if (kv.getValue().equals(output))
					jsonObject.put("emissor", kv.getKey());
			}

			jsonObject.put("id", 7);
			jsonObject.put("broadcast", true);
			jsonObject.put("mensagem", json.get("mensagem"));

			sendAll(jsonObject, output);
		}

		private void chatPrivate(JSONObject json, PrintWriter output) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			for (Entry<String, PrintWriter> kv : mapOnlines.entrySet()) {
				if (kv.getValue().equals(output))
					jsonObject.put("emissor", kv.getKey());
			}

			jsonObject.put("id", 7);
			jsonObject.put("broadcast", false);
			jsonObject.put("mensagem", json.get("mensagem"));

			try {
				send(jsonObject, mapOnlines.get(json.getString("destinatario")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void desconexao(PrintWriter output) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 21);
			jsonObject.put("vez", enviaVez(output));

			sendAll(jsonObject, null);
		}
		
		private void confereLetra(JSONObject json, PrintWriter output) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 14);
			jsonObject.put("letra", json.get("letra"));
			if (palavra.indexOf(json.getInt("letra")) != -1) 
				jsonObject.put("correto", true);
			else 
				jsonObject.put("correto", false);
			
			try {
				send(jsonObject, output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
