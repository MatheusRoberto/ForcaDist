package servidor.app;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import servidor.app.service.ServidorService;

public class Servidor {

	public static void main(String[] args) {
		JLabel lblMessage = new JLabel("Server Port: ");
		JTextField txtPort = new JTextField("20000");
		Object[] texts = { lblMessage, txtPort };
		JOptionPane.showMessageDialog(null, texts);

		ServidorService service = new ServidorService();
		service.conectar(Integer.parseInt(txtPort.getText()));
	}

}
