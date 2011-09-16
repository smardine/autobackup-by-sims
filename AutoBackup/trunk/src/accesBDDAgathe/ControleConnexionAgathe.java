package accesBDDAgathe;

// importation des classes pour JDBC

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ControleConnexionAgathe {
	// PROPRIETES
	static ParametresAgathe lesParametres;
	static boolean etatConnexion;
	static Connection laConnectionStatiqueAgathe;
	static {
		boolean ok = true;

		lesParametres = new ParametresAgathe();

		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			etatConnexion = true;
		} catch (final ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Classes non trouvées"
					+ " pour le chargement du pilote Firebird", "ALERTE",
					JOptionPane.ERROR_MESSAGE);
			ok = false;
			etatConnexion = false;
		}

		if (ok == true) {
			try {
				// récupération des paramètres présents dans la classe
				// Parametres
				// String urlBD = lesParametres.getServeurBD();
				final String nomUtilisateur = lesParametres.getNomUtilisateur();
				final String MDP = lesParametres.getMotDePasse();
				final String DriverSGBD = lesParametres.getDriverSGBD();
				final String Emplacement = lesParametres.getEmplacementBase();
				final String Serveur = lesParametres.getHostName();

				final String UrlDeConnexion = DriverSGBD + ":" + Serveur + ":"
						+ Emplacement + "?encoding=UTF8&user=" + nomUtilisateur
						+ "&password=" + MDP;

				laConnectionStatiqueAgathe = DriverManager
						.getConnection(UrlDeConnexion);
				laConnectionStatiqueAgathe.setAutoCommit(false);

				// Connection laConnectionStatique =
				// DriverManager.getConnection("jdbc:firebirdsql:MICPTDVP:outilhotline?encoding=UTF8&user=sysdba&password=masterkey");

				// laConnectionStatiqueAgathe =
				// DriverManager.getConnection(urlBD, nomUtilisateur, MDP);
				etatConnexion = true;
			}

			catch (final Exception e) {
				JOptionPane.showMessageDialog(null,
						"Impossible de se connecter"
								+ " à la base de données\n\r" + e, "ALERTE",
						JOptionPane.ERROR_MESSAGE);
				etatConnexion = false;
				JOptionPane
						.showMessageDialog(
								null,
								"     Pour des raisons de sécurité, le programme va maintenant être fermé"
										+ " \n\r  il faudra le relancer et vérifier les login, mot de passe, nom du serveur et chemin de la base de données avant de lancer l'export",
								"ALERTE", JOptionPane.ERROR_MESSAGE);

				System.exit(0);
			}
		}
	}

	// CONSTRUCTEUR
	public ControleConnexionAgathe() {
	}

	// METHODES
	// les accesseurs statiques
	// ------------------------
	public static ParametresAgathe getParametres() {
		return lesParametres;
	}

	public static boolean getControleConnexion() {
		return etatConnexion;
	}

	public static Connection getConnexion() {
		return laConnectionStatiqueAgathe;
	}

	// les autres méthodes
	// -------------------
	public static boolean controle(final String Nom, final String MotDePasse) {
		boolean verificationSaisie;
		if (Nom.equals(lesParametres.getNomUtilisateur())
				&& MotDePasse.equals(lesParametres.getMotDePasse())) {
			verificationSaisie = true;
		} else {
			JOptionPane.showMessageDialog(null, "Vérifier votre saisie.",
					"ERREUR", JOptionPane.ERROR_MESSAGE);
			verificationSaisie = false;
		}
		return verificationSaisie;
	}

	public static void fermetureSession() {
		try {
			laConnectionStatiqueAgathe.close();
		} catch (final SQLException e) {
			JOptionPane.showMessageDialog(null, "Problème rencontré"
					+ "à la fermeture de la connexion", "ERREUR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean getControleSaisie() {

		return true;
	}

}