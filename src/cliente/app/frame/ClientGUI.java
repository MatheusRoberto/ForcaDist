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

import cliente.app.regras.OnlinesTableModel;
import cliente.app.regras.PontosTableModel;
import cliente.app.service.ClienteService;
import cliente.app.util.Cliente;

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
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Socket socket;
	private ClienteService service;
	private boolean conectado = false;
	private ArrayList<JButton> teclado = new ArrayList<>();
	private OnlinesTableModel tableModel;
	private PontosTableModel pontosTableModel;
	private boolean mestre = false;
	private boolean vez = false;

	private JPanel contentPane;
	private JTextField txtIp;
	private JTextField txtPorta;
	private JTextField txtNome;
	private JButton btnConectar;
	private JButton btnDesconectar;
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
	private JTable tableOnlines;
	private JScrollPane scrollPane;
	private JButton btnChutar;
	private JPanel panelLetrasChutadas;
	private JLabel lblJogoIniciado;
	private JTextArea textAreaPalavrasChutadas;
	private JTextArea textAreaLetrasChutadas;
	private JPanel panelPontuacao;
	private JTable tablePontuacao;

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
						e.printStackTrace();
					}
					try {
						service.send(jsonObject);
					} catch (IOException e) {
						e.printStackTrace();
					}
					desconectar();
				}
			}
		});
		setTitle("Cliente Forca");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1216, 630);
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

		tableModel = new OnlinesTableModel();
		tableModel.limpar();

		pontosTableModel = new PontosTableModel();
		pontosTableModel.limpar();

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
						e.printStackTrace();
					}
					try {
						service.send(jsonObject);
					} catch (IOException e) {
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
		panelJogo.setBounds(22, 84, 633, 502);
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
					e1.printStackTrace();
				}
				try {
					service.send(jsonObject);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				tglbtnPronto.setEnabled(false);
			}
		});
		tglbtnPronto.setBounds(202, 11, 167, 25);
		panelJogo.add(tglbtnPronto);

		panelForca = new JPanel();
		panelForca.setBounds(12, 46, 609, 410);
		panelJogo.add(panelForca);
		panelForca.setLayout(null);

		panelPalavra = new JPanel();
		panelPalavra.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelPalavra.setBounds(12, 12, 585, 67);
		panelForca.add(panelPalavra);
		panelPalavra.setLayout(null);

		lblQuantidadeDeLetras = new JLabel("Quantidade de Letras: ");
		lblQuantidadeDeLetras.setBounds(12, 0, 221, 15);
		panelPalavra.add(lblQuantidadeDeLetras);

		lblPalavra = new JLabel("");
		lblPalavra.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPalavra.setHorizontalAlignment(SwingConstants.CENTER);
		lblPalavra.setBounds(12, 12, 561, 40);
		panelPalavra.add(lblPalavra);
		lblPalavra.setFont(new Font("Dialog", Font.BOLD, 22));

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "Vez de Jogar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(462, 81, 135, 317);
		panelForca.add(panel);

		listVez = new JList<String>();
		listVez.setEnabled(false);
		listVez.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listVez.setBounds(12, 31, 111, 274);
		panel.add(listVez);

		panelLetras = new JPanel();
		panelLetras.setBounds(12, 291, 446, 107);
		panelForca.add(panelLetras);

		JPanel panelPalavrasChutadas = new JPanel();
		panelPalavrasChutadas.setBorder(
				new TitledBorder(null, "Palavras Chutadas:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPalavrasChutadas.setBounds(12, 81, 446, 126);
		panelForca.add(panelPalavrasChutadas);
		panelPalavrasChutadas.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 20, 424, 94);
		panelPalavrasChutadas.add(scrollPane_1);

		textAreaPalavrasChutadas = new JTextArea();
		textAreaPalavrasChutadas.setEditable(false);
		scrollPane_1.setViewportView(textAreaPalavrasChutadas);

		panelLetrasChutadas = new JPanel();
		panelLetrasChutadas.setLayout(null);
		panelLetrasChutadas.setBorder(
				new TitledBorder(null, "Palavras Chutadas:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLetrasChutadas.setBounds(12, 213, 446, 75);
		panelForca.add(panelLetrasChutadas);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 20, 425, 50);
		panelLetrasChutadas.add(scrollPane_2);

		textAreaLetrasChutadas = new JTextArea();
		textAreaLetrasChutadas.setFont(new Font("Dialog", Font.PLAIN, 16));
		scrollPane_2.setViewportView(textAreaLetrasChutadas);
		textAreaLetrasChutadas.setWrapStyleWord(true);
		textAreaLetrasChutadas.setLineWrap(true);
		textAreaLetrasChutadas.setEditable(false);

		txtChute = new JTextField();
		txtChute.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
					if (!txtChute.getText().isEmpty())
						try {
							chutaPalavra(txtChute.getText());
						} catch (JSONException e) {
							e.printStackTrace();
						}
			}
		});
		txtChute.setEnabled(false);
		txtChute.setBounds(121, 468, 331, 19);
		panelJogo.add(txtChute);
		txtChute.setColumns(10);

		JLabel lblPalavraChute = new JLabel("Palavra Chute:");
		lblPalavraChute.setBounds(12, 470, 104, 15);
		panelJogo.add(lblPalavraChute);

		btnChutar = new JButton("Chutar");
		btnChutar.setEnabled(false);
		btnChutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!txtChute.getText().isEmpty())
					try {
						chutaPalavra(txtChute.getText());
					} catch (JSONException e) {
						e.printStackTrace();
					}
			}
		});
		btnChutar.setBounds(481, 465, 117, 25);
		panelJogo.add(btnChutar);

		lblJogoIniciado = new JLabel("Jogo não iniciado");
		lblJogoIniciado.setForeground(Color.RED);
		lblJogoIniciado.setFont(new Font("Dialog", Font.BOLD, 16));
		lblJogoIniciado.setBounds(414, 12, 207, 20);
		panelJogo.add(lblJogoIniciado);

		JPanel panelChat = new JPanel();
		panelChat.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelChat.setBounds(667, 157, 531, 429);
		contentPane.add(panelChat);
		panelChat.setLayout(null);

		JPanel panelOnlines = new JPanel();
		panelOnlines.setBounds(12, 12, 182, 353);
		panelChat.add(panelOnlines);
		panelOnlines.setBorder(new TitledBorder(null, "Onlines", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelOnlines.setLayout(null);

		tableOnlines = new JTable(tableModel);
		tableOnlines.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					tableOnlines.getSelectionModel().clearSelection();
				}
			}
		});
		tableOnlines.setCellSelectionEnabled(true);
		tableOnlines.setShowGrid(false);
		tableOnlines.setShowVerticalLines(false);
		tableOnlines.setBounds(12, 32, 158, 309);
		panelOnlines.add(tableOnlines);
		tableOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel panelText = new JPanel();
		panelText.setBounds(206, 12, 321, 353);
		panelChat.add(panelText);
		panelText.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(12, 12, 297, 341);
		panelText.add(scrollPane);

		txtrChat = new JTextArea();
		scrollPane.setViewportView(txtrChat);
		txtrChat.setLineWrap(true);
		txtrChat.setWrapStyleWord(true);
		txtrChat.setEditable(false);

		txtTextoChat = new JTextField();
		txtTextoChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						enviaChat();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		txtTextoChat.setEnabled(false);
		txtTextoChat.setBounds(14, 395, 370, 19);
		panelChat.add(txtTextoChat);
		txtTextoChat.setColumns(10);

		cbPrivado = new JCheckBox("Privado");
		cbPrivado.setEnabled(false);
		cbPrivado.setBounds(12, 369, 78, 23);
		panelChat.add(cbPrivado);

		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					enviaChat();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		btnEnviar.setEnabled(false);
		btnEnviar.setBounds(400, 392, 117, 25);
		btnEnviar.setEnabled(false);
		panelChat.add(btnEnviar);

		panelPontuacao = new JPanel();
		panelPontuacao.setBorder(
				new TitledBorder(null, "Pontua\u00E7\u00E3o:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPontuacao.setBounds(667, 12, 531, 142);
		contentPane.add(panelPontuacao);
		panelPontuacao.setLayout(null);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(12, 22, 507, 108);
		panelPontuacao.add(scrollPane_3);

		tablePontuacao = new JTable(pontosTableModel);
		scrollPane_3.setViewportView(tablePontuacao);
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
					JButton b = (JButton) e.getSource();
					try {
						chutaLetra(b.getText().charAt(0));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			});
			panelLetras.add(button);
			teclado.add(button);
		}
	}

	private void interfaceJogo(boolean b) {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.clear();
		listVez.setModel(listModel);

		if (!b) {
			this.lblQuantidadeDeLetras.setText("Quantidade de Letras: ");
			this.lblPalavra.setText("");
			this.lblJogoIniciado.setText("Jogo não iniciado");
			this.lblJogoIniciado.setForeground(Color.RED);
			this.txtrChat.setText("");
		}
		this.textAreaLetrasChutadas.setText("");
		this.textAreaPalavrasChutadas.setText("");
	}

	private void abreChute(boolean b) {
		//System.out.println("Abre chute: "+mestre);
		if (!mestre) {
			this.btnChutar.setEnabled(b);
			this.txtChute.setEnabled(b);
		}
	}

	private class ListenerSocket implements Runnable {

		private BufferedReader input;

		public ListenerSocket(Socket s) {
			try {
				this.input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			JSONObject jsonObject = null;
			try {
				String s;
				while ((s = input.readLine().trim()) != null) {
					jsonObject = new JSONObject(s);
					System.out.println("Recebeu: " + jsonObject.toString());
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
					case 9:
						jogoIniciado();
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
					case 17:
						recebeChutePalavra(jsonObject);
						break;
					case 18:
						verificaOpiniao(jsonObject);
						break;
					case 20:
						pontuacao(jsonObject);
						break;
					case 21:
						imprimeVez(jsonObject);
						break;
					case 22:
						alteraJogar(true);
						break;
					case 23:
						imprimeCampeao(jsonObject);
						break;
					}
				}
			} catch (IOException e) {
				// TODO: handle exception
				desconectar();
			} catch (JSONException | HeadlessException e) {
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
		this.txtChute.setEnabled(false);
		this.btnChutar.setEnabled(false);
		tableModel.limpar();
		interfaceJogo(false);
		alteraJogar(false);

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
		// this.listOnlines.setModel(listModel);
		tableModel.limpar();
		tableModel.addListaClientes(onlines);
		tableOnlines.setModel(tableModel);

	}

	public void jogoIniciado() {
		lblJogoIniciado.setText("Jogo iniciado");
		lblJogoIniciado.setForeground(Color.BLUE);
	}

	private void selecionaPalavra() throws JSONException {
		String palavra = new String();
		while (palavra == null || palavra.isEmpty()) {
			palavra = JOptionPane.showInputDialog(this, "Digite uma palavra para o jogo iniciar!");
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 11);
		jsonObject.put("palavra", palavra.toLowerCase());
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mestre = true;
	}

	private void imprimePalavra(int tamanho, JSONObject json) throws JSONException {
		String palavra = new String();
		String atual = lblPalavra.getText();
		atual = atual.replaceAll(" ", "");
		if (json != null) {
			for (int i = 0; i < tamanho; i++) {
				if (json.getString("palavra").charAt(i) == '*')
					if (atual.charAt(i) != '_')
						palavra = palavra + atual.charAt(i) + " ";
					else
						palavra = palavra + "_ ";
				else
					palavra = palavra + Character.toUpperCase(json.getString("palavra").charAt(i)) + " ";
			}
		} else {
			for (int i = 0; i < tamanho; i++) {
				palavra = palavra + "_ ";
			}
		}

		lblPalavra.setText(palavra);
	}

	private void jogar(JSONObject json) throws JSONException {
		lblQuantidadeDeLetras.setText("Quantidade de Letras: " + String.valueOf(json.getInt("TamanhoPalavra")));

		imprimePalavra(json.getInt("TamanhoPalavra"), null);

		interfaceJogo(true);

		imprimeVez(json);
		
		abreChute(true);
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
			if (cbPrivado.isSelected() && tableOnlines.getSelectedRowCount() > 0) {
				jsonObject.put("id", 6);
				jsonObject.put("mensagem", txtTextoChat.getText());
				jsonObject.put("destinatario", tableModel.getValueAt(tableOnlines.getSelectedRow(), 0));
				try {
					service.send(jsonObject);
					txtrChat.append("eu: " + txtTextoChat.getText() + "\n");
					txtTextoChat.setText("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (!cbPrivado.isSelected()) {
				jsonObject.put("id", 5);
				jsonObject.put("mensagem", txtTextoChat.getText());
				try {
					service.send(jsonObject);
					// txtrChat.append("eu: " + txtTextoChat.getText() + "\n");
					txtTextoChat.setText("");
				} catch (IOException e) {
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
		jsonObject.put("letra", String.valueOf(Character.toLowerCase(c)));
		alteraJogar(false);
		vez = true;
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void recebeLetra(JSONObject json) throws JSONException {
		// System.out.println(json.toString());
		// System.out.println(lblPalavra.getText().length());

		if (vez && !json.getBoolean("correto")) {
			JOptionPane.showMessageDialog(null, "Você errou!", "Errou!!!", JOptionPane.INFORMATION_MESSAGE);
			vez = false;
		}
		
		imprimePalavra(lblPalavra.getText().length() / 2, json);

		textAreaLetrasChutadas.setText(
				textAreaLetrasChutadas.getText() + " " + Character.toUpperCase(json.getString("letra").charAt(0)));
	}

	private void chutaPalavra(String s) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 16);
		jsonObject.put("palavra", s.toLowerCase());
		try {
			service.send(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		alteraJogar(false);
		abreChute(false);
	}

	private void recebeChutePalavra(JSONObject json) throws JSONException {
		textAreaPalavrasChutadas.setText(textAreaPalavrasChutadas.getText() + " " + json.getString("palavra"));
		imprimeVez(json);
	}

	private void verificaOpiniao(JSONObject json) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 19);
		JLabel lblPalavra = new JLabel("Palavra: " + json.getString("palavra"));
		JLabel lblMestre = new JLabel("Mestre: " + json.getString("nome"));
		Object[] texts = { lblPalavra, lblMestre };
		Object[] options = { "Sim", "Não" };
		int opcao = JOptionPane.showOptionDialog(null, texts, "Aceita Palavra?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (opcao == JOptionPane.YES_OPTION)
			jsonObject.put("verificado", true);
		else
			jsonObject.put("verificado", false);

		try {
			service.send(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void pontuacao(JSONObject json) throws HeadlessException, JSONException {
		mestre = false;
		if (json.getBoolean("palavraAceita"))
			JOptionPane.showMessageDialog(null, "Sim", "Palavra Aceita?", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(null, "Não", "Palavra Aceita?", JOptionPane.INFORMATION_MESSAGE);

		ArrayList<Cliente> listaclientes = new ArrayList<>();
		for (int i = 0; i < json.getJSONObject("clientes").getJSONArray("nomes").length(); i++) {
			Cliente c = new Cliente();
			c.setNome(json.getJSONObject("clientes").getJSONArray("nomes").get(i).toString());
			c.setPontos(Integer.valueOf(json.getJSONObject("clientes").getJSONArray("pontos").get(i).toString()));
			listaclientes.add(c);
		}

		pontosTableModel.limpar();
		pontosTableModel.addListaClientes(listaclientes);

	}

	private void alteraJogar(boolean b) {
		if (b) {
			JOptionPane.showMessageDialog(null, "É sua vez!", "Sua vez!", JOptionPane.INFORMATION_MESSAGE);
			this.requestFocus();
		}
		for (JButton jButton : teclado) {
			jButton.setEnabled(b);
		}
	}
	
	private void imprimeCampeao(JSONObject json) throws HeadlessException, JSONException {
		JOptionPane.showMessageDialog(null, "Campeão!", json.getString("campeao"), JOptionPane.INFORMATION_MESSAGE);
	}
}
