package lecture_ecriture;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class WriteFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Ecrire le contenu d'un String dans un fichier
	 * @param contenu -String ce qu'il faut ecrire
	 * @param chemin -String le chemin du fichier
	 */

	public WriteFile(final String contenu, final String chemin) {

		try {
			final BufferedWriter out = new BufferedWriter(
					new FileWriter(chemin));
			out.write(contenu);
			out.close();
		} catch (final IOException e) {
		}
	}

	/**
	 * Ecrire une ligne dans un fichier
	 * @param ligneAEcrire -String ce qu'il faut ecrire
	 * @param CheminDuFichier -String le chemin du fichier
	 */
	public static void WriteLineInNewFile(final String ligneAEcrire,
			final String CheminDuFichier) throws IOException {
		FileWriter writer = null;
		final String texte = (ligneAEcrire);

		try {
			writer = new FileWriter(CheminDuFichier, false);
			writer.write(texte, 0, texte.length());

		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Ecrire une ligne dans un fichier
	 * @param ligneAEcrire -String ce qu'il faut ecrire
	 * @param CheminDuFichier -String le chemin du fichier
	 */
	public static void WriteLine(final String ligneAEcrire,
			final String CheminDuFichier) throws IOException {
		FileWriter writer = null;
		final String texte = (ligneAEcrire + "\n");

		try {
			writer = new FileWriter(CheminDuFichier, true);
			writer.write(texte, 0, texte.length());

		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Ecrire une ligne dans un fichier
	 * @param ligneAEcrire -Vector ce qu'il faut ecrire (idem qu'un string mais
	 *            valeur separée par des ;
	 * @param CheminDuFichier -String le chemin du fichier
	 */
	public static void WriteLineVector(final Vector<String> ligneAEcrire,
			final String CheminDuFichier) {

		FileWriter writer = null;
		final String texte = ("\n" + ligneAEcrire + "\n");

		try {
			writer = new FileWriter(CheminDuFichier, true);
			writer.write(texte, 0, texte.length());

		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException e) {

					Utilitaires.Historique.ecrire("Message d'erreur: " + e);
				}
			}
		}
	}

}
