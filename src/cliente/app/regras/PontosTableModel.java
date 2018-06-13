package cliente.app.regras;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import cliente.app.util.Cliente;

public class PontosTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<Cliente> linhas;

	private final String[] colunas = new String[] { "Nome", "Pontos"};

	public PontosTableModel() {
		// TODO Auto-generated constructor stub
		this.linhas = new ArrayList<>();
	}

	public PontosTableModel(List<Cliente> clientes) {
		// TODO Auto-generated constructor stub
		this.linhas = new ArrayList<>(clientes);
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return colunas.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return linhas.size();
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return colunas[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return super.getColumnClass(columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Cliente cliente = linhas.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return cliente.getNome();
		case 1:
			return cliente.getPontos();
		default:
			// Isto não deveria acontecer...
			throw new IndexOutOfBoundsException("columnIndex out of bounds");
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addItem(Cliente c) {
		// Adiciona o registro.
		linhas.add(c);

		int ultimoIndice = getRowCount() - 1;

		fireTableRowsInserted(ultimoIndice, ultimoIndice);
	}
	
    /* Remove a linha especificada. */
    public void removeItem(int indiceLinha) {
        linhas.remove(indiceLinha);

        fireTableRowsDeleted(indiceLinha, indiceLinha);
    }

    /* Adiciona uma lista de Cliente ao final dos registros. */
    public void addListaClientes(List<Cliente> clientes) {
        // Pega o tamanho antigo da tabela.  
        int tamanhoAntigo = getRowCount();

        // Adiciona os registros.  
        linhas.addAll(clientes);

        fireTableRowsInserted(tamanhoAntigo, getRowCount() - 1);
    }

    /* Remove todos os registros. */
    public void limpar() {
        linhas.clear();

        fireTableDataChanged();
    }

    /* Verifica se este table model esta vazio. */
    public boolean isEmpty() {
        return linhas.isEmpty();
    }
}
