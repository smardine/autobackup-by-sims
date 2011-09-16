package Dialogue;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import Utilitaires.GestionRepertoire;

public class Fen_Download_FlameRobin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JLabel jLabel = null;
	private JLabel operation_jLabel = null;
	private JProgressBar jProgressBar = null;
	private JLabel jLabel1 = null;

	/**
	 * This is the default constructor
	 */
	public Fen_Download_FlameRobin() {
		super();
		initialize();

		final long HeureDebut = System.currentTimeMillis();
		long HeureActuelle;
		final String urlsetup = "http://autobackup-by-sims.googlecode.com/files/flamerobin-0.9.2-1-setup.exe";
		int erreurOuverture = 0;
		InputStream input = null;
		String cheminFichier = "";
		File fichier;
		FileOutputStream writeFile = null;

		try {

			// //
			// //
			// //////////////////////////////////////////////////////////////////////////////////
			final URL url = new URL(urlsetup);
			final URLConnection connection = url.openConnection();
			final int fileLength = connection.getContentLength();

			if ((fileLength == -1) || (fileLength == 0)) {
				System.out.println("Invalide URL or file.");
				erreurOuverture++;
				// return false;
			}

			input = connection.getInputStream();
			String fileName = url.getFile().substring(
					url.getFile().lastIndexOf('/') + 1);
			if (fileName.contains("%20") == true) {
				fileName = fileName.replaceAll("%20", " ");
			}
			if (fileName.contains("&amp;") == true) {
				fileName = fileName.replaceAll("&amp;", " and ");
			}
			cheminFichier = GestionRepertoire.RecupRepTravail() + "\\"
					+ fileName;
			jLabel1.setText(" Fichier en cours : " + fileName);
			fichier = new File(cheminFichier);
			writeFile = new FileOutputStream(cheminFichier);
			// lecture par segment de 4Mo
			final byte[] buffer = new byte[4096 * 1024];
			int read;

			while ((read = input.read(buffer)) > 0) {
				writeFile.write(buffer, 0, read);
				final long TailleEncours = fichier.length();
				final int progressionEnCours = (int) ((100 * (TailleEncours + 1)) / fileLength);
				// int Pourcent=(int) progressionEnCours;

				HeureActuelle = System.currentTimeMillis();

				final long Vitesse = (TailleEncours / (HeureActuelle - HeureDebut));

				jLabel1
						.setText("Téléchargement du fichier , Vitesse Actuelle : "
								+ Vitesse + " Ko/s");
				jProgressBar.setValue(progressionEnCours);
				jProgressBar.setString(progressionEnCours + " %");

			}

			writeFile.flush();
		} catch (final IOException e) {
			System.out.println("Error while trying to download the file.");
			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		} finally {
			try {
				if (erreurOuverture == 0) {
					writeFile.close();
					input.close();
				}

			} catch (final IOException e) {
				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
		}

		final String cmdExecuteSetup = ("cmd /c \"" + cheminFichier + "\" /silent");// le
		// parametre
		// /silent
		// permet
		// de
		// lancer
		// le
		// setup
		// en
		// automatique

		final Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmdExecuteSetup);// //le parametre /silent permet de
			// lancer le setup en automatique
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		try {
			p.waitFor();
		} catch (final InterruptedException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}

		// p.waitFor();

	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(526, 233);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setPreferredSize(new Dimension(526, 233));
		this.setMaximumSize(new Dimension(526, 233));
		this.setMinimumSize(new Dimension(526, 233));
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/LogoPrincipal.png")));
		this.setLocationRelativeTo(null); // On centre la fenêtre sur l'écran
		this.setTitle("Téléchargement de FlameRobin");
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(150, 103, 356, 27));
			jLabel1.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel1.setText("");
			operation_jLabel = new JLabel();
			operation_jLabel.setBounds(new Rectangle(150, 62, 355, 26));
			operation_jLabel.setText(" Opération en cours");
			operation_jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(150, 18, 354, 26));
			jLabel
					.setText(" Veuillez patienter pendant le téléchargement de FlameRobin...");
			jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setFont(new Font("Candara", Font.BOLD, 12));
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(operation_jLabel, null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(jLabel1, null);
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
			jProgressBar.setBounds(new Rectangle(150, 152, 358, 26));
			jProgressBar.setStringPainted(true);
			jProgressBar.setFont(new Font("Candara", Font.PLAIN, 12));
		}
		return jProgressBar;
	}

}
