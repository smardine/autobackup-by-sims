package Thread;

import java.io.File;
import java.sql.SQLException;

import Utilitaires.Historique;
import accesBDD.GestionDemandes;

/**
 * Cette classe permet de classer des fichiers Pretendant a la copie pour savoir
 * si ils seront a copier ou non
 * @author smardine
 */
public class Pretendant {
	private File fichier;
	private boolean isCopiable;
	private int idSauvegarde;
	private long dateRef;
	// private HashSet<String> listFichierPresentEnBase;
	private long dateDerniereModif;
	private String cheminSansAccent;

	/**
	 * Constructeur
	 * @param p_file - File - le fichier pretendant
	 * @param p_idSauvegarde - int - l'id de la sauvegarde associée
	 * @param p_dateRef - Date - une date de reference qui permet de savoir si
	 *            le fichier a besoin d'etre copié ou non
	 */
	public Pretendant(File p_file, int p_idSauvegarde, long p_dateRef) {
		idSauvegarde = p_idSauvegarde;
		dateRef = p_dateRef;
		fichier = p_file;
		cheminSansAccent = fichier.getAbsolutePath().replaceAll("'", "")
				.replaceAll("é", "e").replaceAll("è", "e").replaceAll("à", "a");
		if (fichier.isFile()) {
			try {
				isCopiable();
			} catch (NumberFormatException e) {
				Historique.ecrire("erreur conversion de champ :" + e);
			} catch (SQLException e) {
				Historique.ecrire("Erreur SQL :" + e);
			}
		}

	}

	/**
	 * Obetnir le chemin du fichier en enlevant les accents pour eviter les
	 * problemes de conversion lors de l'enregistrement en bdd
	 * @return le chemin, sans accent, sans apostrophe.
	 */
	public String getChemin() {
		return cheminSansAccent;
	}

	/**
	 * Le fichier est il de type "File"
	 * @return true si oui, false si non
	 */
	public File getFile() {
		return fichier;
	}

	public boolean getEtatCopiable() {
		return isCopiable;
	}

	private void isCopiable() throws NumberFormatException, SQLException {
		isCopiable = false;
		if (fichier.canRead() && fichier.canWrite()) {// le fichier est
			// accessible en
			// lecture/ecriture

			dateDerniereModif = fichier.lastModified();

			if (dateDerniereModif <= dateRef) {
				// la date de derniere modif du fichier est inferieure a la date
				// de ref, on ne fait pas la copie
				return;
			}

			else {// le fichier n'existe pas en base,ou bien sa date de derniere
				// modif est superieure a la date de ref on inserer son id
				// en base et on indique le fichier comme copiable.
				GestionDemandes
						.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
								+ idSauvegarde
								+ ","
								+ dateDerniereModif
								+ ",'"
								+ getChemin() + "')");
				isCopiable = true;
			}

		}

	}

	/**
	 * Si pour une raison ou une autre, le fichier ne peut pas etre copié, on
	 * efface son enregistrement de la base.
	 */
	public void deleteEnregistrement() {
		GestionDemandes
				.executeRequete("DELETE FROM FICHIER WHERE EMPLACEMENT_FICHIER='"
						+ getChemin() + "'");
	}

}
