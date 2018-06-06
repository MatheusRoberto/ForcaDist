package cliente.app.service;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import org.json.JSONObject;

public class ClienteService {

	private Socket socket;
    private PrintStream output;
	
    
    public Socket connect(String ip, int porta) {
    	try {
			this.socket = new Socket(ip, porta);
			this.output = new PrintStream(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return socket;
    }
    
    public void send(JSONObject jsonObject) throws IOException {
    	System.out.println("Enviou: "+jsonObject.toString());
    	output.println(jsonObject.toString());
    }
}
