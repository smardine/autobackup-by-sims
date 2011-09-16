package Utilitaires;

import java.io.File;

import javax.swing.JLabel;

public class ComptageAuto {
	public int nbDossier = 0;
	public int nbFichier = 0;

	public ComptageAuto(final String directoryPath, final JLabel nbdeFichier) {
		LanceComptageAuto(directoryPath, nbdeFichier);
	}

	private void LanceComptageAuto(final String string, final JLabel nbdeFichier) {

		final File directory = new File(string);
		if (!directory.exists()) {
			// System.out.println("Le fichier/répertoire '"+directoryPath+"' n'existe pas");
		} else if (directory.isFile()) {
			// System.out.println("Le chemin '"+directoryPath+"' correspond à un fichier et non à un répertoire");
			// c'est un dossier, le compteur de fichier est incrementé, celui
			// des dossier est decrémenté
			nbDossier--;
			nbFichier++;
			nbdeFichier.setText(nbFichier + " Fichier(s) à sauvegarder");

		} else {
			if (directory.isDirectory()) {
				final File[] subfiles = directory.listFiles();

				if (subfiles != null) {// si subfiles=null, c'est que le dossier
					// a des restriction d'acces
					nbDossier = subfiles.length + nbDossier;
					for (int i = 0; i < subfiles.length; i++) {
						LanceComptageAuto(subfiles[i].toString(), nbdeFichier);
					}
				}

			}
		}
	}

	public int getNbFichier() {
		return nbFichier;
	}

	public int getNbDossier() {
		return nbDossier;
	}

}
