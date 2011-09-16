package Thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import lecture_ecriture.WriteFile;
import zip.OutilsZip;
import Utilitaires.ComptageAvantZip;
import Utilitaires.CopyOfComptage;
import Utilitaires.FileUtility;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.VariableEnvironement;
import accesBDD.GestionDemandes;

// protected TYPE xxx

public class CopyOfThread_Sauvegarde extends Thread {
	// tte les decalration necessaire...
	protected JLabel MESSAGE_UTILISATEUR;
	protected String EMPLACEMENT;
	protected JProgressBar PROGRESSION_EN_COURS, PROGRESSION_TOTALE;
	protected JList LISTE_SAUVEGARDE, LISTE_EXCLUSION;
	protected DefaultListModel MODEL_SAUVEGARDE, MODEL_EXCLUSION;
	int nbFichierACopier = 0;
	protected JButton PAUSE, GO, STOP, REFRESH, SAVE_OK, SAVE_NOK;
	private volatile boolean pause = false;
	private JCheckBox STOP_MACHINE;

	private File encours;
	private File tempDirectory;

	/**
	 * Affiche les differentes etapes du demarrage du logiciel, verifie
	 * certaines choses.
	 * @param actionListener
	 * @param Fenetre -JFrame pour l'affichage des resultats
	 * @param operation_jLabel -JLabel message pour l'utilisateur
	 * @param jTextField -JTextField message pour l'utilisateur
	 * @param jProgressBar -JProgressBar pour la progression
	 */

	public CopyOfThread_Sauvegarde(String destination,
			JProgressBar progressEnCours, JProgressBar progressTotal,
			JLabel operation, JList ListeSauvegarde,
			DefaultListModel ModelSauvegarde, JList ListeExclu,
			DefaultListModel ModelExclu, JButton Pause, JButton Go,
			JButton Stop, JButton Refresh, JButton Save_Ok, JButton Save_Nok,
			JCheckBox Stop_Machine) {

		// on met les equivalence ici

		MESSAGE_UTILISATEUR = operation;
		EMPLACEMENT = destination;
		PROGRESSION_EN_COURS = progressEnCours;
		PROGRESSION_TOTALE = progressTotal;
		LISTE_SAUVEGARDE = ListeSauvegarde;
		MODEL_SAUVEGARDE = ModelSauvegarde;
		LISTE_EXCLUSION = ListeExclu;
		MODEL_EXCLUSION = ModelExclu;
		PAUSE = Pause;
		GO = Go;
		STOP = Stop;
		REFRESH = Refresh;
		SAVE_OK = Save_Ok;
		SAVE_NOK = Save_Nok;
		STOP_MACHINE = Stop_Machine;

	}

	public void run() {

		encours = new File(GestionRepertoire.RecupRepTravail() + "/enCours.txt");
		try {
			encours.createNewFile();
		} catch (IOException e3) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e3);
		}
		PROGRESSION_EN_COURS.setValue(0);
		PROGRESSION_EN_COURS.setString(0 + " %");
		PROGRESSION_TOTALE.setValue(0);
		PROGRESSION_TOTALE.setString(0 + " %");
		MESSAGE_UTILISATEUR.setText("");

		// on crée la reference de sauvegarde dans la BDD
		long dateDuJour = System.currentTimeMillis();

		Date actuelle = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String DateActuelle = dateFormat.format(actuelle);

		String FileName = DateActuelle + "_AutoBackup.zip";
		EMPLACEMENT = EMPLACEMENT + "\\" + FileName;

		GestionDemandes
				.executeRequete("INSERT INTO SAUVEGARDE (DATE_SAUVEGARDE, EMPLACEMENT_SAUVEGARDE) VALUES ("
						+ dateDuJour + ",'" + EMPLACEMENT + "')");
		int ID_SAUVEGARDE = 0;
		try {
			ID_SAUVEGARDE = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
		} catch (NumberFormatException e2) {
			System.out.println(e2);
		} catch (SQLException e2) {

			System.out.println(e2);
		}

		int nbDeLigne = LISTE_SAUVEGARDE.getModel().getSize();
		String TempDirectory = VariableEnvironement.VarEnvSystem("TMP") + "\\"
				+ DateActuelle;
		boolean succesCopie = false;

		MODEL_EXCLUSION.addElement(TempDirectory);// on force l'ajout d'un
		// filtre d'exclussion sur
		// le repertoire temporaire
		// de sauvegarde

		// On créer le repertoire temporaire dans lequel on va stocker les
		// différents fichiers.
		tempDirectory = new File(TempDirectory);
		if (tempDirectory.exists() == false) {// si le dossier n'existe pas
			tempDirectory.mkdirs();// on créer le dossier
			Historique.ecrire("Création du repertoire temporaire : "
					+ tempDirectory);

		}
		if (tempDirectory.canWrite() == false) {// si on ne peut pas ecrire dans
			// le repetoire temporaire
			JOptionPane.showMessageDialog(null,
					"Impossible de réaliser la sauvegarde\n\r"
							+ "La creation du dossier temporaire à echoué",
					"Sauvegarde Impossible", JOptionPane.ERROR_MESSAGE);
			gestionErreur(encours, tempDirectory, false);
			Historique
					.ecrire("Pb lors de la sauvegarde : La creation du dossier temporaire à echoué");
			return;// on arrete la sauvegarde
		}

		for (int i = 0; i < nbDeLigne; i++) {
			LISTE_SAUVEGARDE.setSelectedIndex(i);
			final int index = i;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					public void run() {
						// on affiche la progression dans la progressBar
						LISTE_SAUVEGARDE.ensureIndexIsVisible(index);
					}

				});
			} catch (InterruptedException e) {
				Historique.ecrire("Message d'erreur: " + e);
			} catch (InvocationTargetException e) {
				Historique.ecrire("Message d'erreur: " + e);
			}

			String WorkingDirectory = MODEL_SAUVEGARDE.getElementAt(i)
					.toString();
			String NomRepertoire = WorkingDirectory.substring(WorkingDirectory
					.lastIndexOf("\\"), WorkingDirectory.length());
			File Actu = new File(WorkingDirectory);

			if (Actu.isDirectory() == true && Actu.exists()) {// c'est un
																// dossier et il
																// existe!!!

				// String requete =
				// "select a.EMPLACEMENT_FICHIER, a.DATE_FICHIER from FICHIER a where a.EMPLACEMENT_FICHIER like '"+WorkingDirectory+"%' group by a.EMPLACEMENT_FICHIER, a.DATE_FICHIER";
				// String requete2 =
				// "select a.EMPLACEMENT_FICHIER from FICHIER a where a.EMPLACEMENT_FICHIER like '"+WorkingDirectory+"%' group by a.EMPLACEMENT_FICHIER";

				// HashSet<String> lst =
				// GestionDemandes.executeRequeteEtRetourneUneListe2(requete2);

				boolean isPresent = isFichierPresentEnBase(WorkingDirectory);
				long dateRef = getDateRef(isPresent, WorkingDirectory);

				// String requete =
				// "SELECT a.EMPLACEMENT_FICHIER,a.DATE_FICHIER " +
				// "FROM FICHIER a " +
				// "WHERE " +
				// "a.EMPLACEMENT_FICHIER like '"+WorkingDirectory+"%'" +
				// "and a.DATE_FICHIER in " +
				// "(select max (b.DATE_FICHIER) from FICHIER b where b.EMPLACEMENT_FICHIER = a.EMPLACEMENT_FICHIER"
				// +
				// " group by b.EMPLACEMENT_FICHIER)";
				//				
				// ArrayList <ObjetPair<String,Long>> listeFichierEnBase =
				// GestionDemandes.executeRequeteEtRetourneUneListe(requete); //
				long tempDebut = System.currentTimeMillis(); // 

				CopyOfComptage count = new CopyOfComptage(ID_SAUVEGARDE,
						dateRef, WorkingDirectory, PROGRESSION_EN_COURS,
						MESSAGE_UTILISATEUR, LISTE_EXCLUSION, MODEL_EXCLUSION);
				nbFichierACopier = count.getListePretedant().size()
						+ nbFichierACopier;
				MESSAGE_UTILISATEUR.setText("Copie de " + 0
						+ " fichier(s)  / sur " + nbFichierACopier
						+ " au total");
				Historique.ecrire("Dans le repertoire : " + WorkingDirectory
						+ " ,nombre de fichier à sauvegarder : "
						+ nbFichierACopier);

				if (pause) {
					waitThread();
				}
				CopyPretedant save = new CopyPretedant(count
						.getListePretedant(), TempDirectory + "\\"
						+ NomRepertoire, nbFichierACopier,
						PROGRESSION_EN_COURS, PROGRESSION_TOTALE,
						WorkingDirectory, MESSAGE_UTILISATEUR, LISTE_EXCLUSION,
						MODEL_EXCLUSION);
				try {
					VerifDossierVideEtSupprSiVide(new File(TempDirectory + "\\"
							+ NomRepertoire));
				} catch (IOException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}
				// on recompte le nb de ligne dans la liste des sauvegardes
				// au cas ou l'utilisateur aurait rajouté des choses
				nbDeLigne = LISTE_SAUVEGARDE.getModel().getSize();
				int nbderreur = save.getNbErreur();

				if (nbderreur != 0) {
					succesCopie = false;
					gestionErreur(encours, tempDirectory, false);
					return;
				} else {
					succesCopie = true;
				}
				long tempActuel = System.currentTimeMillis();
				String tempPasse = calculTempPasse(tempActuel, tempDebut);
				Historique.ecrire("temps passé a copier le repertoire "
						+ Actu.getPath() + " : " + tempPasse);
			}
			if (Actu.isFile() == true && Actu.exists()) {// c'est un fichier et
				// il existe!!!
				// boolean succesCopie = false;
				nbFichierACopier = 1 + nbFichierACopier;
				long tailleSource = Actu.length();
				MESSAGE_UTILISATEUR.setText("Copie de " + nbFichierACopier
						+ " fichier(s)  / sur " + nbFichierACopier
						+ " au total");
				File tempo = new File(TempDirectory + "\\" + NomRepertoire);

				String cheminDuFichier = Actu.getAbsolutePath();
				long dateDuFichierOriginal = Actu.lastModified();
				long dateDuFIchierEnBase = 0;

				int nbEnregistrementPresent = 0;
				try {
					String cheminDuFichierSansAccent = cheminDuFichier
							.replaceAll("'", "");
					nbEnregistrementPresent = Integer
							.parseInt(GestionDemandes
									.executeRequeteEtRetourne1Champ("SELECT count(EMPLACEMENT_FICHIER) FROM FICHIER WHERE EMPLACEMENT_FICHIER = '"
											+ cheminDuFichierSansAccent + "'"));
				} catch (NumberFormatException e1) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
				} catch (SQLException e1) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
				}
				if (nbEnregistrementPresent != 0) {// le chemin du fichier est
					// bien en base
					// reste a verifier la date enregistrée en base et a la
					// comptarer a celle du fichier present sur le dd
					try {
						String cheminDuFichierSansAccent = cheminDuFichier
								.replaceAll("'", "");
						dateDuFIchierEnBase = Long
								.parseLong(GestionDemandes
										.executeRequeteEtRetourne1Champ("SELECT a.DATE_FICHIER FROM FICHIER a where  a.EMPLACEMENT_FICHIER= '"
												+ cheminDuFichierSansAccent
												+ "'"));
					} catch (NumberFormatException e) {

						Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					} catch (SQLException e) {

						Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					}

					if (dateDuFichierOriginal == dateDuFIchierEnBase) {
						// les date sont identique, on ne copie pas

						PROGRESSION_EN_COURS
								.setString(cheminDuFichier.toString()
										+ " ignoré car non modifié depuis la dernière sauvegarde");
						succesCopie = true;
					} else {

						// les date de modif sont différentes, on lance la copie

						String SRCSansAccent = cheminDuFichier.replaceAll("'",
								"").toString().trim();

						GestionDemandes
								.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
										+ ID_SAUVEGARDE
										+ ","
										+ dateDuFichierOriginal
										+ ",'"
										+ SRCSansAccent + "')");
						if (tailleSource > 1000000) {// si superieur à 15Mo
							succesCopie = copyAvecProgress(Actu, tempo,
									PROGRESSION_EN_COURS); // on utilise la
							// fonction de copie
							// standard
						} else {
							succesCopie = copyAvecProgressNIO(Actu, tempo,
									PROGRESSION_EN_COURS);// on utilise les
							// channel (+rapide)
						}

					}
				} else {// le chemin du fichier n'est pas rentré en base, on
					// rentre le chemin du fichier ainsi que la date de
					// derniere modif du fichier
					// il nous faut l'ID_SAUVEGARDE qui est le liens entre les
					// tables FICHIER et SAUVEGARDE

					String SRCSansAccent = cheminDuFichier.replaceAll("'", "")
							.toString().trim();
					GestionDemandes
							.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
									+ ID_SAUVEGARDE
									+ ","
									+ dateDuFichierOriginal
									+ ",'"
									+ SRCSansAccent + "')");

					long tailleSource1 = Actu.length();
					if (tailleSource1 > 15000000) {// si superieur à 15Mo
						succesCopie = copyAvecProgress(Actu, tempo,
								PROGRESSION_EN_COURS); // on utilise la fonction
						// de copie standard
					} else {
						succesCopie = copyAvecProgressNIO(Actu, tempo,
								PROGRESSION_EN_COURS);// on utilise les channel
						// (+rapide)
					}

					if (succesCopie == false) {

						Historique
								.ecrire("Erreur lors de la copie du fichier : "
										+ Actu.getAbsolutePath() + " vers : "
										+ tempo.getAbsolutePath());

					}

				}

				if (!succesCopie) {
					gestionErreur(encours, tempDirectory, false);
					return;
				}
			}
			if ((!Actu.isDirectory() && !Actu.isFile()) || !Actu.exists()) {
				// ce n'est ni un fichier, ni un dossier OU n'existe pas
				String RepActu = Actu.getAbsolutePath();
				JOptionPane
						.showMessageDialog(
								null,
								"Impossible de réaliser la sauvegarde\n\r"
										+ "Le repertoire/fichier: "
										+ RepActu
										+ " est introuvable.\n\r Veuillez verifier la liste des dossiers/fichiers a sauvegarder",
								"Sauvegarde Impossible",
								JOptionPane.ERROR_MESSAGE);

				gestionErreur(encours, tempDirectory, false);

				Historique
						.ecrire("Pb lors de la sauvegarde : Le repertoire/fichier: "
								+ RepActu
								+ " est introuvable.\n\r Veuillez verifier la liste des dossiers/fichiers a sauvegarder");
				long tempActuel = System.currentTimeMillis();
				String tempPasse = calculTempPasse(tempActuel, dateDuJour);
				Historique.ecrire("temps passé faire la sauvegarde : "
						+ tempPasse);
				return;
			}

		}

		if (pause) {
			waitThread();
		}

		boolean succesZip = false;

		if (succesCopie == true) {// si les copies ont fonctionnées
			Historique
					.ecrire("Copie des différents fichiers dans le repertoire temporaire reussie.");

			ComptageAvantZip count = new ComptageAvantZip(TempDirectory,
					MESSAGE_UTILISATEUR);
			int nbDeFIchierAZipper = count.getNbFichier();
			if (nbDeFIchierAZipper == 0) {// il n'y a aucun fichier a compresser
				// => on sort de la sauvegarde tt de
				// suite, c'est fini!!!
				GestionDemandes
						.executeRequete("DELETE FROM SAUVEGARDE WHERE DATE_SAUVEGARDE="
								+ dateDuJour
								+ " and  EMPLACEMENT_SAUVEGARDE='"
								+ EMPLACEMENT + "'");

				PROGRESSION_EN_COURS.setValue(0);
				PROGRESSION_EN_COURS.setString(0 + " %");
				PROGRESSION_TOTALE.setValue(0);
				PROGRESSION_TOTALE.setString(0 + " %");
				MESSAGE_UTILISATEUR.setText("");

				REFRESH.setEnabled(true);
				STOP.setEnabled(false);
				GO.setEnabled(true);
				PAUSE.setEnabled(false);
				SAVE_OK.setVisible(true);

				gestionErreur(encours, tempDirectory, true);

				Historique
						.ecrire("Sauvegarde éffectuée avec succés,pas de fichier modifié à sauvegarder");

				long tempActuel = System.currentTimeMillis();
				String tempPasse = calculTempPasse(tempActuel, dateDuJour);
				Historique.ecrire("temps passé faire la sauvegarde : "
						+ tempPasse);
				return;
			}
			if (nbDeFIchierAZipper != 0) {
				try {
					MESSAGE_UTILISATEUR.setText("Compression en cours");
					Historique.ecrire("Archivage du dossier : " + TempDirectory
							+ " vers le chemin : " + EMPLACEMENT);
					succesZip = OutilsZip.zipDir(TempDirectory, EMPLACEMENT,
							nbDeFIchierAZipper, PROGRESSION_TOTALE,
							PROGRESSION_EN_COURS, MESSAGE_UTILISATEUR);
				} catch (FileNotFoundException e) {
					System.out.println(e);
					JOptionPane.showMessageDialog(null,
							"Pb lors de la sauvegarde : \n\r" + e, "Erreur",
							JOptionPane.ERROR_MESSAGE);

					gestionErreur(encours, tempDirectory, false);

					Historique.ecrire("Pb lors de la sauvegarde : " + e);
					long tempActuel = System.currentTimeMillis();
					String tempPasse = calculTempPasse(tempActuel, dateDuJour);
					Historique.ecrire("temps passé faire la sauvegarde : "
							+ tempPasse);
					return;

				} catch (IOException e) {
					System.out.println(e);
					JOptionPane.showMessageDialog(null,
							"Pb lors de la sauvegarde : \n\r" + e, "Erreur",
							JOptionPane.ERROR_MESSAGE);
					gestionErreur(encours, tempDirectory, false);
					Historique.ecrire("Pb lors de la sauvegarde : " + e);
					long tempActuel = System.currentTimeMillis();
					String tempPasse = calculTempPasse(tempActuel, dateDuJour);
					Historique.ecrire("temps passé faire la sauvegarde : "
							+ tempPasse);
					return;

				}
			}// fin de il y a des fichiers a zipper
			if (succesZip == true) {// l'archivage a reussi, on previens
				// l'utilisateur
				gestionErreur(encours, tempDirectory, true);

				long tempActuel = System.currentTimeMillis();
				String tempPasse = calculTempPasse(tempActuel, dateDuJour);
				Historique.ecrire("temps passé faire la sauvegarde : "
						+ tempPasse);

			}

		} else {
			JOptionPane.showMessageDialog(null,
					"La copie des fichiers vers le repertoire temporaire à echouée.\n\r "
							+ "Veuillez ré-essayer",
					"Erreur lors de la sauvegarde", JOptionPane.ERROR_MESSAGE);

			gestionErreur(encours, tempDirectory, false);
			Historique
					.ecrire("La copie des fichiers vers le repertoire temporaire à echouée.\n\r "
							+ "Erreur lors de la sauvegarde");
			long tempActuel = System.currentTimeMillis();
			String tempPasse = calculTempPasse(tempActuel, dateDuJour);
			Historique.ecrire("temps passé faire la sauvegarde : " + tempPasse);
			return;
		}
	}

	private long getDateRef(boolean isPresent, String WorkingDirectory) {
		String dateDeRef = "0";
		long dateRef = 0;
		if (isPresent) {
			try {
				dateDeRef = GestionDemandes
						.executeRequeteEtRetourne1Champ("select max (a.DATE_FICHIER)from FICHIER a where a.EMPLACEMENT_FICHIER like '"
								+ WorkingDirectory + "%' ");
			} catch (SQLException e2) {
				System.out.println(e2);
			}
			if (!dateDeRef.equals("")) {
				dateRef = Long.parseLong(dateDeRef);
			}
		}

		return dateRef;
	}

	private boolean isFichierPresentEnBase(String WorkingDirectory) {
		String requete = "select count (a.EMPLACEMENT_FICHIER) from FICHIER a where a.EMPLACEMENT_FICHIER like '"
				+ WorkingDirectory + "%' ";
		int nbFichier = 0;
		try {
			nbFichier = Integer.parseInt(GestionDemandes
					.executeRequeteEtRetourne1Champ(requete));
		} catch (NumberFormatException e) {
			nbFichier = 0;
		} catch (SQLException e) {
			nbFichier = 0;
		}

		if (nbFichier > 0) {
			return true;
		}
		return false;
	}

	private String calculTempPasse(long tempFin, long tempDebut) {

		long tempPasse = tempFin - tempDebut;
		long time = tempPasse / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}

		String TempPasse = hours + " heure(s) " + minutes + " minute(s) "
				+ seconds + " seconde(s)";
		return TempPasse;
	}

	/**
	 * @param encours
	 * @param resultatSauvegarde
	 * @param nbderreur
	 * @return
	 */
	private void gestionErreur(File encours, File tempDirectory,
			boolean resultatSauvegarde) {

		PROGRESSION_EN_COURS.setValue(0);
		PROGRESSION_EN_COURS.setString(0 + " %");
		PROGRESSION_TOTALE.setValue(0);
		PROGRESSION_TOTALE.setString(0 + " %");
		MESSAGE_UTILISATEUR.setText("");
		if (resultatSauvegarde == false) {
			int ID_SAUVEGARDE = 0;
			try {
				ID_SAUVEGARDE = Integer
						.parseInt(GestionDemandes
								.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
			} catch (NumberFormatException e1) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
			} catch (SQLException e1) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
			}
			String RequetteDelete = "DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE="
					+ ID_SAUVEGARDE;
			GestionDemandes.executeRequete(RequetteDelete);

			RequetteDelete = "DELETE FROM FICHIER WHERE ID_SAUVEGARDE="
					+ ID_SAUVEGARDE;
			GestionDemandes.executeRequete(RequetteDelete);
			SAVE_NOK.setVisible(true);
		} else {
			SAVE_OK.setVisible(true);
		}
		REFRESH.setEnabled(true);
		STOP.setEnabled(false);
		GO.setEnabled(true);
		PAUSE.setEnabled(false);

		try {
			FileUtility.recursifDelete(tempDirectory);
		} catch (IOException e) {
			Historique.ecrire("erreur au vidage du dossier temporaire");
		}

		boolean succesDelete = encours.delete();
		if (!succesDelete) {
			encours.deleteOnExit();
		}

		if (STOP_MACHINE.isSelected()) {
			try {
				WriteFile
						.WriteLineInNewFile(
								"La derniere sauvegarde ne s'est pas correctement déroulée",
								GestionRepertoire.RecupRepTravail()
										+ "/IniFile/Resultat.txt");
			} catch (IOException e1) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
			}
			LanceArret();
		}

	}

	private boolean copyAvecProgress(File sRC2, File dEST2,
			JProgressBar progressEnCours) {
		boolean resultat = false;
		long PourcentEnCours = 0;
		// Déclaration des stream d'entree sortie
		java.io.FileInputStream sourceFile = null;
		java.io.FileOutputStream destinationFile = null;

		try {
			// Création du fichier :
			dEST2.createNewFile();

			// Ouverture des flux
			sourceFile = new java.io.FileInputStream(sRC2);
			destinationFile = new java.io.FileOutputStream(dEST2);
			long tailleTotale = sRC2.length();

			// Lecture par segment de 0.5Mo
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLecture);
				long tailleEnCours = dEST2.length();
				PourcentEnCours = ((100 * (tailleEnCours + 1)) / tailleTotale);
				int Pourcent = (int) PourcentEnCours;
				progressEnCours.setValue(Pourcent);
				// progressEnCours.setString(sRC2.getName()+" : "+Pourcent+" %");
				progressEnCours.setString(sRC2 + " : " + Pourcent + " %");
			}

			// si tout va bien
			resultat = true;
			// dEST2.deleteOnExit();

		} catch (java.io.FileNotFoundException f) {

		} catch (java.io.IOException e) {

		} finally {
			// Quelque soit on ferme les flux
			try {
				sourceFile.close();
			} catch (Exception e) {
			}
			try {
				destinationFile.close();

			} catch (Exception e) {
			}
		}
		return (resultat);

	}

	@SuppressWarnings("unused")
	private boolean copyAvecProgressNIO(File sRC2, File dEST2,
			JProgressBar progressEnCours) {
		boolean resultat = false;
		long PourcentEnCours = 0;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sRC2);
		} catch (FileNotFoundException e) {

			Historique.ecrire("Erreur à la copie du fichier " + sRC2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dEST2);
		} catch (FileNotFoundException e) {

			Historique.ecrire("Erreur à la creation du fichier " + dEST2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}

		java.nio.channels.FileChannel channelSrc = fis.getChannel();
		java.nio.channels.FileChannel channelDest = fos.getChannel();
		progressEnCours.setValue(0);

		progressEnCours.setString(sRC2 + " : 0 %");
		try {
			long tailleCopie = channelSrc.transferTo(0, channelSrc.size(),
					channelDest);
		} catch (IOException e) {

			Historique.ecrire("Erreur à la copie du fichier " + sRC2
					+ " vers la destination " + dEST2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}

		progressEnCours.setValue(100);
		progressEnCours.setString(sRC2 + " : 100 %");
		try {
			if (channelSrc.size() == channelDest.size()) {
				resultat = true;
			} else {
				resultat = false;
			}
		} catch (IOException e) {

			Historique.ecrire("Erreur à la copie du fichier " + sRC2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		try {
			fis.close();
		} catch (IOException e) {

			Historique
					.ecrire("Impossible de fermer le flux à la copie du fichier "
							+ sRC2 + " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		try {
			fos.close();
		} catch (IOException e) {

			Historique
					.ecrire("Impossible de fermer le flux à la copie du fichier "
							+ dEST2 + " pour la raison suivante : " + e);

			return true;
		}

		return (resultat);

	}

	private void LanceArret() {

		Historique.ecrire("Arret automatique de la machine");

		String cmdArretMachine = String.format("cmd /c shutdown -s -t 300 -f");
		Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmdArretMachine);
		} catch (IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
	}

	private void waitThread() {
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException ie) {
				Utilitaires.Historique.ecrire("Message d'erreur: " + ie);
			}
		}

	}

	public void pause() {
		pause = true;

	}

	public void reprise() {
		pause = false;

		synchronized (this) {
			this.notifyAll();
		}
	}

	private void VerifDossierVideEtSupprSiVide(File path) throws IOException {

		if (!path.exists()) {
			throw new IOException("File not found '" + path.getAbsolutePath()
					+ "'");
		}

		if (path.isDirectory()) {
			File[] children = path.listFiles();
			for (int i = 0; children != null && i < children.length; i++) {
				if (children[i].isDirectory() == true) {
					VerifDossierVideEtSupprSiVide(children[i]);
				} else {
					if (children.length == 0 && children[i].isFile()) {
						path.delete();
					}
				}

			}

			if (!path.delete()) {
				System.out.println("No delete path '" + path.getAbsolutePath()
						+ "'");

			}
		} else if (!path.delete())
			throw new IOException("No delete file '" + path.getAbsolutePath()
					+ "'");

	}

	public void termine() {
		PROGRESSION_EN_COURS.setValue(0);
		PROGRESSION_EN_COURS.setString(0 + " %");
		PROGRESSION_TOTALE.setValue(0);
		PROGRESSION_TOTALE.setString(0 + " %");
		MESSAGE_UTILISATEUR.setText("");
		int ID_SAUVEGARDE = 0;
		try {
			ID_SAUVEGARDE = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
		} catch (NumberFormatException e1) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
		} catch (SQLException e1) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
		}
		String RequetteDelete = "DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE="
				+ ID_SAUVEGARDE;
		GestionDemandes.executeRequete(RequetteDelete);

		RequetteDelete = "DELETE FROM FICHIER WHERE ID_SAUVEGARDE="
				+ ID_SAUVEGARDE;
		GestionDemandes.executeRequete(RequetteDelete);
		SAVE_NOK.setVisible(true);

		REFRESH.setEnabled(true);
		STOP.setEnabled(false);
		GO.setEnabled(true);
		PAUSE.setEnabled(false);

		try {
			FileUtility.recursifDelete(tempDirectory);
			Historique.ecrire("Vidage du repertoire temporaire reussi");
		} catch (IOException e) {
			Historique.ecrire("erreur au vidage du dossier temporaire");
		}

		boolean succesDelete = encours.delete();
		if (!succesDelete) {
			encours.deleteOnExit();
		}
	}
}
