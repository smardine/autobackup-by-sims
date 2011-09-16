package Dialogue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Enum.EnChoixRecherche;
import Thread.Thread_RechSauvegarde;

public class Fen_RechSauvegarde extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null; // @jve:decl-index=0:visual-constraint="10,10"
	private JTextField Fichier_Dossier_A_Rechercher = null;
	private JLabel jLabel = null;
	private JButton Go = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	protected static DefaultTableModel modelTable;
	protected static DefaultComboBoxModel modelCombo;
	private JProgressBar jProgressBar = null;
	private JComboBox jComboBox = null;

	/**
	 * This is the default constructor
	 */
	public Fen_RechSauvegarde() {
		super();
		modelTable = new DefaultTableModel();
		modelCombo = new DefaultComboBoxModel(EnChoixRecherche.getListLib());
		initialize();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(1012, 435);
		this.setContentPane(getJContentPane());
		this
				.setTitle("Recherche d'un fichier ou d'un dossier dans les sauvegarde");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setPreferredSize(new Dimension(526, 233));
		this.setMaximumSize(new Dimension(526, 233));
		this.setMinimumSize(new Dimension(526, 233));
		this.setResizable(true);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/LogoPrincipal.png")));
		this.setLocationRelativeTo(null); // On centre la fenêtre sur l'écran
		this.setVisible(true);

	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(14, 14, 245, 29));
			jLabel.setText("Nom du fichier ou du dossier a rechercher");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setSize(new Dimension(664, 397));
			jContentPane.add(getFichier_Dossier_A_Rechercher(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getGo(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(getJComboBox(), null);

		}
		return jContentPane;
	}

	/**
	 * This method initializes Fichier_Dossier_A_Rechercher
	 * @return javax.swing.JTextField
	 */
	private JTextField getFichier_Dossier_A_Rechercher() {
		if (Fichier_Dossier_A_Rechercher == null) {
			Fichier_Dossier_A_Rechercher = new JTextField();
			Fichier_Dossier_A_Rechercher.setBounds(new Rectangle(12, 50, 477,
					27));
		}
		return Fichier_Dossier_A_Rechercher;
	}

	/**
	 * This method initializes Go
	 * @return javax.swing.JButton
	 */
	private JButton getGo() {
		if (Go == null) {
			Go = new JButton();
			Go.setBounds(new Rectangle(45, 96, 195, 37));
			Go.setText("Lancer la recherche");
			Go.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Thread_RechSauvegarde rech = new Thread_RechSauvegarde(
							jTable, modelTable, jComboBox,
							Fichier_Dossier_A_Rechercher, jProgressBar);
					rech.start();
				}
			});
		}
		return Go;
	}

	/**
	 * This method initializes jScrollPane
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(19, 166, 958, 198));
			jScrollPane.setViewportView(getJTable());

			jScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(modelTable);
			jTable.setAutoCreateRowSorter(true);
			jTable.setUpdateSelectionOnSort(true);
			// jTable.setAutoCreateColumnsFromModel(true);
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			// jTable.setFont(new Font("Courier New", Font.PLAIN, 14));
			jTable.setForeground(Color.black);
			jTable.setShowHorizontalLines(true);
			jTable.setShowGrid(false);

		}
		return jTable;
	}

	/**
	 * This method initializes jProgressBar
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(18, 143, 958, 19));
			jProgressBar.setStringPainted(true);
			jProgressBar.setString("");
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jComboBox
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(new Rectangle(511, 50, 147, 27));

			jComboBox.setModel(modelCombo);
		}
		return jComboBox;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
