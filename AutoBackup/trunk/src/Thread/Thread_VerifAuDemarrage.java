package Thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import lecture_ecriture.ReadFile;
import zip.OutilsZip;
import Dialogue.FEN_Princ;
import Dialogue.Fen_Download_FireBird;
import Dialogue.Fen_VerifMaj;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.RecupDate;
import Utilitaires.VariableEnvironement;
import accesBDD.ControleConnexion;

// protected TYPE xxx

public class Thread_VerifAuDemarrage extends Thread {
	// tte les decalration necessaire...
	protected JLabel operation;
	protected JTextField messageAFairePasser;
	protected JProgressBar progress;
	protected JFrame frame;

	// protected AnimatedPanel animation;
	// protected JPanel panelAnimation;

	/**
	 * Affiche les differentes etapes du demarrage du logiciel, verifie
	 * certaines choses.
	 * @param Fenetre -JFrame pour l'affichage des resultats
	 * @param operation_jLabel -JLabel message pour l'utilisateur
	 * @param jTextField -JTextField message pour l'utilisateur
	 * @param jProgressBar -JProgressBar pour la progression
	 */

	public Thread_VerifAuDemarrage(final JFrame Fenetre,
			final JLabel operation_jLabel, final JTextField jTextField,
			final JProgressBar jProgressBar) {

		// on met les equivalence ici

		operation = operation_jLabel;
		messageAFairePasser = jTextField;
		progress = jProgressBar;
		frame = Fenetre;

	}

	@Override
	public void run() {

		// *************** Verification de la taille de l'historique
		// ********************//
		// **Si il depasse 1Mo, on le compresse et on le range dans le dossier
		// "archive"*//
		// **************** avec un format de fichier historique_date.zip
		// ***************//
		final File fbTxt = new File("./fbserver.txt");
		if (fbTxt.exists() == true) {
			fbTxt.delete();
		}
		final File histo = new File("./historique.txt");
		final long tailleHisto = histo.length();
		System.out.println("taille histo :" + tailleHisto / 1024 + " Ko");
		if (tailleHisto >= 1000000) {// le fichier est superieur à 1Mo (1Million
			// d'octet)
			operation.setText("archivage de l'historique");
			System.out.println("taille histo :" + (tailleHisto / 1024) / 1024
					+ " Mo");
			try {
				final String Date = RecupDate.dateEtHeure();
				final File archive = new File("./archives");
				if (archive.exists() == false) {
					archive.mkdirs();
				}

				final String nomDuFichierGzip = "./archives/historique_" + Date
						+ ".txt.gzip";

				final String CheminHisto = "./historique.txt";

				final boolean succes = OutilsZip.gzip(CheminHisto,
						nomDuFichierGzip);
				if (succes == true) {
					// on a reussi a archiver l'historique, on efface le .txt
					histo.delete();
				}
				if (succes == false) {
					// on a pas reussi a archiver, on continu avec le meme
					// fichier
				}
			} catch (final FileNotFoundException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}

		}

		Historique.ecrire("------------ Lancement du logiciel ------------");

		// on verifie la version de java installé:
		final String version = System.getProperty("java.vm.version");

		Historique.ecrire("Version de JVM installé: " + version);

		// on verifie que les drivers my sql sont installé

		Historique.ecrire("Vérification de la présence de firebird");

		operation.setText("Vérification de la présence de firebird");
		// VariableEnvironement.VarEnvSystemTotal();
		final String ProgramFilesdir = VariableEnvironement
				.VarEnvSystem("ProgramFiles");
		final File fbServer = new File(ProgramFilesdir
				+ "\\Firebird\\Firebird_2_0\\bin\\fbserver.exe");

		if (fbServer.exists() == true) {

			Historique.ecrire("FireBird installé");

		}

		if (fbServer.exists() == false) {// le driver odbc n'est pas installé,
			// on l'installe

			Historique.ecrire("Firebird n'est pas installé");

			operation.setText("installation de Firebird server");

			new Fen_Download_FireBird();

			Historique.ecrire("Installation de Firebird terminée");

		}

		new Fen_VerifMaj();

		// On verifie que le server est bien lancé.
		operation.setText("Vérification du lancement de firebird");

		Historique.ecrire("Vérification du lancement de firebird");

		final String repTravail = GestionRepertoire.RecupRepTravail();
		final String cmdVerifFBServer = String
				.format("cmd /c tasklist.exe /FI \"IMAGENAME eq fbserver.exe\" /fo list > fbserver.txt");

		// on affiche la progression dans la progressBar
		progress.setValue(35);
		progress.setString(35 + " %");
		final Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmdVerifFBServer);
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		try {
			p.waitFor();
		} catch (final InterruptedException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}

		boolean fbTourne = ReadFile.FindOccurInFile(repTravail
				+ "\\fbserver.txt", "fbserver.exe");
		if (fbTourne == true) {
			operation.setText("Firebird est lancé");
			final File fbTexte = new File(GestionRepertoire.RecupRepTravail()
					+ "\\fbserver.txt");

			fbTexte.deleteOnExit();

			Historique.ecrire("Firebird est lancé");

		}
		if (fbTourne == false) {

			Historique
					.ecrire("Firebird ne tourne pas, on le lance manuellement");

			// on lance le service firebird
			final Runtime r1 = Runtime.getRuntime();
			Process p1 = null;
			try {
				p1 = r1
						.exec("cmd.exe /c net start FirebirdGuardianDefaultInstance");
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			try {
				p1.waitFor();
			} catch (final InterruptedException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			// on a fini de lancer firebird, on verifie qu'il soit bien lancé
			final Runtime r2 = Runtime.getRuntime();
			Process p2 = null;
			final String cmdVerifFBServer1 = String
					.format("cmd /c tasklist.exe /FI \"IMAGENAME eq fbserver.exe\" /fo list > fbserver.txt");

			try {
				p2 = r2.exec(cmdVerifFBServer1);
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			try {
				p2.waitFor();
			} catch (final InterruptedException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}

			fbTourne = ReadFile.FindOccurInFile(repTravail + "\\fbserver.txt",
					"fbserver.exe");
			if (fbTourne == false) {// il y a vraiment un pb avec firebird,
				// conseil de contacter le support
				JOptionPane.showMessageDialog(null,
						"Impossible de lancer le moteur de la base de données \n\r"
								+ "Veuillez contacter le développeur",
						"Erreur SQL", JOptionPane.WARNING_MESSAGE);

				Historique
						.ecrire("Impossible de lancer le moteur de la base de données \n\r"
								+ "Veuillez contacter le développeur");

				final File fbTexte = new File(GestionRepertoire
						.RecupRepTravail()
						+ "\\fbserver.txt");
				final boolean succesDelete = fbTexte.delete();
				if (succesDelete == false) {
					fbTexte.deleteOnExit();
				}

				System.exit(0);

			}

		}

		boolean result = false;
		try {
			result = ControleConnexion.getControleConnexion();
		} catch (final NullPointerException e) {

			Historique.ecrire("Message d'erreur: " + e);

		}
		if (result == true) {
			progress.setValue(100);
			progress.setString(100 + " %");
			operation.setText("Connexion Réussie");
			// animation.stop();
			frame.setVisible(false);
			FEN_Princ laFenetreMenu = null;
			try {
				laFenetreMenu = new FEN_Princ();
			} catch (final NumberFormatException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			laFenetreMenu.setVisible(true);

			Historique.ecrire("Connexion Réussie");

		}
		if (result == false) {// on est pas arrivé a se connecter a la base de
			// donnée mysql, on essaye de se connecter a la
			// base firebird
			JOptionPane.showMessageDialog(null,
					"Impossible de se connecter à la base", "Erreur",
					JOptionPane.WARNING_MESSAGE);
			// animation.stop();
			frame.setVisible(false);
			FEN_Princ laFenetreMenu = null;
			try {
				laFenetreMenu = new FEN_Princ();
			} catch (final NumberFormatException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			laFenetreMenu.setVisible(true);

			Historique.ecrire("Impossible de se connecter à la base");

		}

	}

	public static double floor(final double a, final int decimales,
			final double plus) {
		final double p = Math.pow(10.0, decimales);
		// return Math.floor((a*p) + 0.5) / p; // avec arrondi éventuel (sans
		// arrondi >>>> + 0.0
		return Math.floor((a * p) + plus) / p;
	}

}
