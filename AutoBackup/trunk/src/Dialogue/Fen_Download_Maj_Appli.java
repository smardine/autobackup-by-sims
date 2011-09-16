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
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.OpenWithDefaultViewer;
import accesBDD.GestionDemandes;
import accesBDDAgathe.GestionDemandesAgathe;

public class Fen_Download_Maj_Appli extends JFrame {

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
	public Fen_Download_Maj_Appli() {
		super();
		initialize();

		final long HeureDebut = System.currentTimeMillis();
		long HeureActuelle;
		final String urlsetup = "http://autobackup-by-sims.googlecode.com/files/setup_AutoBackup.exe";
		int erreurOuverture = 0;
		InputStream input = null;
		String cheminFichier = "";
		File fichier;
		FileOutputStream writeFile = null;

		try {

			// //
			// //
			// la maj est acceptée, il faut extraire le contenu de la base avant
			// de lancer le setup

			final String CheminExport = GestionRepertoire.RecupRepTravail()
					+ "\\export\\";
			// ExportBddDansSQL (CheminExport);
			int nbDerreur = 0;
			final File repExport = new File(CheminExport);
			if (repExport.exists() == false) {
				repExport.mkdirs();
			}
			jLabel1.setText("Export des Sauvegardes.");
			final String ScriptExportSauvegarde = "SELECT a.ID_SAUVEGARDE, a.DATE_SAUVEGARDE, a.EMPLACEMENT_SAUVEGARDE "
					+ " FROM SAUVEGARDE a";
			String Comptage = "SELECT COUNT (*) FROM SAUVEGARDE";
			int nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des sauvegardes.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des sauvegardes.");

			}

			final int nbenregistrement = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportSauvegarde, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\Sauvegarde.export");
			if (nbenregistrement == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export des sauvegardes.");

				Historique
						.ecrire("Il y a eu un problème à l'export des sauvegardes.");

			} else {
				jLabel1.setText(nbenregistrement
						+ " Sauvegarde(s) exportée(s).");

				Historique.ecrire(nbenregistrement
						+ " Sauvegarde(s) exportée(s).");

			}

			final String ScriptExportExclut = "SELECT a.EMPLACEMENT_FICHIER"
					+ " FROM LISTE_EXCLUT a";
			jLabel1.setText("Export des chemins exclus.");
			Comptage = "SELECT COUNT (*) FROM LISTE_EXCLUT";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des fichiers exclus.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des fichiers exclus.");

			}

			int nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportExclut, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\Exclut.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export des chemins exclus.");

				Historique
						.ecrire("Il y a eu un problème à l'export des chemins exclus.");

			} else {
				jLabel1.setText(nbenregistrement1
						+ " Chemin(s) exclu(s) exporté(s).");

				Historique.ecrire(nbenregistrement1
						+ " Chemin(s) exclu(s) exporté(s).");

			}

			final String ScriptExportFichier = "SELECT a.ID_SAUVEGARDE, a.DATE_FICHIER, a.EMPLACEMENT_FICHIER "
					+ "FROM FICHIER a";
			jLabel1.setText("Export des fichiers sauvegardés.");
			Comptage = "SELECT COUNT (*) FROM FICHIER";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des fichiers.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des fichiers.");

			}
			nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportFichier, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\Fichier.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export des fichiers.");

				Historique
						.ecrire("Il y a eu un problème à l'export des fichiers.");

			} else {
				jLabel1.setText(nbenregistrement1
						+ " Fichier(s) sauvegardé(s) exporté(s).");

				Historique.ecrire(nbenregistrement1
						+ " Fichier(s) sauvegardé(s) exporté(s).");

			}
			final String ScriptExportCheminSauvegarde = "SELECT a.EMPLACEMENT_DE_SAUVEGARDE FROM "
					+ "CHEMIN_SAUVEGARDE a";

			jLabel1.setText("Export des emplacements de sauvegarde.");
			Comptage = "SELECT COUNT (*) FROM CHEMIN_SAUVEGARDE";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des emplacements de sauvegarde.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage de la liste des emplacements de sauvegarde.");

			}
			nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportCheminSauvegarde, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\CheminSauvegarde.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export du chemin des sauvegardes.");

				Historique
						.ecrire("Il y a eu un problème à l'export du chemin des sauvegardes.");

			} else {
				jLabel1.setText(nbenregistrement1
						+ " Chemin(s) de sauvegarde exporté(s).");

				Historique.ecrire(nbenregistrement1
						+ " Chemin(s) de sauvegarde exporté(s).");

			}

			final String ScriptExportListeFichier = "SELECT a.EMPLACEMENT_FICHIER FROM "
					+ "LISTE_FICHIER a";
			jLabel1.setText("Export des emplacements à sauvegarder.");
			Comptage = "SELECT COUNT (*) FROM LISTE_FICHIER";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des emplacements à sauvegarder.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des emplacements à sauvegarder.");

			}

			nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportListeFichier, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\ListeFichier.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export des emplacements à sauvegarder.");

				Historique
						.ecrire("Il y a eu un problème à l'export des emplacements à sauvegarder.");

			} else {
				jLabel1.setText(nbenregistrement1
						+ " Liste des emplacements à sauvegarder exportés.");

				Historique.ecrire(nbenregistrement1
						+ " Liste des emplacements à sauvegarder exportés.");

			}

			final String ScriptExportListeHoraire = "SELECT a.HEURE, a.MINUTES, a.ARRET_MACHINE, a.ENVOI_MAIL_ST FROM "
					+ "HORAIRE a";
			jLabel1.setText("Export des horaires de sauvegarde.");

			Comptage = "SELECT COUNT (*) FROM HORAIRE";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des horaires.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des horaires.");

			}
			nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportListeHoraire, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\Horaire.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export de la liste des horaires.");

				Historique
						.ecrire("Il y a eu un problème à l'export de la liste des horaires.");

			} else {
				jLabel1.setText(nbenregistrement1 + " Horaires exportés.");

				Historique.ecrire(nbenregistrement1 + " Horaires exportés.");

			}

			final String ScriptExportListePlanif = "SELECT a.JOUR, a.IS_SELECTED FROM "
					+ "PLANIF a";
			jLabel1.setText("Export des planifications.");

			Comptage = "SELECT COUNT (*) FROM PLANIF";
			nbTotal = 0;
			try {
				nbTotal = Integer.parseInt(GestionDemandes
						.executeRequeteEtRetourne1Champ(Comptage));
			} catch (final NumberFormatException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des planifications.");

			} catch (final SQLException e1) {

				Historique
						.ecrire("Il y a eu un problème au comptage des planifications.");

			}

			nbenregistrement1 = GestionDemandesAgathe
					.RequeteExportDonnéesTable(jProgressBar, nbTotal,
							ScriptExportListePlanif, GestionRepertoire
									.RecupRepTravail()
									+ "\\export\\Planif.export");
			if (nbenregistrement1 == -1) {
				nbDerreur++;
				jLabel1
						.setText("Il y a eu un problème à l'export de la liste des planifications.");

				Historique
						.ecrire("Il y a eu un problème à l'export de la liste des planifications.");

			} else {
				jLabel1.setText(nbenregistrement1
						+ " Planification(s) exportée(s).");

				Historique.ecrire(nbenregistrement1
						+ " Planification(s) exportée(s).");

			}

			if (nbDerreur > 0) {
				/*
				 * JOptionPane.showMessageDialog(null,
				 * " veuillez consulter le fichier Historique.txt", "Attention",
				 * JOptionPane.WARNING_MESSAGE);
				 */

				Historique.ecrire("Export Terminé avec des erreurs");
				Historique
						.ecrire("----------------------------------------------");

				final int ouvrirHisto = JOptionPane
						.showConfirmDialog(
								null,
								"Il y a eu des problèmes lors de l'exportation des données.\n"
										+ "\n Voulez-vous consulter le fichier Historique.txt ?",
								"Erreur", JOptionPane.YES_NO_OPTION);
				if (ouvrirHisto == 0) {// on accepte
					OpenWithDefaultViewer.open(GestionRepertoire
							.RecupRepTravail()
							+ "/historique.txt");
				}

				jLabel1.setText("Export Terminé avec des erreurs");
				// jLabel1.setText("----------------------------------------------");

			} else {
				/*
				 * JOptionPane.showMessageDialog(null, "Export Terminé", "Ok",
				 * JOptionPane.INFORMATION_MESSAGE);
				 */
				jLabel1.setText("Export Terminé");
				// jLabel1.setText("----------------------------------------------");

				Historique.ecrire("Export Terminé");
				Historique
						.ecrire("----------------------------------------------");

			}

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

				final long Vitesse = (long) (TailleEncours / (HeureActuelle - HeureDebut));

				jLabel1
						.setText("Téléchargement de la nouvelle version , Vitesse Actuelle : "
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

		final Runtime r1 = Runtime.getRuntime();
		// Process p = null;

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
		try {
			r1.exec(cmdExecuteSetup);
		} catch (final IOException e) {

			JOptionPane
					.showMessageDialog(null, e, "Erreur au lancement du setup",
							JOptionPane.WARNING_MESSAGE);

			Historique.ecrire("Erreur au lancement du setup : " + e);

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		// p.waitFor();
		System.exit(0);

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
		this.setTitle("Mise à jour");
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
			jLabel.setText(" Veuillez patienter pendant la mise à jour...");
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
