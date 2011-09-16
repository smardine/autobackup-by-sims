package Utilitaires;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import accesBDD.GestionDemandes;

public class Copy {
	// Dans le contructeur on va utiliser notre methode copy
	// et donc on vas faire quelques ptit test
	// protected String src,dest;
	protected JButton Pause, Go, Stop;
	// File DEST,SRC;
	int nbTotal;
	static int nbIgnoré = 0;
	int nbDerreur = 0;

	/**
	 * Copie le contenu d'un repertoire vers un autre et affiche le status de la
	 * copie dans une barre de progression.
	 * @param Source -String Le répertoire source
	 * @param Destination -String le repertoire de destination
	 * @param nbTotal -int le nb total de fichier a copier qui permet de
	 *            calculer la progression.
	 * @param progress -JProgressBar la barre de progression.
	 * @param sortieModel -DefaultModelList model de liste
	 * @param sortieList -JList le composant JList.
	 * @throws SQLException
	 * @throws IOException
	 */

	public Copy(final JButton Pause, final JButton Go, final JButton Stop,
			final String Source, final String Destination, final int nbTotal,
			final JProgressBar progressEnCours,
			final JProgressBar progressTotal, final String RepRacineLocal,
			final JLabel label, final JList ListeExclusion,
			final DefaultListModel ModelExclusion) throws SQLException,
			IOException {

		this.nbTotal = nbTotal;
		// this.src=Source;
		// this.dest=Destination;
		// this.SRC = new File (Source);
		// this.DEST = new File (Destination);
		this.Pause = Pause;
		this.Go = Go;
		this.Stop = Stop;

		int exclut = 0;

		for (int i = 0; i < ListeExclusion.getModel().getSize(); i++) {// on
			// verifie
			// si le
			// dossier/fichier
			// appartiens
			// a la
			// liste
			// d'exclusion

			final boolean FichierOuDossierExclu = Source.equals(ModelExclusion
					.getElementAt(i).toString());
			if (FichierOuDossierExclu == true) {
				// le fichier ou le dossier fait parti de la liste d'exclision.
				exclut++;
			}

		}

		// ben si le rep dest n'existe pas, et notre source est un repertoire
		final File DEST = new File(Destination);
		final File SRC = new File(Source);
		if (!DEST.exists() && exclut == 0) {
			if (SRC.isDirectory()) {
				DEST.mkdirs();

			}
		}

		final File[] fichier = SRC.listFiles();
		// Mais si jammais c'est un fichier, on fait un simple copie
		if (SRC.isFile() && exclut == 0) {
			// on recupere la date et le chemin du fichier d'origine.
			// on verifie si ele fichier existe en base de donnée
			// si oui, on verifie la date de modif, si identique, on ne le copie
			// pas
			// si les dates sont différents, on fait la copie.
			final long dateDuFichierOriginal = SRC.lastModified();
			long dateDuFIchierEnBase = 0;
			int nbEnregistrementPresent = 0;
			final String cheminDuFichierSansAccent = Source.replaceAll("'", "")
					.trim();
			final int ID_SAUVEGARDE = Integer
					.parseInt(GestionDemandes
							.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
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
			if (nbEnregistrementPresent != 0) {// le chemin du fichier est bien
				// en base
				// reste a verifier la date enregistrée en base et a la
				// comptarer a celle du fichier present sur le dd
				try {
					dateDuFIchierEnBase = Long
							.parseLong(GestionDemandes
									.executeRequeteEtRetourne1Champ("SELECT a.DATE_FICHIER FROM FICHIER a where  a.EMPLACEMENT_FICHIER='"
											+ cheminDuFichierSansAccent + "'"));
				} catch (final NumberFormatException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				} catch (final SQLException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}

				if (dateDuFichierOriginal == dateDuFIchierEnBase) {
					// les date sont identique, on ne copie pas
					nbIgnoré++;
					progressEnCours
							.setString(SRC.toString()
									+ " ignoré car non modifié depuis la dernière sauvegarde");
				} else {
					// les date de modif sont différentes, on lance la copie
					boolean succes = GestionDemandes
							.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
									+ ID_SAUVEGARDE
									+ ","
									+ dateDuFichierOriginal
									+ ",'"
									+ cheminDuFichierSansAccent + "')");
					succes = copyAvecProgress(SRC, DEST, progressEnCours);
					if (succes == false) {
						nbDerreur++;
						Historique
								.ecrire("Erreur lors de la copie du fichier : "
										+ SRC + " vers : " + DEST);

					}
				}

			} else {// le chemin du fichier n'est pas rentré en base, on rentre
				// le chemin du fichier ainsi que la date de derniere modif
				// du fichier
				// il nous faut l'ID_SAUVEGARDE qui est le liens entre les
				// tables FICHIER et SAUVEGARDE
				boolean succes = GestionDemandes
						.executeRequete("INSERT INTO FICHIER (ID_SAUVEGARDE,DATE_FICHIER,EMPLACEMENT_FICHIER) VALUES ("
								+ ID_SAUVEGARDE
								+ ","
								+ dateDuFichierOriginal
								+ ",'" + cheminDuFichierSansAccent + "')");
				succes = copyAvecProgress(SRC, DEST, progressEnCours); // on
				// utilise
				// la
				// fonction
				// de
				// copie
				// standard
				if (succes == false) {
					nbDerreur++;
					Historique.ecrire("Erreur lors de la copie du fichier : "
							+ SRC + " vers : " + DEST);
				}

			}

			// et si notre source est un repertoire qu'on doit copié!!!
		} else if (SRC.isDirectory() && fichier != null && exclut == 0) {// si
			// fichier=null,
			// c'est
			// que
			// le
			// dossier
			// a
			// des
			// restriction
			// d'acces
			// on parcour tout les elements de ce catalogue,
			for (final File f : SRC.listFiles()) {
				// et hop on fait un appel recursif a cette classe en mettant a
				// jour les path de src et dest: et le tour est joué
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						/**
						 * {@inheritDoc}
						 */
						@Override
						public void run() {

							final ComptageAuto count = new ComptageAuto(
									RepRacineLocal, label);
							final int nbEncours = count.getNbFichier()
									+ nbIgnoré;

							final int PourcentProgression = (100 * (nbEncours + 1))
									/ nbTotal;
							label.setText("Copie de " + nbEncours
									+ " fichier(s)  / sur " + nbTotal
									+ " au total");

							progressTotal.setValue(PourcentProgression);
							progressTotal.setString("Copie Totale : "
									+ PourcentProgression + " %");

						}
					});
				} catch (final InterruptedException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				} catch (final InvocationTargetException e) {
					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}

				new Copy(Pause, Go, Stop, f.getAbsolutePath(), DEST
						.getAbsoluteFile()
						+ "/" + f.getName(), nbTotal, progressEnCours,
						progressTotal, RepRacineLocal, label, ListeExclusion,
						ModelExclusion);

			}
		}

	}

	// /**
	// * Permet de fixer la date systeme en fonction de la date de création d'un
	// fichier
	// * @param cheminDuFichier -String le fichier dont on se sert pour fixer la
	// date Systeme
	// *
	// */
	// public static void FixeDateSystemeALaDateDeCreationDuFichier(String
	// cheminDuFichier) {
	//		
	// //on créer la commande qui servira a recuperer la date du fichier
	//	    
	// Runtime r= Runtime.getRuntime();
	// String cmdRecupDate =
	// String.format("cmd.exe /c dir /TC %s | find \"/\"  > tmp.txt",cheminDuFichier);
	// try {
	// Process p = r.exec(cmdRecupDate);
	// try {
	// p.waitFor();
	// } catch (InterruptedException e) {
	//				
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// } catch (IOException e) {
	//			
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// //on extrait la date systeme du fichier text et on fixe la date systeme
	// String cmdSetDate =
	// String.format("cmd.exe /c FOR /F \"tokens=1-4 delims= \" %%i in (tmp.txt) do DATE %%i");
	// try {
	// Process p = r.exec(cmdSetDate);
	// try {
	// p.waitFor();
	// } catch (InterruptedException e) {
	//					
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// } catch (IOException e) {
	//				
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	//			
	// String cmdEffaceTmpText = String.format("cmd.exe /c del tmp.txt");
	// try {
	// Process p = r.exec(cmdEffaceTmpText);
	// try {
	// p.waitFor();
	// } catch (InterruptedException e) {
	//						
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// } catch (IOException e) {
	//					
	// Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// }

	private boolean copyAvecProgress(final File p_Source,
			final File p_Destination, final JProgressBar progressEnCours) {
		boolean resultat = false;
		long PourcentEnCours = 0;
		// Déclaration des stream d'entree sortie
		java.io.FileInputStream sourceFile = null;
		java.io.FileOutputStream destinationFile = null;

		try {
			// Création du fichier :
			p_Destination.createNewFile();

			// Ouverture des flux
			sourceFile = new java.io.FileInputStream(p_Source);
			destinationFile = new java.io.FileOutputStream(p_Destination);
			final long tailleFichier = p_Source.length();

			// Lecture par segment de 0.5Mo
			final byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLecture);
				final long tailleEnCours = p_Destination.length();
				PourcentEnCours = ((100 * (tailleEnCours + 1)) / tailleFichier);
				final int Pourcent = (int) PourcentEnCours;
				progressEnCours.setValue(Pourcent);
				progressEnCours.setString(p_Source + " : " + Pourcent + " %");
			}

			// si tout va bien
			resultat = true;

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

	// private boolean copyAvecProgressNIO(File sRC2, File dEST2,JProgressBar
	// progressEnCours) {
	// boolean resultat = false;
	// long PourcentEnCours=0;
	//		 
	//
	// FileInputStream fis = null;
	// try {
	// fis = new FileInputStream(sRC2);
	// } catch (FileNotFoundException e) {
	//			
	//			
	// Historique.ecrire("Erreur à la copie du fichier "+sRC2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// //Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// FileOutputStream fos = null;
	// try {
	// fos = new FileOutputStream(dEST2);
	// } catch (FileNotFoundException e) {
	//			
	//			
	// Historique.ecrire("Erreur à la creation du fichier "+dEST2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// //Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	//
	//
	// java.nio.channels.FileChannel channelSrc = fis.getChannel();
	// java.nio.channels.FileChannel channelDest = fos.getChannel();
	// progressEnCours.setValue(0);
	//         
	// progressEnCours.setString(sRC2+" : 0 %");
	// try {
	// long tailleCopie= channelSrc.transferTo(0, channelSrc.size() ,
	// channelDest);
	// } catch (IOException e) {
	//			
	//			
	// Historique.ecrire("Erreur à la copie du fichier "+sRC2+" vers la destination "+dEST2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// //Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	//
	// progressEnCours.setValue(100);
	// progressEnCours.setString(sRC2+" : 100 %");
	// try {
	// if (channelSrc.size()==channelDest.size()){
	// resultat=true;
	// }else{
	// resultat= false;
	// }
	// } catch (IOException e) {
	//			
	//			
	// Historique.ecrire("Erreur à la copie du fichier "+sRC2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// //Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// try {
	// fis.close();
	// } catch (IOException e) {
	//			
	//			
	// Historique.ecrire("Impossible de fermer le flux à la copie du fichier "+sRC2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// //Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
	// }
	// try {
	// fos.close();
	// } catch (IOException e) {
	//			
	//			
	// Historique.ecrire("Impossible de fermer le flux à la copie du fichier "+dEST2+" pour la raison suivante : "
	// +e);
	//			
	// return true;
	// }
	//
	// return( resultat );
	//		
	// }

	public int getNbErreur() {

		return nbDerreur;
	}

}
