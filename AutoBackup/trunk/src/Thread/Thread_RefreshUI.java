package Thread;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.RecupDate;
import accesBDD.GestionDemandes;

/**
 * @author smardine Classe gerant l'affichage des differents composants.
 */
public class Thread_RefreshUI extends Thread {

	private JLabel labelDate, labelVersion;
	private JList listFichier, listExclut, listSauvegarde;
	private DefaultListModel modelListFichier, modelListFichierExclut,
			modelListSauvegarde;
	private JTextField cheminSauvegarde, heure, minute;
	private JCheckBox envoiMail, arretMachine, lundi, mardi, mercredi, jeudi,
			vendredi, samedi, dimanche;

	public Thread_RefreshUI(JLabel p_labelDate, JLabel p_labelVersion,
			JList p_listFichier, DefaultListModel p_modelListFichier,
			JList p_listExclut, DefaultListModel p_modelListFichierExclut,
			JList p_listSauvegarde, DefaultListModel p_modelListSauvegarde,
			JTextField p_cheminSauvegarde, JTextField p_heure,
			JTextField p_minute, JCheckBox p_envoiMail,
			JCheckBox p_arretMachine, JCheckBox p_lundi, JCheckBox p_mardi,
			JCheckBox p_mercredi, JCheckBox p_jeudi, JCheckBox p_vendredi,
			JCheckBox p_samedi, JCheckBox p_dimanche) {
		labelDate = p_labelDate;
		labelVersion = p_labelVersion;
		listFichier = p_listFichier;
		listExclut = p_listExclut;
		listSauvegarde = p_listSauvegarde;
		modelListFichier = p_modelListFichier;
		modelListFichierExclut = p_modelListFichierExclut;
		modelListSauvegarde = p_modelListSauvegarde;
		cheminSauvegarde = p_cheminSauvegarde;
		envoiMail = p_envoiMail;
		arretMachine = p_arretMachine;
		heure = p_heure;
		minute = p_minute;
		lundi = p_lundi;
		mardi = p_mardi;
		mercredi = p_mercredi;
		jeudi = p_jeudi;
		vendredi = p_vendredi;
		samedi = p_samedi;
		dimanche = p_dimanche;
	}

	public void run() {
		// on affiche la date du jour
		labelDate.setText(RecupDate.dateSeulement());

		// on affiche la version actuelle
		String versionInstalle = recupVersion();
		labelVersion.setText(versionInstalle);

		// on met a jour les differentes listes (fichier, exclut, sauvegarde)
		refreshList();

		// on affiche le chemin de la sauvegarde
		String CheminSauvegarde = "";
		try {
			CheminSauvegarde = GestionDemandes
					.executeRequeteEtRetourne1Champ("SELECT * FROM CHEMIN_SAUVEGARDE");
		} catch (final SQLException e) {

			Historique.ecrire("Erreur SQL :" + e);
		}
		cheminSauvegarde.setText(CheminSauvegarde);

		// ///////////////////////////////////////////////////////////
		// on affiche les differents jour/horaires de sauvegarde ///
		// en vue d'un lancement par le raccourci "demarrage" ///
		// //////////////////////////////////////////////////////////
		refreshPlanif();
		refreshHoraires();

		refreshArretMachine();
		refreshEnvoiMail();
	}

	/**
	 * Doit on envoyer un mail au ST a la fermeture de l'appli?
	 */
	private void refreshEnvoiMail() {
		int etatEnvoiMail = 0;
		try {
			etatEnvoiMail = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT ENVOI_MAIL_ST FROM HORAIRE"));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (etatEnvoiMail == 1) {
			envoiMail.setSelected(true);
		}
	}

	/**
	 * Doit on arreter la machine a la fin de la sauvegarde (idependament du
	 * resultat de celle ci)?
	 */
	private void refreshArretMachine() {
		int etatArretMachine = 0;
		try {
			etatArretMachine = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT ARRET_MACHINE FROM HORAIRE"));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (etatArretMachine == 1) {
			arretMachine.setSelected(true);
		}
	}

	/**
	 * On rafraichi les horaires de lancement.
	 */
	private void refreshHoraires() {
		String heures = "";
		try {
			heures = GestionDemandes.executeRequeteEtRetourne1Champ(
					"SELECT HEURE FROM HORAIRE").toString();
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		String minutes = "";
		try {
			minutes = GestionDemandes.executeRequeteEtRetourne1Champ(
					"SELECT MINUTES FROM HORAIRE").toString();
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		heure.setText(heures);
		minute.setText(minutes);
	}

	/**
	 * 
	 */
	private void refreshPlanif() {
		boolean etatLundi = false, etatMardi = false, etatMercredi = false, etatJeudi = false, etatVendredi = false, etatSamedi = false, etatDimanche = false;
		String JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'LUNDI'";
		int lundiSelectionn� = 0;
		try {
			lundiSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (lundiSelectionn� == 1) {
			etatLundi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'MARDI'";
		int mardiSelectionn� = 0;
		try {
			mardiSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (mardiSelectionn� == 1) {
			etatMardi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'MERCREDI'";
		int mercrediSelectionn� = 0;
		try {
			mercrediSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (mercrediSelectionn� == 1) {
			etatMercredi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'JEUDI'";
		int jeudiSelectionn� = 0;
		try {
			jeudiSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (jeudiSelectionn� == 1) {
			etatJeudi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'VENDREDI'";
		int vendrediSelectionn� = 0;
		try {
			vendrediSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (vendrediSelectionn� == 1) {
			etatVendredi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'SAMEDI'";
		int samediSelectionn� = 0;
		try {
			samediSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (samediSelectionn� == 1) {
			etatSamedi = true;
		}
		JourSelectionn� = "SELECT IS_SELECTED FROM PLANIF where JOUR like 'DIMANCHE'";
		int dimancheSelectionn� = 0;
		try {
			dimancheSelectionn� = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(JourSelectionn�));
		} catch (final NumberFormatException e) {
			Historique.ecrire("Erreur de format de nombre :" + e);
		} catch (final SQLException e) {
			Historique.ecrire("Erreur SQL :" + e);
		}
		if (dimancheSelectionn� == 1) {
			etatDimanche = true;
		}

		lundi.setSelected(etatLundi);
		mardi.setSelected(etatMardi);
		mercredi.setSelected(etatMercredi);
		jeudi.setSelected(etatJeudi);
		vendredi.setSelected(etatVendredi);
		samedi.setSelected(etatSamedi);
		dimanche.setSelected(etatDimanche);
	}

	/**
	 * on raffraichit les differentes liste.
	 */
	private void refreshList() {
		int nbLigne = GestionDemandes.executeRequeteEtAfficheJList(
				"SELECT a.EMPLACEMENT_FICHIER FROM LISTE_FICHIER a",
				modelListFichier);

		forceAffichage(nbLigne, listFichier, modelListFichier);

		nbLigne = GestionDemandes.executeRequeteEtAfficheJList(
				"SELECT a.EMPLACEMENT_FICHIER FROM LISTE_EXCLUT a",
				modelListFichierExclut);
		forceAffichage(nbLigne, listExclut, modelListFichierExclut);

		nbLigne = GestionDemandes
				.executeRequeteEtAfficheJList(
						"SELECT a.EMPLACEMENT_SAUVEGARDE FROM SAUVEGARDE a ORDER by a.ID_SAUVEGARDE",
						modelListSauvegarde);
		forceAffichage(nbLigne, listSauvegarde, modelListSauvegarde);
	}

	/**
	 * @param nbLigne
	 * @param list
	 */
	private void forceAffichage(int nbLigne, final JList list,
			final DefaultListModel model) {
		if (nbLigne > 0) {
			Object element = list.getModel().getElementAt(0);
			model.remove(0);
			model.add(0, element);

		}
	}

	/**
	 * 
	 */
	private String recupVersion() {
		ini_Manager.ConfigMgt versionInstall�e = null;
		try {
			versionInstall�e = new ini_Manager.ConfigMgt("version.ini",
					GestionRepertoire.RecupRepTravail() + "\\IniFile\\", '[');
		} catch (final NullPointerException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		return versionInstall�e.getValeurDe("version");

	}

}
