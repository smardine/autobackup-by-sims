package accesBDDAgathe;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

import lecture_ecriture.WriteFile;
import Utilitaires.Historique;
import Utilitaires.ParseString;

public class GestionDemandesAgathe {
	private static Vector<String> nomColonnesCommunes = new Vector<String>();
	private static Vector<String> nomColonnesMedecins = new Vector<String>();
	@SuppressWarnings("unused")
	private static Vector<String> nomColonnesMutuelles = new Vector<String>();
	@SuppressWarnings("unchecked")
	private static Vector<Vector> tabLignes = new Vector<Vector>();

	public static Vector<String> getNomColonnesCommunes() {
		return nomColonnesCommunes;
	}

	public static Vector<String> getNomColonnesMedecins() {
		return nomColonnesMedecins;
	}

	@SuppressWarnings("unchecked")
	public static Vector<Vector> getTabLignes() {
		return tabLignes;
	}

	public static Connection laConnexion = ControleConnexionAgathe
			.getConnexion();

	public DefaultTableModel model;

	/**
	 * Exporte les communes dans un fichier .csv
	 * @param requete -String la recherche effectuée (select * from....)
	 * @param model -DefaultTableModel affiche des infos pour l'utilisateur
	 * @return nb enregistrement la requete d'export est la suivante:"select CP,VILLE,KMPLA,KMMON,KMSKI from LOCALITE where (not CP is null or not VILLE is null) group by CP,VILLE,KMPLA,KMMON,KMSKI"
	 */

	/**
	 * Exporte les patients dans un fichier .csv
	 * @param requete -String la recherche effectuée (select * from....)
	 * @param model -DefaultTableModel affiche des infos pour l'utilisateur
	 * @return nb enregistrement la requete d'export est la suivante:
	 *         "SELECT ass.NUMSS,ass.NUMSSCLE," +
	 *         "cg.CODECINFO,cai.NUMDES,cai.CAIPAY," + "ass.PRENOM,ass.NOM," +
	 *         "pat.ORIGDROIT," + "pat.DATENAISS," + "pat.PRENOM,pat.NOM," +
	 *         "pat.RANGNAISS," + "pat.ORIGDROIT," + "pat.TXC1," + "pat.LIEN," +
	 *         "pat.VISVILLE,pat.VISCP," + "pat.TEL,pat.TEL2," +
	 *         "pat.VISAD1,pat.VISAD2," + "mut.IDAMC,mut.NUMADH" +
	 *         " from ASSURE ass " +
	 *         "left join PATIENT pat on (pat.CODEASSUR=ass.CODEASSUR)" +
	 *         "left join CAISSE cai on (pat.CODEC1=cai.CODECAISSE)" +
	 *         "left join CAISSEGEST cg on (cai.CODECGEST=cg.CODECGEST)" +
	 *         "left join MUTPAT mut on (mut.CODEPAT=pat.CODEPAT)"
	 */

	@SuppressWarnings("null")
	public static int RequeteExportDonnéesTable(final JProgressBar progress,
			final int nbTotal, final String requete,
			final String CheminExportEtNomDeFichier) {

		int nbClients = 0;
		int nbActu = 0;
		// String CheminExport =
		// GestionRepertoire.RecupRepTravail()+"\\export\\";
		final File fichierExport = new File(CheminExportEtNomDeFichier);
		if (fichierExport.exists() == true) {
			fichierExport.delete();
		}
		Statement state = null;
		try {
			state = laConnexion.createStatement();
			final ResultSet jeuEnregistrements = state.executeQuery(requete);
			final ResultSetMetaData infojeuEnregistrements = jeuEnregistrements
					.getMetaData();
			for (int i = 1; i <= infojeuEnregistrements.getColumnCount(); i++) {
				nomColonnesCommunes.add(infojeuEnregistrements
						.getColumnLabel(i));

			}
			String NomDesColonnes = nomColonnesCommunes.toString();
			NomDesColonnes = ParseString.removeCrochet(NomDesColonnes);
			// model.addElement(NomDesColonnes);

			try {
				WriteFile.WriteLine(NomDesColonnes, CheminExportEtNomDeFichier);
			} catch (final IOException e) {
				Historique.ecrire("impossible d'ecrire dans le fichier "
						+ CheminExportEtNomDeFichier);
			}

			while (jeuEnregistrements.next()) {
				nbActu++;
				final int Pourcentage = (100 * nbActu) / nbTotal;
				progress.setString(Pourcentage + " %");
				progress.setValue(Pourcentage);
				final Vector<String> ligne = new Vector<String>();
				for (int i = 1; i <= infojeuEnregistrements.getColumnCount(); i++) {
					String chaine_champ = jeuEnregistrements.getString(i);

					if ((chaine_champ == null)
							|| (chaine_champ.equals("") == true)) {
						if (chaine_champ == null) {
							ligne.add(chaine_champ.trim());
						} else {
							ligne.add("''''");
						}

					}
					if ((chaine_champ != null)
							&& chaine_champ.equals("") == false) {
						// chaine_champ=ParseString.removeFirstSpace(chaine_champ);
						chaine_champ = ParseString.removevirgule(chaine_champ
								.trim());
						ligne.add("''" + chaine_champ.trim() + "''");
					}
				}

				String Valeur = ligne.toString().trim();
				Valeur = ParseString.remplaceVirguleParPointVirgule(Valeur);
				Valeur = ParseString.removeCrochet(Valeur);
				Valeur = ParseString.removeApostrophe(Valeur);
				Valeur = ParseString.removeLineFeed(Valeur);
				Valeur = ParseString.removeCageReturn(Valeur);
				// model.addElement(Valeur);
				try {
					WriteFile.WriteLine(Valeur, CheminExportEtNomDeFichier);
				} catch (final IOException e) {

					Historique.ecrire("impossible d'ecrire dans le fichier "
							+ CheminExportEtNomDeFichier);
				}

				nbClients = nbClients + 1;
			}
			laConnexion.commit();
			jeuEnregistrements.close();
			state.close();
			if (nbClients == 0) {
				final boolean succes = fichierExport.delete();
				if (succes == false) {
					fichierExport.deleteOnExit();
				}
			}

		} catch (final SQLException e1) {

			JOptionPane.showMessageDialog(null, "Erreur : " + e1, "Erreur",
					JOptionPane.ERROR_MESSAGE);

			Historique.ecrire("Message d'erreur: " + e1
					+ "\n\r sur la requete : " + requete);

			try {

				state.close();
			} catch (final SQLException e) {
				Historique.ecrire("Message d'erreur: " + e1
						+ "\n\r sur la requete : " + requete);

			}
			return -1;
		}

		return nbClients;

	}

	/**
	 * On execute simplement une requete sur la base
	 * @param requete -String la requete à effectuer (delete, truncate...)
	 * @return vrai si ca a marché, sinon faux si il y a une erreur sql, on
	 *         inscrit l'erreur dans l'historique
	 */
	public static boolean executeRequete(final String requete) {
		// Connection laConnexion = ControleConnexion.getConnexion();

		Statement state = null;
		try {
			state = laConnexion.createStatement();
		} catch (final SQLException e) {

			Historique.ecrire("erreur a la connexion " + e);
			return false;
		}
		try {
			state.executeUpdate(requete);
		} catch (final SQLException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);

			try {
				state.close();
			} catch (final SQLException e1) {
				Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
			}
			return false;
		}
		try {
			laConnexion.commit();
			state.close();
		} catch (final SQLException e) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		return true;

		// ControleConnexion.fermetureSession();

	}

}
