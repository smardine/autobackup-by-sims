package Utilitaires;

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;

import Thread.ListePretendant;
import Thread.Pretendant;

public class CopyOfComptage {
	private int nbDossier = 0;
	private int nbFichier = 0;
	private ListePretendant listePretandant;
	private int idSauvegarde;
	private long dateRef;

	public CopyOfComptage(int p_idSauvegarde, long p_dateRef,
			String directoryPath, JProgressBar progressEnCours,
			JLabel nbdeFichier, JList ListeExclusion,
			DefaultListModel ModelExclusion) {

		idSauvegarde = p_idSauvegarde;
		dateRef = p_dateRef;
		listePretandant = new ListePretendant();
		// progressEnCours.setIndeterminate(true);
		progressEnCours
				.setString("Calcul du nombre de fichier(s) à sauvegarder en cours... merci de patienter.");
		LanceComptage(directoryPath, nbdeFichier, ListeExclusion,
				ModelExclusion);
		// progressEnCours.setIndeterminate(false);

	}

	private void LanceComptage(String string, JLabel nbdeFichier,
			final JList ListeExclusion, DefaultListModel ModelExclusion) {

		File directory = new File(string);
		int exclut = 0;

		for (int i = 0; i < ListeExclusion.getModel().getSize(); i++) {
			// on verifie si le dossier/fichier appartiens a la liste
			// d'exclusion
			boolean FichierOuDossierExclu = string.equals(ModelExclusion
					.getElementAt(i).toString());
			if (FichierOuDossierExclu == true) {
				// le fichier ou le dossier fait parti de la liste d'exclision.
				exclut++;
			}
		}

		if (!directory.exists()) {
			// System.out.println("Le fichier/répertoire '"+directoryPath+"' n'existe pas");
		} else if (directory.isFile() && exclut == 0) {
			// c'est un dossier, le compteur de fichier est incrementé, celui
			// des dossier est decrémenté
			nbDossier--;
			nbFichier++;
			// creation d'un pretedant a la copie
			Pretendant pretendant = new Pretendant(directory, idSauvegarde,
					dateRef);
			// ajout de ce pretendant a la liste des pretendants
			listePretandant.ajout(pretendant);
			nbdeFichier.setText(nbFichier + " Fichier(s) à sauvegarder");

		} else {
			if (directory.isDirectory() && exclut == 0) {
				File[] subfiles = directory.listFiles();

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

	public ListePretendant getListePretedant() {
		return listePretandant;
	}

}
