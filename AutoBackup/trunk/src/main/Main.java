package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Thread.Thread_VerifAuDemarrage;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JLabel jLabel = null;
	private JLabel operation_jLabel = null;
	private JTextField jTextField = null;
	private JProgressBar jProgressBar = null;

	// private JPanel jPanel = null;
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(15, 35, 117, 119));
			jButton.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton.setIcon(new ImageIcon(getClass().getResource(
					"/LogoPrincipal.png")));
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(150, 107, 357, 26));
			jTextField.setEnabled(true);
			jTextField.setEditable(false);
			jTextField.setFont(new Font("Candara", Font.PLAIN, 12));
			jTextField.setVisible(false);
		}
		return jTextField;
	}

	/**
	 * This method initializes jProgressBar
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(150, 152, 358, 26));
			jProgressBar.setFont(new Font("Candara", Font.PLAIN, 12));
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jPanel
	 * @return javax.swing.JPanel
	 */
	/*
	 * private JPanel getJPanel() { if (jPanel == null) { jPanel = new JPanel();
	 * jPanel.setLayout(carder = new CardLayout()); jPanel.setBounds(new
	 * Rectangle(15, 35, 117, 119)); } return jPanel; }
	 */

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Main thisClass = new Main();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public Main() {
		super();
		initialize();
		final Thread_VerifAuDemarrage verifAuDemarrage = new Thread_VerifAuDemarrage(
				this, operation_jLabel, jTextField, jProgressBar);
		verifAuDemarrage.start();

	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(526, 233);
		this.setPreferredSize(new Dimension(526, 233));
		this.setMaximumSize(new Dimension(526, 233));
		this.setMinimumSize(new Dimension(526, 233));
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/LogoPrincipal.png")));
		this.setContentPane(getJContentPane());
		this.setLocationRelativeTo(null); // On centre la fenêtre sur l'écran
		this.setTitle("Démarrage");
		this.setVisible(true);

	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			operation_jLabel = new JLabel();
			operation_jLabel.setBounds(new Rectangle(150, 62, 355, 26));
			operation_jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			operation_jLabel.setText("Textmessage");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(150, 18, 164, 26));
			jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel.setText(" Opération (s) en cours:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setFont(new Font("Candara", Font.BOLD, 12));
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(operation_jLabel, null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJProgressBar(), null);

		}
		return jContentPane;
	}

}
