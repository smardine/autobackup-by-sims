package Utilitaires;

import java.io.IOException;
import java.util.Properties;

public class Internet {

	/**
	 * Ouvre une url dans le navigateur par defaut
	 * @param AdresseSite -String
	 * @return vrai si Ok, faux sinon
	 */

	public static boolean OuvrePageInternet(final String AdresseSite) {

		final Properties sys = System.getProperties();
		final String os = sys.getProperty("os.name");
		final Runtime r = Runtime.getRuntime();
		try {
			if (os.endsWith("NT") || os.endsWith("2000") || os.endsWith("XP")) {
				r.exec("cmd /c start " + AdresseSite);
			} else {
				r.exec("cmd /c start " + AdresseSite);
			}
		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
			return false;
		}
		return true;

	}

	/**
	 * Ouvre une url dans internet explorer specifiquement
	 * @param Adresse -String
	 * @return vrai si Ok, faux sinon
	 */
	public static boolean OuvrePageInternetExplorer(final String Adresse) {
		final Properties sys = System.getProperties();
		final String os = sys.getProperty("os.name");
		final Runtime r = Runtime.getRuntime();
		try {
			if (os.endsWith("NT") || os.endsWith("2000") || os.endsWith("XP")) {
				r.exec("cmd /c start iexplore " + Adresse);
			} else {
				r.exec("cmd /c start iexplore " + Adresse);
			}
		} catch (final IOException ex) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + ex);
			return false;
		}

		return true;

	}

}
