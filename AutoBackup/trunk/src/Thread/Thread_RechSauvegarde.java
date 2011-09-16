package Thread;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Enum.EnChoixRecherche;
import accesBDD.GestionDemandes;

public class Thread_RechSauvegarde extends Thread {

	private JTable table;
	private DefaultTableModel model;

	private JTextField text;
	private JProgressBar progressBar;
	private JComboBox comboBox;
	private int tailleMax;

	public Thread_RechSauvegarde(JTable p_table, DefaultTableModel p_model,
			JComboBox p_comboBox, JTextField p_text, JProgressBar p_progressBar) {

		table = p_table;
		model = p_model;
		comboBox = p_comboBox;
		text = p_text;
		progressBar = p_progressBar;
		progressBar.setVisible(true);
	}

	public void run() {
		progressBar.setIndeterminate(true);
		progressBar.setString("Recherche en cours...");

		int nbcolonne = model.getColumnCount();
		if (nbcolonne != 0) {// si il y a des colonnes, on
			// reinitialise le model
			model = new DefaultTableModel();
			table.setModel(model);
		}

		if (model.getColumnCount() == 0) {
			// si il n'y a pas deja des colonnes
			// on les crées
			model.addColumn("ID");
			model.addColumn("Date sauvegarde");
			model.addColumn("Emplacement sauvegarde");
			model.addColumn("Date de modif. du fichier");
			model.addColumn("Emplacement original");

			TableColumn column0 = table.getColumnModel().getColumn(0);// id
																		// sauvegarde
			TableColumn column1 = table.getColumnModel().getColumn(1);// date
																		// sauvegarde
			TableColumn column2 = table.getColumnModel().getColumn(2);// chemin
																		// sauvegarde
			TableColumn column3 = table.getColumnModel().getColumn(3);// date
																		// modif
																		// fichier
			TableColumn column4 = table.getColumnModel().getColumn(4);// chemin
																		// original
																		// fichier
			column0.setPreferredWidth(75);
			column1.setPreferredWidth(150);
			column2.setPreferredWidth(500);
			column3.setPreferredWidth(150);
			column4.setPreferredWidth(500);
		}

		StringBuilder sb = new StringBuilder();
		sb
				.append("SELECT a.ID_SAUVEGARDE,b.DATE_SAUVEGARDE,b.EMPLACEMENT_SAUVEGARDE,a.DATE_FICHIER,a.EMPLACEMENT_FICHIER FROM FICHIER a "
						+ "left join SAUVEGARDE b on (a.ID_SAUVEGARDE=b.ID_SAUVEGARDE) WHERE a.EMPLACEMENT_FICHIER ");

		String idRechercheSelectionne = (String) comboBox.getSelectedItem();

		if (idRechercheSelectionne.equals(EnChoixRecherche.EXACTE.getLib())) {
			sb.append("= '" + text.getText() + "'");
		}

		if (idRechercheSelectionne.equals(EnChoixRecherche.COMMENCE.getLib())) {
			sb.append("LIKE '" + text.getText() + "%'");
		}

		if (idRechercheSelectionne.equals(EnChoixRecherche.CONTIENT.getLib())) {
			sb.append("LIKE '%" + text.getText() + "%'");
		}
		if (idRechercheSelectionne.equals(EnChoixRecherche.FINI.getLib())) {
			sb.append("LIKE '%" + text.getText() + "'");
		}

		String requete = sb.toString();
		ArrayList<Vector<String>> lst = GestionDemandes
				.executeRequeteEtRetourneUneListeVector(requete, table);

		for (Vector<String> v : lst) {
			model.insertRow(0, v);
			int tailleActu = v.size();
			if (tailleActu > tailleMax) {
				tailleMax = tailleActu;
				table.getColumnModel().getColumn(4).setPreferredWidth(
						tailleMax + 700);
			}
		}

		progressBar.setIndeterminate(false);
		progressBar.setString(lst.size() + " Element(s) trouvé(s)");

	}

}
