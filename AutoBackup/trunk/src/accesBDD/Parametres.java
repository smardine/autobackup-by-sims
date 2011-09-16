package accesBDD;

import ini_Manager.ConfigMgt;

import java.io.IOException;

import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;

public class Parametres {
	private final String USER;
	private final String PASSWORD;
	// private String serveurBD;

	private final String driverSGBD;
	private final String HOSTNAME;
	private final String EmplacementBase;

	// Constructeur
	public Parametres() {

		ConfigMgt Base = null;
		try {
			Base = new ConfigMgt("AccesBdd.ini", GestionRepertoire
					.RecupRepTravail()
					+ "\\IniFile\\", '[');
		} catch (final NullPointerException e1) {
			Historique.ecrire("fichier AccesBdd.ini introuvable");
		} catch (final IOException e1) {
			Historique.ecrire("fichier AccesBdd.ini illisible");
		}
		USER = Base.getValeurDe("user");
		PASSWORD = Base.getValeurDe("password");
		HOSTNAME = Base.getValeurDe("serveur");
		driverSGBD = "jdbc:firebirdsql";
		EmplacementBase = GestionRepertoire.RecupRepTravail()
				+ "\\Database\\AUTOBACKUP.FDB";

	}

	public String getNomUtilisateur() {
		return USER;
	}

	public String getMotDePasse() {
		return PASSWORD;
	}

	public String getEmplacementBase() {
		return EmplacementBase;
	}

	public String getDriverSGBD() {
		return driverSGBD;
	}

	public String getHostName() {
		return HOSTNAME;
	}

}
