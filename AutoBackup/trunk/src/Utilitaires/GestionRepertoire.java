package Utilitaires;

import java.io.File;

import javax.swing.JFileChooser;

public class GestionRepertoire {

	/**
	 * Recupere le repertoire de travail de l'application
	 * @return Le repertoire de travail -String
	 */
	public static String RecupRepTravail() {
		final File dir = new File(".");
		String sAppliDir = new String();
		try {
			sAppliDir = dir.getCanonicalPath();
		} catch (final Exception e1) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
		}
		return sAppliDir;
	}

	/**
	 * Affiche une fenetre permettant d'ouvrir un fichier
	 * @return le chemin complet -String
	 */
	public static String openFile() {
		final JFileChooser chooser = new JFileChooser();
		final int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
		}
		return chooser.getSelectedFile().getName();

	}

}
