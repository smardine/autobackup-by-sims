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
import Utilitaires.Comptage;
import Utilitaires.ComptageAvantZip;
import Utilitaires.Copy;
import Utilitaires.FileUtility;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.VariableEnvironement;
import accesBDD.GestionDemandes;

// protected TYPE xxx

public class Thread_Sauvegarde extends Thread {
	// tte les decalration necessaire...
	protected JLabel MESSAGE_UTILISATEUR;
	protected String EMPLACEMENT;
	protected JProgressBar PROGRESSION_EN_COURS, PROGRESSION_TOTALE;
	protected JList LISTE_SAUVEGARDE, LISTE_EXCLUSION;
	protected DefaultListModel MODEL_SAUVEGARDE, MODEL_EXCLUSION;
	int nbFichierACopier = 0;
	protected JButton PAUSE, GO, STOP, REFRESH, SAVE_OK, SAVE_NOK;
	private volatile boolean pause = false;
	private final JCheckBox STOP_MACHINE;

	/**
	 * Affiche les differentes etapes du demarrage du logiciel, verifie
	 * certaines choses.
	 * @param actionListener
	 * @param Fenetre -JFrame pour l'affichage des resultats
	 * @param operation_jLabel -JLabel message pour l'utilisateur
	 * @param jTextField -JTextField message pour l'utilisateur
	 * @param jProgressBar -JProgressBar pour la progression
	 */

	public Thread_Sauvegarde(final String destination,
			final JProgressBar progressEnCours,
			final JProgressBar progressTotal, final JLabel operation,
			final JList ListeSauvegarde,
			final DefaultListModel ModelSauvegarde, final JList ListeExclu,
			final DefaultListModel ModelExclu, final JButton Pause,
			final JButton Go, final JButton Stop, final JButton Refresh,
			final JButton Save_Ok, final JButton Save_Nok,
			final JCheckBox Stop_Machine) {

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

		final File encours = new File(GestionRepertoire.RecupRepTravail()
				+ "/enCours.txt");
		try {
			encours.createNewFile();
		} catch (final IOException e3) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e3);
		}
		PROGRESSION_EN_COURS.setValue(0);
		PROGRESSION_EN_COURS.setString(0 + " %");
		PROGRESSION_TOTALE.setValue(0);
		PROGRESSION_TOTALE.setString(0 + " %");
		MESSAGE_UTILISATEUR.setText("");

		// on crée la reference de sauvegarde dans la BDD
		final long dateDuJour = System.currentTimeMillis();

		final Date actuelle = new Date();
		final DateFormat dateFormat = new SimpleDateFormat(
				"yyyy_MM_dd_HH_mm_ss");
		final String Date = dateFormat.format(actuelle);

		final String FileName = Date + "_AutoBackup.zip";
		EMPLACEMENT = EMPLACEMENT + "\\" + FileName;

		GestionDemandes
				.executeRequete("INSERT INTO SAUVEGARDE (DATE_SAUVEGARDE, EMPLACEMENT_SAUVEGARDE) VALUES ("
						+ dateDuJour + ",'" + EMPLACEMENT + "')");

		int nbDeLigne = LISTE_SAUVEGARDE.getModel().getSize();
		final String TempDirectory = VariableEnvironement.VarEnvSystem("TMP")
				+ "\\" + Date;
		boolean succesCopie = false;
		File tempDirectory = null;
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
			} catch (final InterruptedException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			} catch (final InvocationTargetException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}

			final String WorkingDirectory = MODEL_SAUVEGARDE.getElementAt(i)
					.toString();
			final String NomRepertoire = WorkingDirectory.substring(
					WorkingDirectory.lastIndexOf("\\"), WorkingDirectory
							.length());
			final File Actu = new File(WorkingDirectory);

			if (Actu.isDirectory() == true && Actu.exists()) {// c'est un
				// dossier et il
				// existe!!!
				final Comptage count = new Comptage(WorkingDirectory,
						MESSAGE_UTILISATEUR, LISTE_EXCLUSION, MODEL_EXCLUSION);
				nbFichierACopier = count.getNbFichier() + nbFichierACopier;
				MESSAGE_UTILISATEUR.setText("Copie de " + 0
						+ " fichier(s)  / sur " + nbFichierACopier
						+ " au total");
				Historique.ecrire("Nombre de fichier à sauvegarder : "
						+ nbFichierACopier);
				Copy save = null;
				if (pause) {
					waitThread();
				}

				try {
					save = new Copy(PAUSE, GO, STOP, WorkingDirectory,
							TempDirectory + "\\" + NomRepertoire,
							nbFichierACopier, PROGRESSION_EN_COURS,
							PROGRESSION_TOTALE, TempDirectory + "\\"
									+ NomRepertoire, MESSAGE_UTILISATEUR,
							LISTE_EXCLUSION, MODEL_EXCLUSION);
				} catch (final SQLException e2) {
					System.out.println("erreur SQL lors de la copie :" + e2);
				} catch (final IOException e2) {
					System.out
							.println("erreur d'acces lors de la copie :" + e2);
				}
				try {
					VerifDossierVideEtSupprSiVide(new File(TempDirectory + "\\"
							+ NomRepertoire));
				} catch (final IOException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}

				nbDeLigne = LISTE_SAUVEGARDE.getModel().getSize();// on recompte
				// le nb de
				// ligne
				// dans la
				// liste des
				// sauvegarde
				// au cas ou
				// l'utilisateur
				// aurait
				// rajouter
				// des
				// choses
				final int nbderreur = save.getNbErreur();

				if (nbderreur != 0) {
					succesCopie = false;
					System.out.println("nb d'erreur a la copie: " + nbderreur);
					gestionErreur();
					return;

				} else {
					succesCopie = true;
				}

			}
			if (Actu.isFile() == true && Actu.exists()) {// c'est un fichier et
				// il existe!!!
				nbFichierACopier = 1 + nbFichierACopier;
				// long tailleSource = Actu.length();
				MESSAGE_UTILISATEUR.setText("Copie de " + nbFichierACopier
						+ " fichier(s)  / sur " + nbFichierACopier
						+ " au total");
				final File tempo = new File(TempDirectory + "\\"
						+ NomRepertoire);

				final String cheminDuFichier = Actu.getAbsolutePath();
				final long dateDuFichierOriginal = Actu.lastModified();
				long dateDuFIchierEnBase = 0;

				int nbEnregistrementPresent = 0;
				final String cheminDuFichierSansAccent = cheminDuFichier
						.replaceAll("'", "");
				try {
					nbEnregistrementPresent = Integer
							.parseInt(GestionDemandes
									.executeRequeteEtRetourne1Champ("SELECT count(EMPLACEMENT_FICHIER) FROM FICHIER WHERE EMPLACEMENT_FICHIER = '"
											+ cheminDuFichierSansAccent + "'"));
				} catch (final NumberFormatException e1) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
				} catch (final SQLException e1) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
				}
				int ID_SAUVEGARDE = 0;
				try {
					ID_SAUVEGARDE = Integer
							.parseInt(GestionDemandes
									.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
				} catch (final NumberFormatException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				} catch (final SQLException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}
				if (nbEnregistrementPresent != 0) {// le chemin du fichier est
					// bien en base
					// reste a verifier la date enregistrée en base et a la
					// comptarer a celle du fichier present sur le dd
					try {
						dateDuFIchierEnBase = Long
								.parseLong(GestionDemandes
										.executeRequeteEtRetourne1Champ("SELECT a.DATE_FICHIER FROM FICHIER a where  a.EMPLACEMENT_FICHIER= '"
												+ cheminDuFichierSansAccent
												+ "'"));
					} catch (final NumberFormatException e) {

						Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					} catch (final SQLException e) {

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
						GestionDemandes
								.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
										+ ID_SAUVEGARDE
										+ ","
										+ dateDuFichierOriginal
										+ ",'"
										+ cheminDuFichierSansAccent + "')");
						succesCopie = copyAvecProgress(Actu, tempo,
								PROGRESSION_EN_COURS); // on utilise la fonction
						// de copie standard

					}
				} else {// le chemin du fichier n'est pas rentré en base, on
					// rentre le chemin du fichier ainsi que la date de
					// derniere modif du fichier
					// il nous faut l'ID_SAUVEGARDE qui est le liens entre les
					// tables FICHIER et SAUVEGARDE
					GestionDemandes
							.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
									+ ID_SAUVEGARDE
									+ ","
									+ dateDuFichierOriginal
									+ ",'"
									+ cheminDuFichierSansAccent + "')");
					succesCopie = copyAvecProgress(Actu, tempo,
							PROGRESSION_EN_COURS); // on utilise la fonction de
					// copie standard
					if (succesCopie == false) {
						Historique
								.ecrire("Erreur lors de la copie du fichier : "
										+ Actu.getAbsolutePath() + " vers : "
										+ tempo.getAbsolutePath());
					}

				}

				if (!succesCopie) {
					gestionErreur();
					return;
				}
			}
			if ((!Actu.isDirectory() && !Actu.isFile()) || !Actu.exists()) {
				// ce n'est ni un fichier, ni un dossier OU n'existe pas
				final String RepActu = Actu.getAbsolutePath();
				JOptionPane
						.showMessageDialog(
								null,
								"Impossible de réaliser la sauvegarde\n\r"
										+ "Le repertoire/fichier: "
										+ RepActu
										+ " est introuvable.\n\r Veuillez verifier la liste des dossiers/fichiers a sauvegarder",
								"Sauvegarde Impossible",
								JOptionPane.ERROR_MESSAGE);

				PROGRESSION_EN_COURS.setValue(0);
				PROGRESSION_EN_COURS.setString(0 + " %");
				PROGRESSION_TOTALE.setValue(0);
				PROGRESSION_TOTALE.setString(0 + " %");
				MESSAGE_UTILISATEUR.setText("");

				REFRESH.setEnabled(true);
				STOP.setEnabled(false);
				GO.setEnabled(true);
				PAUSE.setEnabled(false);
				SAVE_NOK.setVisible(true);
				final boolean succesDelete = encours.delete();
				if (!succesDelete) {
					encours.deleteOnExit();
				}
				gestionErreur();
				Historique
						.ecrire("Pb lors de la sauvegarde : Le repertoire/fichier: "
								+ RepActu
								+ " est introuvable.\n\r Veuillez verifier la liste des dossiers/fichiers a sauvegarder");
				if (STOP_MACHINE.isSelected()) {
					try {
						WriteFile
								.WriteLineInNewFile(
										"La derniere sauvegarde ne s'est pas correctement déroulée",
										GestionRepertoire.RecupRepTravail()
												+ "/IniFile/Resultat.txt");
					} catch (final IOException e1) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e1);
					}
					LanceArret();
					return;
				} else {
					return;
				}

			}

		}

		if (pause) {
			waitThread();
		}

		boolean succesZip = false;

		if (succesCopie == true) {// si les copies ont fonctionnées
			Historique
					.ecrire("Copie des différents fichiers dans le repertoire temporaire reussie.");
			final ComptageAvantZip count = new ComptageAvantZip(TempDirectory,
					MESSAGE_UTILISATEUR);
			final int nbDeFIchierAZipper = count.getNbFichier();
			if (nbDeFIchierAZipper == 0) {// il n'y a aucun fichier a compresser
				// => on sort de la sauvegarde tt de
				// suite, c'est fini!!!
				GestionDemandes
						.executeRequete("DELETE FROM SAUVEGARDE WHERE DATE_SAUVEGARDE="
								+ dateDuJour
								+ " and  EMPLACEMENT_SAUVEGARDE='"
								+ EMPLACEMENT + "'");
				// JOptionPane.showMessageDialog(null,
				// "Sauvegarde éffectuée avec succés\n\r pas de fichier modifié à sauvegarder",
				// "Sauvegarde Ok", JOptionPane.INFORMATION_MESSAGE);
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

				gestionErreur();

				final boolean succesDelete = encours.delete();
				if (!succesDelete) {
					encours.deleteOnExit();
				}

				Historique
						.ecrire("Sauvegarde éffectuée avec succés,pas de fichier modifié à sauvegarder");

				try {
					FileUtility.recursifDelete(tempDirectory);
				} catch (final IOException e) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					System.out.println(e);
				}

				if (STOP_MACHINE.isSelected()) {
					try {
						WriteFile
								.WriteLineInNewFile(
										"La derniere sauvegarde s'est correctement déroulée",
										GestionRepertoire.RecupRepTravail()
												+ "/IniFile/Resultat.txt");
					} catch (final IOException e) {

						Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					}
					LanceArret();
					return;
				} else {
					return;
				}

			}
			if (nbDeFIchierAZipper != 0) {
				try {

					MESSAGE_UTILISATEUR.setText("Compression en cours");
					Historique.ecrire("Archivage du dossier : " + TempDirectory
							+ " vers le chemin : " + EMPLACEMENT);

					succesZip = OutilsZip.zipDir(TempDirectory, EMPLACEMENT,
							nbDeFIchierAZipper, PROGRESSION_TOTALE,
							PROGRESSION_EN_COURS, MESSAGE_UTILISATEUR);
				} catch (final FileNotFoundException e) {

					// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
					System.out.println(e);
					JOptionPane.showMessageDialog(null,
							"Pb lors de la sauvegarde : \n\r" + e, "Erreur",
							JOptionPane.ERROR_MESSAGE);
					PROGRESSION_EN_COURS.setValue(0);
					PROGRESSION_EN_COURS.setString(0 + " %");
					PROGRESSION_TOTALE.setValue(0);
					PROGRESSION_TOTALE.setString(0 + " %");
					MESSAGE_UTILISATEUR.setText("");

					REFRESH.setEnabled(true);
					STOP.setEnabled(false);
					GO.setEnabled(true);
					PAUSE.setEnabled(false);
					SAVE_NOK.setVisible(true);
					gestionErreur();

					final boolean succesDelete = encours.delete();

					if (!succesDelete) {
						encours.deleteOnExit();
					}

					Historique.ecrire("Pb lors de la sauvegarde : " + e);

					if (STOP_MACHINE.isSelected()) {
						try {
							WriteFile
									.WriteLineInNewFile(
											"La derniere sauvegarde ne s'est pas correctement déroulée",
											GestionRepertoire.RecupRepTravail()
													+ "/IniFile/Resultat.txt");
						} catch (final IOException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						}
						LanceArret();
						return;
					} else {
						return;
					}

				} catch (final IOException e) {

					// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
					System.out.println(e);
					JOptionPane.showMessageDialog(null,
							"Pb lors de la sauvegarde : \n\r" + e, "Erreur",
							JOptionPane.ERROR_MESSAGE);
					PROGRESSION_EN_COURS.setValue(0);
					PROGRESSION_EN_COURS.setString(0 + " %");
					PROGRESSION_TOTALE.setValue(0);
					PROGRESSION_TOTALE.setString(0 + " %");
					MESSAGE_UTILISATEUR.setText("");

					REFRESH.setEnabled(true);
					STOP.setEnabled(false);
					GO.setEnabled(true);
					PAUSE.setEnabled(false);
					SAVE_NOK.setVisible(true);

					gestionErreur();
					final boolean succesDelete = encours.delete();
					if (!succesDelete) {
						encours.deleteOnExit();
					}

					Historique.ecrire("Pb lors de la sauvegarde : " + e);

					if (STOP_MACHINE.isSelected()) {
						try {
							WriteFile
									.WriteLineInNewFile(
											"La derniere sauvegarde ne s'est pas correctement déroulée",
											GestionRepertoire.RecupRepTravail()
													+ "/IniFile/Resultat.txt");
						} catch (final IOException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						}
						LanceArret();
						return;
					} else {
						return;
					}

				}
			}

			if (succesZip == true) {// l'archivage a reussi, on previens
				// l'utilisateur

				Historique.ecrire("Sauvegarde éffectuée avec succés");

				try {
					FileUtility.recursifDelete(tempDirectory);
				} catch (final IOException e) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					System.out.println(e);
				}

				// JOptionPane.showMessageDialog(null,
				// "Sauvegarde éffectuée avec succés",
				// "Sauvegarde Ok", JOptionPane.INFORMATION_MESSAGE);
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
				final boolean succesDelete = encours.delete();
				if (!succesDelete) {
					encours.deleteOnExit();
				}

				if (STOP_MACHINE.isSelected()) {
					try {
						WriteFile
								.WriteLineInNewFile(
										"La derniere sauvegarde s'est correctement déroulée",
										GestionRepertoire.RecupRepTravail()
												+ "/IniFile/Resultat.txt");
					} catch (final IOException e) {

						Utilitaires.Historique.ecrire("Message d'erreur: " + e);
					}
					LanceArret();
				}

			}

		} else {
			JOptionPane.showMessageDialog(null,
					"La copie des fichiers vers le repertoire temporaire à echouée.\n\r "
							+ "Veuillez ré-essayer",
					"Erreur lors de la sauvegarde", JOptionPane.ERROR_MESSAGE);
			PROGRESSION_EN_COURS.setValue(0);
			PROGRESSION_EN_COURS.setString(0 + " %");
			PROGRESSION_TOTALE.setValue(0);
			PROGRESSION_TOTALE.setString(0 + " %");
			MESSAGE_UTILISATEUR.setText("");

			REFRESH.setEnabled(true);
			STOP.setEnabled(false);
			GO.setEnabled(true);
			PAUSE.setEnabled(false);
			SAVE_NOK.setVisible(true);
			gestionErreur();

			final boolean succesDelete = encours.delete();
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
				} catch (final IOException e1) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
				}
				LanceArret();
			}

			Historique
					.ecrire("La copie des fichiers vers le repertoire temporaire à echouée.\n\r "
							+ "Erreur lors de la sauvegarde");

			return;
		}
	}

	/**
	 * @param nbderreur
	 * @return
	 */
	private boolean gestionErreur() {
		boolean succesCopie;
		succesCopie = false;

		Historique
				.ecrire("Il y a eu des pb lors de la copie des fichiers vers le repertoire temporaire ");

		int ID_SAUVEGARDE = 0;
		try {
			ID_SAUVEGARDE = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
		} catch (final NumberFormatException e1) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
		} catch (final SQLException e1) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
		}
		String RequetteDelete = "DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE="
				+ ID_SAUVEGARDE;
		GestionDemandes.executeRequete(RequetteDelete);

		RequetteDelete = "DELETE FROM FICHIER WHERE ID_SAUVEGARDE="
				+ ID_SAUVEGARDE;
		GestionDemandes.executeRequete(RequetteDelete);
		return succesCopie;
	}

	private boolean copyAvecProgress(final File sRC2, final File dEST2,
			final JProgressBar progressEnCours) {
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
			final long tailleTotale = sRC2.length();

			// Lecture par segment de 0.5Mo
			final byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLecture);
				final long tailleEnCours = dEST2.length();
				PourcentEnCours = ((100 * (tailleEnCours + 1)) / tailleTotale);
				final int Pourcent = (int) PourcentEnCours;
				progressEnCours.setValue(Pourcent);
				// progressEnCours.setString(sRC2.getName()+" : "+Pourcent+" %");
				progressEnCours.setString(sRC2 + " : " + Pourcent + " %");
			}

			// si tout va bien
			resultat = true;
			// dEST2.deleteOnExit();

		} catch (final java.io.FileNotFoundException f) {

		} catch (final java.io.IOException e) {

		} finally {
			// Quelque soit on ferme les flux
			try {
				sourceFile.close();
			} catch (final Exception e) {
			}
			try {
				destinationFile.close();

			} catch (final Exception e) {
			}
		}
		return (resultat);

	}

	@SuppressWarnings("unused")
	private boolean copyAvecProgressNIO(final File sRC2, final File dEST2,
			final JProgressBar progressEnCours) {
		boolean resultat = false;
		final long PourcentEnCours = 0;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sRC2);
		} catch (final FileNotFoundException e) {

			Historique.ecrire("Erreur à la copie du fichier " + sRC2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dEST2);
		} catch (final FileNotFoundException e) {

			Historique.ecrire("Erreur à la creation du fichier " + dEST2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}

		final java.nio.channels.FileChannel channelSrc = fis.getChannel();
		final java.nio.channels.FileChannel channelDest = fos.getChannel();
		progressEnCours.setValue(0);

		progressEnCours.setString(sRC2 + " : 0 %");
		try {
			final long tailleCopie = channelSrc.transferTo(0,
					channelSrc.size(), channelDest);
		} catch (final IOException e) {

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
		} catch (final IOException e) {

			Historique.ecrire("Erreur à la copie du fichier " + sRC2
					+ " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		try {
			fis.close();
		} catch (final IOException e) {

			Historique
					.ecrire("Impossible de fermer le flux à la copie du fichier "
							+ sRC2 + " pour la raison suivante : " + e);

			return true;
			// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
		}
		try {
			fos.close();
		} catch (final IOException e) {

			Historique
					.ecrire("Impossible de fermer le flux à la copie du fichier "
							+ dEST2 + " pour la raison suivante : " + e);

			return true;
		}

		return (resultat);

	}

	private void LanceArret() {

		Historique.ecrire("Arret automatique de la machine");

		final String cmdArretMachine = String
				.format("cmd /c shutdown -s -t 300 -f");
		final Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmdArretMachine);
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
		try {
			p.waitFor();
		} catch (final InterruptedException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
	}

	private void waitThread() {
		synchronized (this) {
			try {
				this.wait();
			} catch (final InterruptedException ie) {
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

	private void VerifDossierVideEtSupprSiVide(final File path)
			throws IOException {

		if (!path.exists()) {
			throw new IOException("File not found '" + path.getAbsolutePath()
					+ "'");
		}

		if (path.isDirectory()) {
			final File[] children = path.listFiles();
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
		} else if (!path.delete()) {
			throw new IOException("No delete file '" + path.getAbsolutePath()
					+ "'");
		}

	}

}
