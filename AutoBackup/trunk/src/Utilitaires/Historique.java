package Utilitaires;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Historique {
	/**
	 * ecrire une info dans le fichier historique.txt
	 * @param p_text -String L'info souhaitée.
	 */
	public static void ecrire(final String p_text) {
		// String Date = RecupDate.date();

		final Date actuelle = new Date();
		final DateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss");
		final String Date = dateFormat.format(actuelle);

		final String repTravail = GestionRepertoire.RecupRepTravail();
		final String ligne = "Le " + Date + "   " + p_text + "\r\n";

		FileWriter writer = null;
		try {
			writer = new FileWriter(repTravail + "/historique.txt", true);
			writer.write(ligne, 0, ligne.length());

		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException e) {
					System.out.println(e);
				}
			}
		}

	}

	/**
	 * ouvrir le fichier historique.txt avec le programme par defaut du systeme.
	 */
	public static void lire() {
		final String repTravail = GestionRepertoire.RecupRepTravail();
		OpenWithDefaultViewer.open(repTravail + "/historique.txt");
	}

}
