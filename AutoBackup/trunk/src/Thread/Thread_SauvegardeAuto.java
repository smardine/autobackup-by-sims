package Thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import zip.OutilsZip;
import Utilitaires.ComptageAuto;
import Utilitaires.ComptageAvantZip;
import Utilitaires.CopyAuto;
import Utilitaires.FileUtility;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.RecupDate;
import Utilitaires.VariableEnvironement;

// protected TYPE xxx

public class Thread_SauvegardeAuto extends Thread {
	// tte les decalration necessaire...
	protected JLabel MESSAGE_UTILISATEUR;
	protected String EMPLACEMENT;
	protected JProgressBar PROGRESSION, PROGRESSION_TOTALE;

	// protected AnimatedPanel animation;
	// protected JPanel panelAnimation;

	/**
	 * Affiche les differentes etapes du demarrage du logiciel, verifie
	 * certaines choses.
	 * @param actionListener
	 * @param Fenetre -JFrame pour l'affichage des resultats
	 * @param operation_jLabel -JLabel message pour l'utilisateur
	 * @param jTextField -JTextField message pour l'utilisateur
	 * @param jProgressBar -JProgressBar pour la progression
	 */

	public Thread_SauvegardeAuto(final String destination,
			final JProgressBar progressEnCours,
			final JProgressBar progressTotale, final JLabel operation) {

		// on met les equivalence ici

		MESSAGE_UTILISATEUR = operation;
		EMPLACEMENT = destination;
		PROGRESSION_TOTALE = progressTotale;
		PROGRESSION = progressEnCours;

	}

	@Override
	public void run() {

		MESSAGE_UTILISATEUR.setVisible(true);
		PROGRESSION.setVisible(true);

		final String WorkingDirectory = GestionRepertoire.RecupRepTravail();

		final String TempDirectory = VariableEnvironement.VarEnvSystem("TMP")
				+ "\\" + RecupDate.dateEtHeure();

		// On créer le repertoire temporaire dans lequel on va stocker les
		// différents fichiers.
		final File tempDirectory = new File(TempDirectory);
		if (tempDirectory.exists() == false) {// si le dossier n'existe pas
			tempDirectory.mkdirs();// on créer le dossier

			Historique.ecrire("Crétion du repertoire temporaire : "
					+ tempDirectory);

		}
		if (tempDirectory.canWrite() == false) {// si on ne peut pas ecrire dans
			// le repetoire temporaire
			JOptionPane.showMessageDialog(null,
					"Impossible de réaliser la sauvegarde\n\r"
							+ "La creation du dossier temporaire à echoué",
					"Sauvegarde Impossible", JOptionPane.ERROR_MESSAGE);

			Historique
					.ecrire("Pb lors de la sauvegarde : La creation du dossier temporaire à echoué");

			return;// on arrete la sauvegarde
		}

		boolean succesCopie = false;
		final ComptageAuto count = new ComptageAuto(WorkingDirectory,
				MESSAGE_UTILISATEUR);
		final int nbFichierACopier = count.getNbFichier();

		Historique.ecrire("Nombre de fichier à sauvegarder :"
				+ nbFichierACopier);

		CopyAuto save = null;
		try {
			save = new CopyAuto(WorkingDirectory, TempDirectory,
					nbFichierACopier, PROGRESSION, PROGRESSION_TOTALE,
					TempDirectory, MESSAGE_UTILISATEUR);
		} catch (final SQLException e2) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e2);

		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}

		final int nbderreur = save.getNbErreur();

		if (nbderreur != 0) {
			succesCopie = false;

			Historique
					.ecrire("Il y a eu des pb lors de la copie des fichiers vers le repertoire temporaire ");

			Historique
					.ecrire("Nombre d'erreur lors de la copie : " + nbderreur);

		} else {
			succesCopie = true;
		}

		boolean succesZip = false;

		if (succesCopie == true) {// si les copies ont fonctionnées

			Historique
					.ecrire("Copie des différents fichiers dans le repertoire temporaire reussie.");

			try {
				MESSAGE_UTILISATEUR.setText("Compression en cours");
				Historique.ecrire("Archivage du dossier : " + TempDirectory
						+ " vers le chemin : " + EMPLACEMENT);
				final ComptageAvantZip count1 = new ComptageAvantZip(
						TempDirectory, MESSAGE_UTILISATEUR);
				final int nbDeFIchierAZipper = count1.getNbFichier();
				succesZip = OutilsZip.zipDir(TempDirectory, EMPLACEMENT,
						nbDeFIchierAZipper, PROGRESSION_TOTALE, PROGRESSION,
						MESSAGE_UTILISATEUR);
			} catch (final FileNotFoundException e) {

				// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				System.out.println(e);
				JOptionPane.showMessageDialog(null,
						"Pb lors de la Sauvegarde automatique  : \n\r" + e,
						"Erreur", JOptionPane.ERROR_MESSAGE);
				PROGRESSION.setValue(0);
				PROGRESSION.setString(0 + " %");
				MESSAGE_UTILISATEUR.setText("");

				Historique.ecrire("Pb lors de la sauvegarde : " + e);

				System.exit(0);
				return;
			} catch (final IOException e) {

				// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				System.out.println(e);
				JOptionPane.showMessageDialog(null,
						"Pb lors de la Sauvegarde automatique  : \n\r" + e,
						"Erreur", JOptionPane.ERROR_MESSAGE);
				PROGRESSION.setValue(0);
				PROGRESSION.setString(0 + " %");
				MESSAGE_UTILISATEUR.setText("");

				Historique.ecrire("Pb lors de la sauvegarde : " + e);

				System.exit(0);
				return;
			}
			if (succesZip == true) {// l'archivage a reussi, on previens
				// l'utilisateur
				// JOptionPane.showMessageDialog(null,
				// "Sauvegarde automatique éffectuée avec succés",
				// "Sauvegarde Ok", JOptionPane.INFORMATION_MESSAGE);
				PROGRESSION.setValue(0);
				PROGRESSION.setString(0 + " %");
				MESSAGE_UTILISATEUR.setText("");

				Historique
						.ecrire("Sauvegarde automatique éffectuée avec succés");

				try {
					FileUtility.recursifDelete(tempDirectory);
				} catch (final IOException e) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					System.out.println(e);
				}

				System.exit(0);

			}

		} else {
			JOptionPane.showMessageDialog(null,
					"La copie des fichiers vers le repertoire temporaire à echouée.\n\r "
							+ "Abandon de la sauvegarde automatique",
					"Erreur lors de la sauvegarde", JOptionPane.ERROR_MESSAGE);
			PROGRESSION.setValue(0);
			PROGRESSION.setString(0 + " %");
			MESSAGE_UTILISATEUR.setText("");
			System.exit(0);
			return;
		}

	}

}
