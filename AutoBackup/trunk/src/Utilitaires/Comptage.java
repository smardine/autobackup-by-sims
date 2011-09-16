package Utilitaires;

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

public class Comptage {
	private int nbDossier = 0;
	private int nbFichier = 0;

	public Comptage(final String directoryPath, final JLabel nbdeFichier,
			final JList ListeExclusion, final DefaultListModel ModelExclusion) {
		LanceComptage(directoryPath, nbdeFichier, ListeExclusion,
				ModelExclusion);
	}

	private void LanceComptage(final String p_repSource,
			final JLabel nbdeFichier, final JList ListeExclusion,
			final DefaultListModel ModelExclusion) {

		final File directory = new File(p_repSource);
		int exclut = 0;

		for (int i = 0; i < ListeExclusion.getModel().getSize(); i++) {// on
			// verifie
			// si le
			// dossier/fichier
			// appartiens
			// a la
			// liste
			// d'exclusion
			final boolean FichierOuDossierExclu = p_repSource
					.equals(ModelExclusion.getElementAt(i).toString());
			if (FichierOuDossierExclu == true) {
				// le fichier ou le dossier fait parti de la liste d'exclision.
				exclut++;
			}
		}

		if (!directory.exists()) {
			// System.out.println("Le fichier/répertoire '"+directoryPath+"' n'existe pas");
		} else if (directory.isFile() && exclut == 0) {
			// System.out.println("Le chemin '"+directoryPath+"' correspond à un fichier et non à un répertoire");
			// c'est un dossier, le compteur de fichier est incrementé, celui
			// des dossier est decrémenté
			nbDossier--;
			nbFichier++;
			nbdeFichier.setText(nbFichier + " Fichier(s) à sauvegarder");

		} else {
			if (directory.isDirectory() && exclut == 0) {
				final File[] subfiles = directory.listFiles();

				if (subfiles != null && exclut == 0) {// si subfiles=null, c'est
					// que le dossier a des
					// restriction d'acces
					nbDossier = subfiles.length + nbDossier;
					for (int i = 0; i < subfiles.length; i++) {
						LanceComptage(subfiles[i].toString(), nbdeFichier,
								ListeExclusion, ModelExclusion);
					}
				}
			}
		}// fin de c'est un dossier, pas un fichier
	}

	public int getNbFichier() {
		return nbFichier;
	}

	public int getNbDossier() {
		return nbDossier;
	}

}
