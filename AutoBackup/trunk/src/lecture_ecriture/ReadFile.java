package lecture_ecriture;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import Utilitaires.Historique;
import accesBDD.GestionDemandes;

public class ReadFile {

	private static final long serialVersionUID = 1L;

	/**
	 * Lecture ligne à ligne d'un fichier texte et affichage dans une jList
	 * @param chemin -String le chemin du fichier texte
	 * @param listModel -DefaultModelList le model de liste
	 * @param nbLigne -JLabel sert a afficher le nb de ligne
	 * @param nbAdresse -int le nb d'adresse trouvée.
	 */

	public static int ReadLine(final String chemin,
			final DefaultListModel listModel) {

		int nbAdresse = 0;
		try {
			// Open the file that is the first
			// command line parameter
			final FileInputStream fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			final DataInputStream in = new DataInputStream(fstream);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				// Print the content on the console
				// System.out.println (strLine);

				if (!strLine.equals("")) {
					listModel.addElement(strLine);
					nbAdresse++;
				}
			}
			// Close the input stream
			in.close();

		} catch (final Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return nbAdresse;
	}

	/**
	 * Trouver une chaine de caracteres dans un fichier
	 * @param cheminFichier -String le chemin du fichier
	 * @param OccurToFind -String la chaine a trouver ex "abc@hotmail.com"
	 * @return result -boolean vrai si on trouve la chaine de caracteres.
	 */
	public static boolean FindOccurInFile(final String cheminFichier,
			final String OccurToFind) {

		String line = null;
		boolean result = false;

		try {
			final BufferedReader br = new BufferedReader(new FileReader(
					cheminFichier));

			int i = 1; // initialisation du numero de ligne
			while ((line = br.readLine()) != null) {
				if (line.indexOf(OccurToFind) != -1) {
					System.out.println("Mot trouve a la ligne " + i);
					result = true;
					return result;
				}
				i++;
			}
			br.close();
		} catch (final FileNotFoundException exc) {
			System.out.println("File not found");
		} catch (final IOException ioe) {
			System.out.println("Erreur IO");
		}
		return result;

	}

	public static int compteNbLigne(final String chemin) {
		int nbLigne = 0;
		try {
			// Open the file that is the first
			// command line parameter
			final FileInputStream fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			final DataInputStream in = new DataInputStream(fstream);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				// System.out.println (strLine);

				if (!strLine.equals("")) {
					nbLigne++;
				}
			}
			// Close the input stream
			in.close();

		} catch (final Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Erreur : " + e, "Erreur",
					JOptionPane.ERROR_MESSAGE);

			Historique.ecrire("Erreur : " + e);

			return -1;
		}
		return nbLigne - 1;
	}

	public static int FichierReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress)
			throws IOException {
		int progression = 0;

		int ID_SAUVEGARDE = 0;
		long DATE_FICHIER = 0;
		String EMPLACEMENT_FICHIER = "";
		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						ID_SAUVEGARDE = Integer.parseInt(tabChaine[0].trim());
						DATE_FICHIER = Long.parseLong(tabChaine[1].trim());
						EMPLACEMENT_FICHIER = tabChaine[2].trim();

						final String InsertFichier = "INSERT INTO FICHIER (ID_SAUVEGARDE, DATE_FICHIER, EMPLACEMENT_FICHIER) "
								+ "VALUES ("
								+ ID_SAUVEGARDE
								+ ","
								+ DATE_FICHIER
								+ ",'"
								+ EMPLACEMENT_FICHIER
								+ "')";

						// Historique.ecrire("Insertion d'un fichier sauvegardé avec la requete : "+InsertFichier);

						final boolean resultInsereCategorie = GestionDemandes
								.executeRequete(InsertFichier);
						if (resultInsereCategorie == false) {
							Historique
									.ecrire("Insertion d'un fichier sauvegardé avec la requete : "
											+ InsertFichier);
							Historique
									.ecrire("Insertion d'un fichier sauvegardé echoué");
							nbDinsert--;
						} else {
							// Historique.ecrire("Insertion d'un fichier sauvegardé réussi");
							nbDinsert++;
						}
						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import Fichier sauvegardé "
								+ progression + " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Historique.ecrire("erreur à l'importation : " + e);

				return -1;
			} catch (final IOException e) {

				Historique.ecrire("erreur à l'importation : " + e);

				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import Fichier sauvegardé " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Historique.ecrire("erreur à l'importation : " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int ExclutReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress) {
		int progression = 0;

		String EMPLACEMENT_FICHIER = "";
		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						EMPLACEMENT_FICHIER = tabChaine[0];

						final String InsertExclut = "INSERT INTO LISTE_EXCLUT (EMPLACEMENT_FICHIER) "
								+ "VALUES ('" + EMPLACEMENT_FICHIER + "')";

						final boolean resultInsereCategorie = GestionDemandes
								.executeRequete(InsertExclut);
						if (resultInsereCategorie == false) {
							Historique
									.ecrire("Insertion d'un emplacement exclut avec la requete : "
											+ InsertExclut);
							Historique
									.ecrire("Insertion d'un emplacment exclut echoué");
							nbDinsert--;
						} else {
							// Historique.ecrire("Insertion d'un fichier exclut réussi");
							nbDinsert++;
						}

						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import emplacement exclut "
								+ progression + " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			} catch (final IOException e) {
				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import Fichier exclut " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int SauvegardeReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress)
			throws IOException {
		int progression = 0;

		int ID_SAUVEGARDE = 0;
		long DATE_SAUVEGARDE = 0;
		String EMPLACEMENT_SAUVEGARDE = "";
		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						ID_SAUVEGARDE = Integer.parseInt(tabChaine[0].trim());
						DATE_SAUVEGARDE = Long.parseLong(tabChaine[1].trim());
						EMPLACEMENT_SAUVEGARDE = tabChaine[2].trim();

						final String InsertSauvegarde = "INSERT INTO SAUVEGARDE (ID_SAUVEGARDE, DATE_SAUVEGARDE, EMPLACEMENT_SAUVEGARDE) "
								+ "VALUES ("
								+ ID_SAUVEGARDE
								+ ","
								+ DATE_SAUVEGARDE
								+ ",'"
								+ EMPLACEMENT_SAUVEGARDE + "')";

						final boolean resultInsereSecteur = GestionDemandes
								.executeRequete(InsertSauvegarde);
						if (resultInsereSecteur == false) {
							nbDinsert--;
							Historique
									.ecrire("Insertion d'une sauvegarde avec la requete : "
											+ InsertSauvegarde);
							Historique
									.ecrire("Insertion d'une sauvegarde echouée");
						} else {
							nbDinsert++;
							// Historique.ecrire("Insertion d'une sauvegarde réussie");

						}

						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import Sauvegarde " + progression
								+ " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Historique.ecrire("erreur à l'importation : " + e);

				return -1;
			} catch (final IOException e) {

				Historique.ecrire("erreur à l'importation : " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import Sauvegarde " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int HoraireReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress)
			throws IOException {
		int progression = 0;

		int HEURE = 0;
		int MINUTES = 0;
		int ARRET_MACHINE = 0;
		int ENVOI_MAIL = 0;
		int nbLigne = 0, nbDinsert = 0;
		String InsertHoraire = "";
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");

						HEURE = Integer.parseInt(tabChaine[0].trim());
						MINUTES = Integer.parseInt(tabChaine[1].trim());
						if (tabChaine.length > 2) {
							ARRET_MACHINE = Integer.parseInt(tabChaine[2]
									.trim());
							ENVOI_MAIL = Integer.parseInt(tabChaine[3].trim());
							InsertHoraire = "INSERT INTO HORAIRE (HEURE, MINUTES,ARRET_MACHINE,ENVOI_MAIL_ST) "
									+ "VALUES ("
									+ HEURE
									+ ","
									+ MINUTES
									+ ","
									+ ARRET_MACHINE + "," + ENVOI_MAIL + ")";
						} else {
							InsertHoraire = "INSERT INTO HORAIRE (HEURE, MINUTES,ARRET_MACHINE,ENVOI_MAIL_ST) "
									+ "VALUES ("
									+ HEURE
									+ ","
									+ MINUTES
									+ ",0,0)";
						}

						final boolean resultInsereSecteur = GestionDemandes
								.executeRequete(InsertHoraire);
						if (resultInsereSecteur == false) {
							nbDinsert--;
							Historique
									.ecrire("Insertion d'un horaire avec la requete : "
											+ InsertHoraire);
							Historique.ecrire("Insertion d'un horaire echouée");
						} else {
							nbDinsert++;
							// Historique.ecrire("Insertion d'un horaire réussi");

						}

						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import Horaire " + progression
								+ " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Historique.ecrire("erreur à l'importation : " + e);

				return -1;
			} catch (final IOException e) {

				Historique.ecrire("erreur à l'importation : " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import Sauvegarde " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int PlanifReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress)
			throws IOException {
		int progression = 0;

		String JOUR = "";
		int IS_SELECTED = 0;
		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						JOUR = tabChaine[0].trim();
						IS_SELECTED = Integer.parseInt(tabChaine[1].trim());

						final String InsertPlanif = "INSERT INTO PLANIF (JOUR, IS_SELECTED) "
								+ "VALUES ('" + JOUR + "'," + IS_SELECTED + ")";

						final boolean resultInsereSecteur = GestionDemandes
								.executeRequete(InsertPlanif);
						if (resultInsereSecteur == false) {
							nbDinsert--;
							Historique
									.ecrire("Insertion d'une planif avec la requete : "
											+ InsertPlanif);
							Historique.ecrire("Insertion d'une planif echouée");
						} else {
							nbDinsert++;
							// Historique.ecrire("Insertion d'une planif réussi");

						}

						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import Planif " + progression
								+ " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Historique.ecrire("erreur à l'importation : " + e);

				return -1;
			} catch (final IOException e) {

				Historique.ecrire("erreur à l'importation : " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import Sauvegarde " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Historique.ecrire("fichier a importer non trouvé : " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int ListeFichierReadLineEtInsereEnBase(final String chemin,
			final int nbligneAimporter, final JProgressBar progress) {

		int progression = 0;
		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						final String EMPLACEMENT_FICHIER = tabChaine[0].trim();

						final String InsertListeFichier = "INSERT INTO LISTE_FICHIER (EMPLACEMENT_FICHIER) "
								+ "VALUES ('" + EMPLACEMENT_FICHIER + "')";

						final boolean resultInsereSecteur = GestionDemandes
								.executeRequete(InsertListeFichier);
						if (resultInsereSecteur == false) {
							nbDinsert--;
							Historique
									.ecrire("Insertion d'une liste de fichier avec la requete : "
											+ InsertListeFichier);
							Historique
									.ecrire("Insertion d'une liste de fichier echouée");
						} else {
							nbDinsert++;
							// Historique.ecrire("Insertion d'une liste de fichier réussie");

						}
						nbLigne++;
						progression = (100 * (nbLigne - 1)) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import ListeFichier " + progression
								+ " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import ListeFichier " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static int CheminSauvegardeFichierReadLineEtInsereEnBase(
			final String chemin, final int nbligneAimporter,
			final JProgressBar progress) {
		int progression = 0;

		int nbLigne = 0, nbDinsert = 0;
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(chemin);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			// Read File Line By Line
			try {
				while ((strLine = br.readLine()) != null) {

					if (nbLigne >= 1) {// si c'est la deuxieme ligne (ou plus)
						// (la premiere contient les différents
						// champs)
						strLine = strLine.replace("(", " ");
						strLine = strLine.replace(")", " ");
						strLine = strLine.trim();
						final String[] tabChaine = strLine.split(";");
						final String EMPLACEMENT_SAUVEGARDE = tabChaine[0]
								.trim();

						final String InsertListeFichier = "INSERT INTO CHEMIN_SAUVEGARDE (EMPLACEMENT_DE_SAUVEGARDE) "
								+ "VALUES ('" + EMPLACEMENT_SAUVEGARDE + "')";

						final boolean resultInsereSecteur = GestionDemandes
								.executeRequete(InsertListeFichier);
						if (resultInsereSecteur == false) {
							nbDinsert--;
							Historique
									.ecrire("Insertion d'un chemin de sauvegarde avec la requete : "
											+ InsertListeFichier);
							Historique
									.ecrire("Insertion d'un chemin de sauvegarde echoué");
						} else {
							nbDinsert++;
							// Historique.ecrire("Insertion d'un chemin de sauvegarde réussi");

						}

						nbLigne++;
						progression = (100 * nbLigne) / nbligneAimporter;
						progress.setValue(progression);
						progress.setString("Import d'un chemin de sauvegarde "
								+ progression + " %");

					} else {
						nbLigne++;
					}

				}
			} catch (final NumberFormatException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				return -1;
			}

			/*
			 * nbLigne++; progression = (100*nbLigne)/nbligneAimporter;
			 * progress.setValue (progression); progress.setString
			 * ("Import d'un chemin de sauvegarde " +progression+" %");
			 */
		} catch (final FileNotFoundException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		// Close the input stream
		try {
			in.close();
			br.close();
			fstream.close();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return -1;
		}

		return nbDinsert;
	}

	public static String ReadLine(final File CheminDuFichier) {
		DataInputStream in = null;
		BufferedReader br;

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream = null;

		try {
			fstream = new FileInputStream(CheminDuFichier);
		} catch (final FileNotFoundException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return null;
		}
		// Get the object of DataInputStream
		in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
		String strLine = null;
		try {
			strLine = br.readLine();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return null;
		}

		try {
			in.close();
		} catch (final IOException e) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			return null;
		}

		return strLine;

	}

}
