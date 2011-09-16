package Thread;

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;

import Utilitaires.Historique;

public class CopyPretedant {
	private int nbDerreur = 0;
	private int nbIgnoré = 0;

	public CopyPretedant(
			ListePretendant src,
			String dest,//
			final int nbTotal,
			final JProgressBar progressEnCours,
			final JProgressBar progressTotal,//
			final String RepRacineLocal, final JLabel label,
			final JList ListeExclusion, DefaultListModel ModelExclusion) {

		int tailleList = src.size();
		progressTotal.setValue(0);

		for (int i = 0; i < tailleList; i++) {

			Pretendant p = src.get(i);

			if (p.getFile().isFile()) {
				String destFile = dest
						+ p.getChemin().substring(RepRacineLocal.length(),
								p.getChemin().length());
				String destDossier = destFile.substring(0, destFile
						.lastIndexOf("\\"));
				File dossierDesti = new File(destDossier);
				if (!dossierDesti.exists()) {// si le repertoire de destination
												// n'existe pas, on le crée
					dossierDesti.mkdirs();
				}
				if (p.getEtatCopiable()) {

					boolean succes = copyAvecProgress(p.getFile(), new File(
							destFile), progressEnCours);
					if (succes == false) {// il y a eu un probleme pendant la
											// copie, on efface l'enregistrement
						// de la base de données.
						p.deleteEnregistrement();
						// nbDerreur++;
					}

					label.setText("Copie de " + (i + 1) + " fichier(s)  / sur "
							+ nbTotal + " au total");

				}// fin de p.getEtatCopiable()
				else {
					nbIgnoré++;
				}
			}// fin de p.isFile()
			else if (p.getFile().isDirectory()) {
				// System.out.println(p.getChemin() + " est un dossier");
			}

			int PourcentProgression = (100 * (i + 1)) / nbTotal;
			progressTotal.setValue(PourcentProgression);
			StringBuilder sb = new StringBuilder();
			sb.append("Copie Totale : ");
			sb.append(PourcentProgression);
			sb.append(" %");
			progressTotal.setString(sb.toString());

		}// fin de for/each
		progressTotal.setValue(100);
	}

	private boolean copyAvecProgress(File p_source, File p_destination,
			JProgressBar progressEnCours) {
		boolean resultat = false;
		long PourcentEnCours = 0;
		// Déclaration des stream d'entree sortie
		java.io.FileInputStream sourceFile = null;
		java.io.FileOutputStream destinationFile = null;

		try {

			// Ouverture des flux
			sourceFile = new java.io.FileInputStream(p_source);
			// Création du fichier de sortie :
			p_destination.createNewFile();
			destinationFile = new java.io.FileOutputStream(p_destination);
			long tailleTotale = p_source.length();

			// Lecture par segment de 0.5Mo
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLecture);
				long tailleEnCours = p_destination.length();
				PourcentEnCours = ((100 * (tailleEnCours + 1)) / tailleTotale);
				int Pourcent = (int) PourcentEnCours;
				progressEnCours.setValue(Pourcent);
				progressEnCours.setString(p_source + " : " + Pourcent + " %");
			}

			// si tout va bien
			resultat = true;

		} catch (Exception e) {
			// soit le fichier n'est pas trouvé, soit il est bloqué par un
			// processus ou autre
			// on sort tt de suite de la fonction de copie.
			Historique.ecrire("Le fichier " + p_source
					+ " ne sera pas copié pour la raison suivante:");
			Historique.ecrire(e.getMessage());
			return false;
		} finally {
			// Quelque soit on ferme les flux
			try {
				sourceFile.close();
				destinationFile.close();
			} catch (Exception e) {
				// Historique.ecrire("impossible de fermer le flux d'entrée " +
				// e);
				resultat = false;
			}

		}
		return (resultat);

	}

	public int getNbErreur() {
		return nbDerreur;
	}
}
