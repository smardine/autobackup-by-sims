package Dialogue;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import Thread.Thread_SauvegardeAuto;
import Utilitaires.RecupDate;
import Utilitaires.VariableEnvironement;
import accesBDD.ControleConnexion;

public class Fen_Fermeture extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JLabel jLabel = null;
	private JLabel operation_jLabel = null;
	private JProgressBar jProgressBar = null;
	private JLabel jLabel1 = null;
	private JProgressBar jProgressBar1 = null;

	/**
	 * This is the default constructor
	 */
	public Fen_Fermeture() {
		super();
		initialize();

		ControleConnexion.fermetureSession();

		// ///////////////////////////////////////////////////
		// ////// SAUVEGARDE AUTOMATIQUE A LA FERMETURE ////
		// ///////////////////////////////////////////////////

		final String Date = RecupDate.dateEtHeure();
		final String FileName = Date + "_AutoBackup.zip";

		final String APPData = VariableEnvironement.VarEnvSystem("APPDATA")
				+ "\\AutoBackup\\";
		final File EmplacementArchive = new File(APPData + "\\archives\\");
		if (EmplacementArchive.exists() == false) {
			EmplacementArchive.mkdirs();
		}
		final String EMPLACEMENT = APPData + "\\archives\\" + FileName;

		final Thread_SauvegardeAuto save = new Thread_SauvegardeAuto(
				EMPLACEMENT, jProgressBar1, jProgressBar, jLabel1);
		save.start();

	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(526, 233);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setPreferredSize(new Dimension(526, 233));
		this.setMaximumSize(new Dimension(526, 233));
		this.setMinimumSize(new Dimension(526, 233));
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/LogoPrincipal.png")));
		this.setLocationRelativeTo(null); // On centre la fenêtre sur l'écran
		this.setTitle("Fermeture");
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(149, 88, 356, 27));
			jLabel1.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel1.setText("");
			operation_jLabel = new JLabel();
			operation_jLabel.setBounds(new Rectangle(150, 50, 355, 26));
			operation_jLabel.setText(" Opération en cours");
			operation_jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(150, 12, 354, 26));
			jLabel.setText(" Veuillez patienter pendant la fermeture...");
			jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setFont(new Font("Candara", Font.BOLD, 12));
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(operation_jLabel, null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJProgressBar1(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(15, 35, 117, 119));
			jButton.setIcon(new ImageIcon(getClass().getResource(
					"/LogoPrincipal.png")));
			jButton.setFont(new Font("Candara", Font.PLAIN, 12));
		}
		return jButton;
	}

	/**
	 * This method initializes jProgressBar
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(148, 158, 358, 26));
			jProgressBar.setStringPainted(true);
			jProgressBar.setFont(new Font("Candara", Font.PLAIN, 12));
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jProgressBar1
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar1() {
		if (jProgressBar1 == null) {
			jProgressBar1 = new JProgressBar();
			jProgressBar1.setBounds(new Rectangle(148, 127, 358, 26));
			jProgressBar1.setFont(new Font("Candara", Font.PLAIN, 12));
			jProgressBar1.setStringPainted(true);
		}
		return jProgressBar1;
	}

}
