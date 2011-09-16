package Utilitaires;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class CopyAuto {
	// Dans le contructeur on va utiliser notre methode copy
	// et donc on vas faire quelques ptit test
	protected String src, dest;
	File DEST, SRC;
	int nbTotal;
	int nbDerreur = 0;

	/**
	 * Copie le contenu d'un repertoire vers un autre et affiche le status de la
	 * copie dans une barre de progression.
	 * @param src -String Le répertoire source
	 * @param dest -String le repertoire de destination
	 * @param nbTotal -int le nb total de fichier a copier qui permet de
	 *            calculer la progression.
	 * @param progress -JProgressBar la barre de progression.
	 * @param sortieModel -DefaultModelList model de liste
	 * @param sortieList -JList le composant JList.
	 * @throws SQLException
	 * @throws IOException
	 */

	public CopyAuto(final String src, final String dest, final int nbTotal,
			final JProgressBar progressEnCours,
			final JProgressBar progressTotal, final String RepRacineLocal,
			final JLabel label) throws SQLException, IOException {

		this.nbTotal = nbTotal;
		this.src = src;
		this.dest = dest;
		this.SRC = new File(src);
		this.DEST = new File(dest);
		// ben si le rep dest n'existe pas, et notre source est un repertoire
		if (!DEST.exists()) {
			if (SRC.isDirectory()) {
				// Alors on cree un rep destination

				Historique.ecrire("Création du répértoire : " + DEST);

				DEST.mkdir();
				// DEST.deleteOnExit();
				// nbEncours++;
			}
		}
		// Mais si jammais c'est un fichier, on fait un simple copie
		if (SRC.isFile()) {

			final long tailleSource = SRC.length();
			boolean succes = false;
			if (tailleSource > 100000) {
				succes = copyAvecProgress(SRC, DEST, progressEnCours);
			} else {
				succes = copyAvecProgressNIO(SRC, DEST, progressEnCours);
			}
			if (succes == false) {
				nbDerreur++;

				Historique.ecrire("Erreur lors de la copie du fichier : " + SRC
						+ " vers : " + DEST);

			}
		}

		// et si notre source est un repertoire qu'on doit copié!!!
		else if (SRC.isDirectory()) {
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
							final int nbEncours = count.getNbFichier();

							final int PourcentProgression = (100 * (nbEncours + 1))
									/ nbTotal;
							label.setText("Copie de " + nbEncours
									+ " fichier(s)  / sur " + nbTotal
									+ " au total");

							progressTotal.setValue(PourcentProgression / 2);
							progressTotal.setString("Total : "
									+ PourcentProgression / 2 + " %");

						}
					});
				} catch (final InterruptedException e) {
					Historique.ecrire("InvocationTargetException " + e);
				} catch (final InvocationTargetException e) {
					Historique.ecrire("InvocationTargetException " + e);
				}

				new CopyAuto(f.getAbsolutePath(), DEST.getAbsoluteFile() + "/"
						+ f.getName(), nbTotal, progressEnCours, progressTotal,
						RepRacineLocal, label);

			}
		}

	}

	private boolean copyAvecProgressNIO(final File sRC2, final File dEST2,
			final JProgressBar progressEnCours) throws IOException {
		boolean resultat = false;

		final FileInputStream fis = new FileInputStream(sRC2);
		final FileOutputStream fos = new FileOutputStream(dEST2);

		final java.nio.channels.FileChannel channelSrc = fis.getChannel();
		final java.nio.channels.FileChannel channelDest = fos.getChannel();
		progressEnCours.setValue(0);

		progressEnCours.setString(sRC2 + " : 0 %");
		channelSrc.transferTo(0, channelSrc.size(), channelDest);
		progressEnCours.setValue(100);
		progressEnCours.setString(sRC2 + " : 100 %");
		if (channelSrc.size() == channelDest.size()) {
			resultat = true;
		} else {
			resultat = false;
		}
		fis.close();
		fos.close();
		return (resultat);

	}

	/**
	 * Permet de fixer la date systeme en fonction de la date de création d'un
	 * fichier
	 * @param cheminDuFichier -String le fichier dont on se sert pour fixer la
	 *            date Systeme
	 */
	public static void FixeDateSystemeALaDateDeCreationDuFichier(
			final String cheminDuFichier) {

		// on créer la commande qui servira a recuperer la date du fichier

		final Runtime r = Runtime.getRuntime();
		final String cmdRecupDate = String.format(
				"cmd.exe /c dir /TC %s | find \"/\"  > tmp.txt",
				cheminDuFichier);
		try {
			final Process p = r.exec(cmdRecupDate);
			try {
				p.waitFor();
			} catch (final InterruptedException e) {

				Historique.ecrire("InvocationTargetException " + e);
			}
		} catch (final IOException e) {

			Historique.ecrire("InvocationTargetException " + e);
		}
		// on extrait la date systeme du fichier text et on fixe la date systeme
		final String cmdSetDate = String
				.format("cmd.exe /c FOR /F \"tokens=1-4 delims= \" %%i in (tmp.txt) do DATE %%i");
		try {
			final Process p = r.exec(cmdSetDate);
			try {
				p.waitFor();
			} catch (final InterruptedException e) {

				Historique.ecrire("InvocationTargetException " + e);
			}
		} catch (final IOException e) {

			Historique.ecrire("InvocationTargetException " + e);
		}

		final String cmdEffaceTmpText = String.format("cmd.exe /c del tmp.txt");
		try {
			final Process p = r.exec(cmdEffaceTmpText);
			try {
				p.waitFor();
			} catch (final InterruptedException e) {

				Historique.ecrire("InvocationTargetException " + e);
			}
		} catch (final IOException e) {

			Historique.ecrire("InvocationTargetException " + e);
		}
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
				progressEnCours.setString(sRC2.getName() + " : " + Pourcent
						+ " %");
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

	public int getNbErreur() {

		return nbDerreur;
	}

}
