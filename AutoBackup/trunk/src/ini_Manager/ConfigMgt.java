package ini_Manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;

public class ConfigMgt {

	// ****************************************
	// Membres privés de ConfigMgt.
	// ****************************************

	private String nomFichier; // Nom du fichier
	private String repertoireFichier; // Répertoire du fichier
	private File fichier; // Le fichier
	private File fichierTmp; // Le fichier de travail
	private RandomAccessFile contenuFichier; // Son contenu
	private final char commentaireChar; // Caractère de commentaire
	@SuppressWarnings("unchecked")
	private Hashtable configuration; // Variables et leur valeur
	private BufferedWriter output; // Variable qui permet d'écrire dans un

	// fichier

	// ****************************************
	// Constructeurs de ConfigMgt.
	// ****************************************
	/**
	 * Construction d'un nouveau manager de configuration fichier. Si le fichier
	 * passé en parametre est null, cela déclanche une exception. On peut aussi
	 * parametrer la valeur du caractère de commentaire dans le fichier.
	 * @param fic fichier contenant les informations de configurations.
	 * @param commentaire Caractère permettant de commenter des lignes.
	 * @throws NullPointerException Si le fichier passé en paramètre est null.
	 * @throws IOException Si le fichier n'en est pas un, ou s'il n'est lisible,
	 *             ou s'il n'est pas modifiable.
	 */
	public ConfigMgt(final File fic, final char commentaire)
			throws NullPointerException, IOException {
		// Initalisation du caractère de commentaire
		commentaireChar = commentaire;

		// Test de la nullité du fichier.
		if (fic != null) {
			// Le fichier n'est pas null.

			// Dernière occurence du caractère '/' ou '\' dans le chemin absolue
			// du fichier.
			final int derIndexSepFichier = fic.getAbsolutePath().lastIndexOf(
					File.separatorChar) + 1;

			// Initialisation du nom et du répertoire du fichier.
			nomFichier = fic.getAbsolutePath().substring(derIndexSepFichier);
			repertoireFichier = fic.getAbsolutePath().substring(0,
					derIndexSepFichier);

			// On test la validité du fichier
			if (isFichierOk()) {
				// Le fichier est OK, on charge les éléments de la config en
				// mémoire.
				if (!chargementConfig()) {
					throw new IOException(
							"Impossible de charger le fichier en mémoire !!!");
				}
			} else {
				// Le fichier ne répond aux critères.
				throw new IOException();
			}
		} else {
			// Le fichier est null, cela produit une exception.
			throw new NullPointerException("Le fichier est null !");
		}
	}

	/**
	 * Construction d'un nouveau manager de configuration fichier. Si l'une des
	 * deux chaines de caractères passées en argument est null, cela provoque
	 * une exception.
	 * @param nomFic Chaine de caractères contenant le nom du fichier.
	 * @param repFic Chaine de caractères contenant le nom du répertoire où se
	 *            trouve du fichier.
	 * @param commentaire Caractère permettant de commenter des lignes.
	 * @throws NullPointerException Si le nom du fichier et/ou le nom du
	 *             repertoire passés en paramètre sont null.
	 * @throws IOException Si le fichier n'en est pas un, ou s'il n'est lisible,
	 *             ou s'il n'est pas modifiable.
	 */
	public ConfigMgt(final String nomFic, final String repFic,
			final char commentaire) throws NullPointerException, IOException {
		// Initalisation du caractère de commentaire
		commentaireChar = commentaire;

		// On teste la nullité du nom du fichier et du répertoire.
		if (nomFic != null && repFic != null) {
			// Ni le fichier, ni le répertoire sont null

			// Initialisation du nom du fichier
			nomFichier = nomFic;

			if (nomFichier.trim().equals("")) {
				throw new IOException();
			}

			if ((nomFichier + repFic).trim().equals("")
					|| (nomFichier + repFic).trim().equals("\\")
					|| (nomFichier + repFic).trim().equals("/")) {
				throw new IOException();
			}

			// On s'assure que le dernier caractère du chemin est bien le
			// caractère de séparation des répertoires.
			if (repFic.charAt(repFic.length() - 1) == File.separatorChar) {
				// Initialisation du nom du répertoire
				repertoireFichier = repFic;
			} else {
				// Initialisation du nom du répertoire auquel on ajoute
				// un caractère de séparation
				repertoireFichier = repFic + File.separatorChar;
			}

			// On test la validité du fichier
			if (isFichierOk()) {
				// Le fichier est OK, on charge les éléments de la config en
				// mémoire.
				if (!chargementConfig()) {
					throw new IOException(
							"Impossible de charger le fichier en mémoire !!!");
				}
			} else {
				// Le fichier ne répond aux critères.
				throw new IOException();
			}
		} else {
			// Le nom fichier est null ou le nom de repertoire est null,
			// cela produit une exception.
			throw new NullPointerException(
					"Le nom du fichier ou le nom du repertoire est null !!!");
		}
	}

	// ****************************************
	// Méthodes privées de ConfigMgt.
	// ****************************************

	/**
	 * Méthode de vérification du fichier de configuration. Celui-ci doit
	 * existéer, être lisible et accessible en écriture. S'il ne rempli pas ces
	 * conditions, cela génère une exception IOException.
	 * @return Vrai si le fichier est conforme aux attentes sinon Faux.
	 * @throws IOException Si le fichier n'en est pas un, ou s'il n'est lisible,
	 *             ou s'il n'est pas modifiable.
	 */
	public boolean isFichierOk() {
		boolean rc = false;

		try {
			// Création du fichier
			fichier = new File(repertoireFichier + nomFichier);

			// est-ce bien un fichier?
			if (!fichier.isFile()) {
				rc = false;
				// System.err.println("*** Ce n'est pas un fichier valide !!! ***");
			} else if (!fichier.canRead()) {
				rc = false;
				// System.err.println("*** Le Fichier n'est pas accessible en lecture !!! ***");
			} else if (!fichier.canWrite()) {
				rc = false;
				// System.err.println("*** Le Fichier n'est pas accessible en écriture !!! ***");
			} else {
				// création du fichier temporaire sur lequel on va travailler
				fichierTmp = File.createTempFile("$" + nomFichier + "$", "tmp",
						null);
				fichierTmp.deleteOnExit();
				copier(fichier, fichierTmp);
				rc = true;
			}
		} catch (final IOException IOex) {
			rc = false;
		}

		return rc;
	}

	/**
	 * Méthode de chargement du fichier de config en mémoire.
	 * @return Vrai s'il a réussi à le charger, sinon Faux.
	 */
	@SuppressWarnings("unchecked")
	private boolean chargementConfig() {
		boolean rc = false;
		int posEgal = -1;
		configuration = new Hashtable();

		try {
			// Création de l'accesseur du fichier
			contenuFichier = new RandomAccessFile(fichierTmp, "rw");

			// On se replace en haut du fichier
			contenuFichier.seek(0);

			// On récupère la première ligne
			String LigneCourrante = contenuFichier.readLine();

			// on sort de la boucle si la ligne courante est null(fin du
			// fichier)
			while (LigneCourrante != null) {
				// On s'assure que la ligne n'est pas vide
				if (LigneCourrante.trim().length() != 0) {
					// On ne prend que les lignes non commentées
					if (LigneCourrante.trim().charAt(0) != commentaireChar) {
						// on supprime les commentaires de fin de lignes
						final int i = LigneCourrante.indexOf(commentaireChar);
						if (i > -1) {
							LigneCourrante = LigneCourrante.substring(0, i)
									.trim();
						}

						// on recherche le caractère '='
						posEgal = LigneCourrante.indexOf('=');
						if (posEgal > -1) {
							// Il y a un caractère '='
							final String variable = new String(LigneCourrante
									.substring(0, posEgal).trim());

							if (!variable.equals("")) {
								final String valeur = LigneCourrante.substring(
										posEgal + 1).trim();

								configuration.put(variable, valeur);
							} else {
								// System.err.println("*** Le fichier est mal formater !! ***");
								throw new IOException();
							}
						} else {
							// System.err.println("*** Le fichier est mal formater !! ***");
							throw new IOException();
						}
					}
				}
				// On passe à la ligne suivante
				LigneCourrante = contenuFichier.readLine();
			}

			contenuFichier.close();

			rc = true;
		} catch (final IOException IOe) {
			rc = false;
		} finally {
			try {
				contenuFichier.close();
			} catch (final IOException e) {
			}
		}

		return rc;
	}

	/**
	 * Méthode de copie d'un fichier dans un autre.
	 * @param source Fichier que l'on veut copier.
	 * @param destination Fichier dans lequel on copie.
	 * @return Vrai Si la copie s'est bien passé sinon faux.
	 */
	private boolean copier(final File source, final File destination) {
		boolean rc = false;

		// Declaration des flux
		FileInputStream sourceFile = null;
		FileOutputStream destinationFile = null;

		try {
			// Création du fichier :
			destination.createNewFile();
			// Ouverture des flux
			sourceFile = new java.io.FileInputStream(source);
			destinationFile = new java.io.FileOutputStream(destination);
			// Lecture par segment de 1ko
			final byte buffer[] = new byte[1024];
			int nbLecture;

			while ((nbLecture = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLecture);
			}
			// Copie réussie
			rc = true;
		} catch (final FileNotFoundException f) {
		} catch (final IOException e) {
		} finally {
			// Quoi qu'il arrive, on ferme les flux
			try {
				sourceFile.close();
			} catch (final Exception e) {
			}
			try {
				destinationFile.close();
			} catch (final Exception e) {
			}
		}

		return rc;
	}

	// ****************************************
	// Méthodes publiques de ConfigMgt.
	// ****************************************
	/**
	 * Fonction retournant la valeur d'un paramètre de la configuration. <br>
	 * e.g. si dans un fichier on a une ligne du type age=34 la fonction
	 * GetValueOf("age") retournera "34".
	 * @param VarName nom de la variable dans le fichier.
	 * @return valeur de la variable rentrer en paramètre ou une chaine vide si
	 *         la variable n'existe pas.
	 */
	public String getValeurDe(final String varName) {
		// Initialisation des variables
		String rc = "";
		Object tmp;

		// Vérification du paramètre VarName
		if (!varName.trim().equals("")) {
			// Le parametre n'est pas un chaine vide
			// Lecture de la Hashtable
			tmp = configuration.get(varName);
			// Si la valeur est non null
			if (tmp != null) {
				rc = tmp.toString();
			}
		}

		// On retourne le résultat
		return rc;
	}

	/**
	 * Méthode de mise à jour d'une variable dans la configuration. Si la valeur
	 * passée en argument est null alors on sauve "" et non null (On travaille
	 * sur un fichier txt). Si varName n'existe pas alors rien n'est fait de
	 * même si varName est null.
	 * @param varName Chaine de caractères contenant le nom de la variable.
	 * @param valeur Chaine de caractères contenant la valeur de la variable.
	 * @return Vrai si la configuration a été modifiée sinon Faux.
	 */
	@SuppressWarnings("unchecked")
	public boolean setValeurDe(final String varName, final String valeur) {
		boolean rc = false;

		if (varName != null && valeur != null) {
			// Les deux paramètres sont non null
			if (configuration.containsKey(varName)) {
				// La variable existe et on la met à jour
				configuration.put(varName, valeur);
				rc = true;
			}
		} else {
			// Au moins l'un des deux est null
			if (varName != null) {
				// La variable est non null
				if (configuration.containsKey(varName)) {
					// La variable existe et on la met à jour
					configuration.put(varName, "");
					rc = true;
				}
			}
		}

		return rc;
	}

	/**
	 * On crée un variable et son valeur . On l'écrit à la fin du fichier
	 * @param varName Chaine de caractères contenant le nom de la variable.
	 * @param valeur Chaine de caractères contenant la valeur de la variable.
	 * @return Vrai si la configuration a été modifiée sinon Faux.
	 */
	public boolean addVariable(final String varName, final String valeur) {
		boolean rc = false;

		if (varName != null && valeur != null) {
			// Les deux paramètres sont non null
			try {
				output = new BufferedWriter(new FileWriter(fichierTmp, true)); // flux
				// d'écriture
				output.write(varName + " = " + valeur); // on écrit à la fin du
				// fichier
				output.newLine(); // on ajoute une nouvelle ligne
				output.close();
				this.chargementConfig();
			} catch (final IOException e) {

				Utilitaires.Historique.ecrire("Message d'erreur: " + e);
			}
			rc = true;
		}

		return rc;
	}

	/**
	 * Permet de retourner la liste des fichiers qui ont le même extension il
	 * faut definir l'emplacement et l'extension du fichier
	 */
	public File[] listeFichier(final String emplacement, final String extension) {
		final File dir = new File(emplacement); // on crée une instance du
		// répertoire

		final FilenameFilter filter = new FilenameFilter() { // on crée un
			// filtre
			public boolean accept(final File dir, final String name)
			// retourne tous les fichier qui se termine par l'extension souhaité
			// .
			{
				return name.endsWith(extension);
			}
		};
		final File[] files = dir.listFiles(filter); // on met le résultat dans
		// un
		// tableau du fichier
		return files;

	}

	/**
	 * Méthode de sauvegarde de la configuration dans le fichier temporaire.
	 * Cette méthode permet de sauvegarder un état intermédiaire de la
	 * configuration sans écraser le fichier original.
	 * @return Vrai si la sauvegarde a réussi sinon Faux.
	 */
	public boolean sauveConfigDansFichierTmp() {
		boolean rc = false;
		int posEgal = -1;

		final String retourLigne = System.getProperty("line.separator");
		final StringBuffer chaineTampon = new StringBuffer();

		try {
			// Création de l'accesseur du fichier
			contenuFichier = new RandomAccessFile(fichierTmp, "rw");

			// On se place au début du fichier
			contenuFichier.seek(0);

			String ligneCourante = contenuFichier.readLine();

			// On parcourt toutes les lignes du fichiers
			while (ligneCourante != null) {
				// On regarde si la ligne est non vide
				if (ligneCourante.length() > 0) {
					// On regarde le premier caractère de la ligne
					if (ligneCourante.charAt(0) == commentaireChar) {
						// C'est une ligne de commentaire on la met dans le
						// tampon.
						chaineTampon.append(ligneCourante + retourLigne);
					} else {
						// On sauvegarde les commentaires situés sur cette ligne
						String comment = "";
						final int posCom = ligneCourante
								.indexOf(commentaireChar);
						if (posCom > -1) {
							comment = ligneCourante.substring(posCom);
							ligneCourante = ligneCourante.substring(0, posCom);
						}
						// Ce n'est pas une ligne de commentaire
						if (ligneCourante.trim().length() > 0) {
							// Ce n'est pas une ligne avec que des espaces

							// On teste la présence et la postion du caractère
							// '='
							posEgal = ligneCourante.indexOf('=');

							if (posEgal > -1) {
								// il y a un caractère '=' dans la ligne
								final String variable = new String(
										ligneCourante.substring(0, posEgal)
												.trim());

								if (!variable.equals("")) {
									// Le nom de la variable est non nul
									final String valeur = (String) configuration
											.get(variable);

									chaineTampon.append(variable + " = "
											+ valeur + " " + comment
											+ retourLigne);
								} else {
									// System.err.println("*** Le fichier est mal formater !! ***");
									throw new IOException();
								}
							} else {
								// la ligne est non vide et ne possède aucun
								// caractère '='
								// System.err.println("*** Le fichier est mal formaté. ***");
								throw new IOException();
							}
						} else {
							// La ligne ne possède que des espaces, on met un
							// retour
							// à la ligne dans le tampon
							chaineTampon.append(comment + retourLigne);
						}
					}
				} else {
					// La ligne est vide on met un retour à la ligne dans le
					// tampon
					chaineTampon.append(retourLigne);
				}

				ligneCourante = contenuFichier.readLine();
			}

			// Si on arrive ici c'est l'on a réussi à créer une nouvelle version
			// du fichier dans le tampon.
			contenuFichier.seek(0);

			contenuFichier.setLength(0);

			contenuFichier.writeBytes(chaineTampon.toString());

			contenuFichier.close();

			rc = true;
		} catch (final IOException e) {
			rc = false;
		} finally {
			try {
				contenuFichier.close();
			} catch (final IOException e1) {
			}
		}

		return rc;
	}

	/**
	 * Cette méthode permet de valider les modifications apportées au fichier.
	 * Celle-ci seront disponibles à la prochaine lecture du fichier de
	 * configuration. Dans le cas où l'enregistrement des données seraient
	 * impossible, la fonction retourne faux.
	 * @return Vrai si la sauvegarde est effective sinon Faux.
	 */
	public boolean valideSauveDansFichier() {
		boolean rc = false;

		rc = copier(fichierTmp, fichier);

		return rc;
	}
}
