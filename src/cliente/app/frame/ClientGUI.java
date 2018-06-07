package cliente.app.frame;

import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.json.JSONException;
import org.json.JSONObject;

import cliente.app.service.ClienteService;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingConstants;

public class ClientGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private ClienteService service;
	private boolean conectado = false;
	private ArrayList<JButton> teclado = new ArrayList<>();

	private JPanel contentPane;
	private JTextField txtIp;
	private JTextField txtPorta;
	private JTextField txtNome;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private JList<String> listOnlines;
	private JPanel panelJogo;
	private JToggleButton tglbtnPronto;
	private JPanel panelForca;
	private JLabel lblQuantidadeDeLetras;
	private JPanel panel;
	private JList<String> listVez;
	private JTextField txtChute;
	private JButton btnEnviar;
	private JCheckBox cbPrivado;
	private JTextField txtTextoChat;
	private JTextArea txtrChat;
	private JLabel lblPalavra;
	private JPanel panelLetras;
	private JPanel panelPalavra;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (conectado) {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("id", 4);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						service.send(jsonObject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					desconectar();
				}
			}
		});
		setTitle("Cliente Forca");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1216, 428);
		contentPane = new JPanel();
		contentPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelConectar = new JPanel();
		panelConectar.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Conectar Servidor",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panelConectar.setBounds(12, 12, 643, 60);
		contentPane.add(panelConectar);
		panelConectar.setLayout(null);

		txtIp = new JTextField();
		txtIp.setText("localhost");
		txtIp.setBounds(12, 29, 114, 19);
		panelConectar.add(txtIp);
		txtIp.setColumns(10);

		txtPorta = new JTextField();
		txtPorta.setText("20000");
		txtPorta.setBounds(138, 29, 80, 19);
		panelConectar.add(txtPorta);
		txtPorta.setColumns(10);

		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!txtNome.getText().isEmpty() && !txtIp.getText().isEmpty() && !txtPorta.getText().isEmpty()
						&& !conectado) {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("id", 1);
						jsonObject.put("nome", txtNome.getText());

						if (socket == null) {
							service = new ClienteService();
							socket = service.connect(txtIp.getText(), Integer.parseInt(txtPorta.getText()));

							new Thread(new ListenerSocket(socket)).start();
						}

						service.send(jsonObject);
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnConectar.setBounds(356, 26, 117, 25);
		panelConectar.add(btnConectar);

		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (conectado) {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("id", 4);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						service.send(jsonObject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					desconectar();
				}
			}
		});
		btnDesconectar.setEnabled(false);
		btnDesconectar.setBounds(485, 26, 148, 25);
		panelConectar.add(btnDesconectar);

		txtNome = new JTextField();
		txtNome.setText("Nome");
		txtNome.setBounds(230, 29, 114, 19);
		panelConectar.add(txtNome);
		txtNome.setColumns(10);

		panelJogo = new JPanel();
		panelJogo.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelJogo.setBounds(22, 84, 633, 298);
		contentPane.add(panelJogo);
		panelJogo.setLayout(null);

		tglbtnPronto = new JToggleButton("Pronto");
		tglbtnPronto.setEnabled(false);
		tglbtnPronto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("id", 8);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					service.send(jsonObject);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tglbtnPronto.setEnabled(false);
			}
		});
		tglbtnPronto.setBounds(146, 9, 167, 25);
		panelJogo.add(tglbtnPronto);

		panelForca = new JPanel();
		panelForca.setBounds(12, 46, 609, 210);
		panelJogo.add(panelForca);
		panelForca.setLayout(null);
		
		panelPalavra = new JPanel();
		panelPalavra.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelPalavra.setBounds(12, 12, 438, 67);
		panelForca.add(panelPalavra);
		panelPalavra.setLayout(null);

		lblQuantidadeDeLetras = new JLabel("Quantidade de Letras: ");
		lblQuantidadeDeLetras.setBounds(12, 0, 221, 15);
		panelPalavra.add(lblQuantidadeDeLetras);
		
				lblPalavra = new JLabel("");
				lblPalavra.setVerticalAlignment(SwingConstants.BOTTOM);
				lblPalavra.setHorizontalAlignment(SwingConstants.CENTER);
				lblPalavra.setBounds(12, 12, 414, 40);
				panelPalavra.add(lblPalavra);
				lblPalavra.setFont(new Font("Dialog", Font.BOLD, 22));

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "Vez de Jogar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(462, 12, 135, 186);
		panelForca.add(panel);

		listVez = new JList<String>();
		listVez.setEnabled(false);
		listVez.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listVez.setBounds(12, 31, 111, 143);
		panel.add(listVez);
		
		panelLetras = new JPanel();
		panelLetras.setBounds(12, 91, 446, 107);
		panelForca.add(panelLetras);

		txtChute = new JTextField();
		txtChute.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(!txtChute.getText().isEmpty())
				try {
					chutaPalavra(txtChute.getText());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		txtChute.setEnabled(false);
		txtChute.setBounds(121, 268, 331, 19);
		panelJogo.add(txtChute);
		txtChute.setColumns(10);

		JLabel lblPalavraChute = new JLabel("Palavra Chute:");
		lblPalavraChute.setBounds(12, 270, 104, 15);
		panelJogo.add(lblPalavraChute);
		
		JButton btnChutar = new JButton("Chutar");
		btnChutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!txtChute.getText().isEmpty())
				try {
					chutaPalavra(txtChute.getText());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnChutar.setBounds(481, 265, 117, 25);
		panelJogo.add(btnChutar);

		JPanel panelChat = new JPanel();
		panelChat.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelChat.setBounds(667, 12, 531, 370);
		contentPane.add(panelChat);
		panelChat.setLayout(null);

		JPanel panelOnlines = new JPanel();
		panelOnlines.setBounds(12, 12, 147, 286);
		panelChat.add(panelOnlines);
		panelOnlines.setBorder(new TitledBorder(null, "Onlines", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelOnlines.setLayout(null);

		listOnlines = new JList<String>();
		listOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listOnlines.setBounds(12, 26, 123, 248);
		panelOnlines.add(listOnlines);

		JPanel panelText = new JPanel();
		panelText.setBounds(171, 24, 356, 274);
		panelChat.add(panelText);
		panelText.setLayout(null);

		txtrChat = new JTextArea();
		txtrChat.setEditable(false);
		txtrChat.setBounds(8, 12, 336, 250);
		panelText.add(txtrChat);

		txtTextoChat = new JTextField();
		txtTextoChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						enviaChat();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		txtTextoChat.setEnabled(false);
		txtTextoChat.setBounds(24, 336, 370, 19);
		panelChat.add(txtTextoChat);
		txtTextoChat.setColumns(10);

		cbPrivado = new JCheckBox("Privado");
		cbPrivado.setEnabled(false);
		cbPrivado.setBounds(22, 310, 78, 23);
		panelChat.add(cbPrivado);

		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					enviaChat();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnEnviar.setEnabled(false);
		btnEnviar.setBounds(410, 333, 117, 25);
		panelChat.add(btnEnviar);
		geraTeclado();
	}

	private void geraTeclado() {
		for (int i = 'a'; i <= 'z'; i++) {
			JButton button = new JButton(String.valueOf(Character.toUpperCase((char) i)));
			button.setText(String.valueOf(Character.toUpperCase((char) i)));
			button.setEnabled(false);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JButton b = (JButton) e.getSource();
					try {
						chutaLetra(b.getText().charAt(0));
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			panelLetras.add(button);
			teclado.add(button);
		}
	}

	private class ListenerSocket implements Runnable {

		private BufferedReader input;

		public ListenerSocket(Socket s) {
			// TODO Auto-generated constructor stub
			try {
				this.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JSONObject jsonObject = null;
			try {
				String s;
				while ((s = input.readLine().trim()) != null) {
					jsonObject = new JSONObject(s);
					System.out.println("Recebeu: "+jsonObject.toString());
					switch (jsonObject.getInt("id")) {
					case 2:
						conectar(jsonObject);
						break;
					case 3:
						refreshOnlines(jsonObject);
						break;
					case 7:
						recebeChat(jsonObject);
						break;
					case 10:
						selecionaPalavra();
						break;
					case 12:
						jogar(jsonObject);
						break;
					case 14:
						recebeLetra(jsonObject);
						break;
					case 18:
						verificaOpiniao(jsonObject);
					case 21:
						imprimeVez(jsonObject);
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void conectar(JSONObject json) throws HeadlessException, JSONException {
		if (!json.getBoolean("conectou")) {
			JOptionPane.showMessageDialog(this, "Conexão não realizada!\n Motivo: " + json.getString("motivo"),
					"Conexão não realizada!'", JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.btnConectar.setEnabled(false);
		this.txtNome.setEditable(false);

		this.btnDesconectar.setEnabled(true);
		this.tglbtnPronto.setEnabled(true);
		this.txtTextoChat.setEnabled(true);
		this.btnEnviar.setEnabled(true);
		this.cbPrivado.setEnabled(true);
		
		for (JButton jButton : teclado) {
			jButton.setEnabled(true);
		}
		
		conectado = true;

		// JOptionPane.showMessageDialog(this, "Você está conectado!", "Você está
		// conectado!",
		// JOptionPane.INFORMATION_MESSAGE);

	}

	private void desconectar() {

		this.btnConectar.setEnabled(true);
		this.txtNome.setEditable(true);

		this.btnDesconectar.setEnabled(false);
		this.tglbtnPronto.setEnabled(false);
		this.tglbtnPronto.setSelected(false);
		this.txtTextoChat.setEnabled(false);
		this.btnEnviar.setEnabled(false);
		this.cbPrivado.setEnabled(false);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.clear();
		listOnlines.setModel(listModel);
		listVez.setModel(listModel);
		this.lblQuantidadeDeLetras.setText("Quantidade de Letras: ");
		this.lblPalavra.setText("");
		
		for (JButton jButton : teclado) {
			jButton.setEnabled(false);
		}
		
		conectado = false;
		socket = null;
		service = null;

		// JOptionPane.showMessageDialog(this, "Você desconectou!");
	}

	private void refreshOnlines(JSONObject json) throws JSONException {
		ArrayList<String> onlines = new ArrayList<>();
		for (int i = 0; i < json.getJSONArray("nome").length(); i++) {
			if (!json.getJSONArray("nome").get(i).toString().equals(txtNome.getText())) {
				onlines.add(json.getJSONArray("nome").get(i).toString());
			}
		}

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String string : onlines) {
			listModel.addElement(string);
		}
		this.listOnlines.setModel(listModel);

	}

	private void selecionaPalavra() throws JSONException {
		String palavra = new String();
		while (palavra.isEmpty()) {
			palavra = JOptionPane.showInputDialog(this, "Digite uma palavra para o jogo iniciar?");
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 11);
		jsonObject.put("palavra", palavra.toLowerCase());
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void imprimePalavra(int tamanho) {
		String palavra = new String();
		for (int i = 0; i < tamanho; i++) {
			palavra = palavra + "_ ";
		}

		lblPalavra.setText(palavra);
	}

	private void jogar(JSONObject json) throws JSONException {
		lblQuantidadeDeLetras
				.setText(lblQuantidadeDeLetras.getText() + " " + String.valueOf(json.getInt("TamanhoPalavra")));
		
		imprimePalavra(json.getInt("TamanhoPalavra"));

		imprimeVez(json);
	}

	private void imprimeVez(JSONObject json) throws JSONException {
		ArrayList<String> vez = new ArrayList<>();
		for (int i = 0; i < json.getJSONArray("vez").length(); i++) {
			vez.add(json.getJSONArray("vez").get(i).toString());
		}

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String string : vez) {
			listModel.addElement(string);
		}
		this.listVez.setModel(listModel);
		this.listVez.repaint();
	}
	
	private void enviaChat() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (!txtTextoChat.getText().isEmpty()) {
			if (cbPrivado.isSelected() && !listOnlines.isSelectionEmpty()) {
				jsonObject.put("id", 6);
				jsonObject.put("mensagem", txtTextoChat.getText());
				jsonObject.put("destinatario", listOnlines.getSelectedValue());
				try {
					service.send(jsonObject);
					txtrChat.append("eu: " + txtTextoChat.getText() + "\n");
					txtTextoChat.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (!cbPrivado.isSelected()) {
				jsonObject.put("id", 5);
				jsonObject.put("mensagem", txtTextoChat.getText());
				try {
					service.send(jsonObject);
					txtrChat.append("eu: " + txtTextoChat.getText() + "\n");
					txtTextoChat.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selecione um usuário para enviar mensagem privada");
			}
		} else
			JOptionPane.showMessageDialog(this, "Digite um texto para enviar");
	}

	private void recebeChat(JSONObject json) throws JSONException {
		if (!json.getBoolean("broadcast")) {
			txtrChat.append("Mensagem privada de " + json.getString("emissor") + " disse: " + json.getString("mensagem")
					+ "\n");
		} else {
			txtrChat.append(json.getString("emissor") + " disse: " + json.getString("mensagem") + "\n");
		}
	}
	
	private void chutaLetra(char c) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 13);
		jsonObject.put("letra", Character.toLowerCase(c));
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recebeLetra(JSONObject json) {
		System.out.println(json.toString());
	}
	
	private void chutaPalavra(String s) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 16);
		jsonObject.put("palavra", s);
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void verificaOpiniao(JSONObject json) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 19);
		JLabel lblPalavra = new JLabel("Palavra: "+json.getString("palavra"));
		JLabel lblMestre = new JLabel("Mestre: "+json.getString("nome"));
		Object[] texts = { lblPalavra, lblMestre };
		Object[] options = { "Sim", "Não" };
		int opcao = JOptionPane.showOptionDialog(null, texts, "Aceita Palavra?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(opcao == JOptionPane.YES_OPTION)
			jsonObject.put("verificado", true);
		else
			jsonObject.put("verificado", false);
		
		System.out.println(jsonObject);
	}
}
