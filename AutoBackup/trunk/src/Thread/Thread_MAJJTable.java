package Thread;

import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import accesBDD.GestionDemandes;





//protected TYPE xxx

public class Thread_MAJJTable extends Thread{
	//tte les decalration necessaire...
	protected JTable TABLE;
	protected JLabel NUMCLIENT;
	protected JTextField NOM,PRENOM,ADRESSE,CP,VILLE,TEL,GSM;
	protected JTextField SECTEUR;
	protected JTextArea COMMENTAIRES;
	//protected AnimatedPanel animation;
	//protected JPanel panelAnimation;
	
	/**
	 * Affiche les differentes etapes du demarrage du logiciel,
	 * verifie certaines choses.
	 * 
	 * @param Fenetre -JFrame pour l'affichage des resultats
	 * @param operation_jLabel -JLabel message pour l'utilisateur
	 * @param jTextField -JTextField message pour l'utilisateur
	 * @param jProgressBar -JProgressBar pour la progression
	 */
	
	public Thread_MAJJTable(JTable table,JLabel numclient,JTextField nom,JTextField prenom,JTextField adresse,JTextField cp,JTextField ville,JTextField tel,JTextField gsm,
			JTextField secteur,JTextArea commentaires ){

		//on met les equivalence ici
	
		TABLE = table;
		NUMCLIENT = numclient;
		NOM = nom;
		PRENOM=prenom;
		ADRESSE=adresse;
		CP=cp;
		VILLE=ville;
		TEL=tel;
		GSM=gsm;
		SECTEUR=secteur;
		COMMENTAIRES=commentaires;
		
	}
	
	
	
	
	public void run(){
		
		{
			//System.out.println("mouseClicked()"); // TODO Auto-generated Event stub mouseClicked()
			
			//int LigneChoisie=jTable.getSelectedRow();
			
			int nbColonnes = TABLE.getColumnCount();
			String [] value = null;
			String ValTempo=null;
			for (int i=0;i<nbColonnes;i++){
				if (i>0){
					ValTempo = ValTempo+TABLE.getValueAt(TABLE.getSelectedRow(), i).toString()+";";
				}
				else{
					ValTempo = TABLE.getValueAt(TABLE.getSelectedRow(), i).toString()+";";
				}
				
			}
			value=ValTempo.split(";");
			
			String Nom = value[0];
			String Adresse = value[1];
			String Cp = value[2];
			String Ville = value[3];
			String Tel = value[4];
			String Gsm = value[5];
			int ID_SECTEUR = Integer.parseInt(value[6]);
			String Commentaire =value[7];
			String Pnom = value[8];
			String ID=value[9];
			
			
			String RechSecteur = "SELECT SECTEUR FROM SECTEUR where ID_SECTEUR='"+ID_SECTEUR+"'";
			String Secteur = null;
			try {
				Secteur = GestionDemandes.executeRequeteEtRetourne1Champ(RechSecteur);
			} catch (SQLException e1) {
				
				Utilitaires.Historique.ecrire ("Message d'erreur: "+e1);
			}
			
			//String CATEGORIE = value[0];
			NUMCLIENT.setText(ID);
			NOM.setText(Nom);
			PRENOM.setText(Pnom);
			ADRESSE.setText(Adresse);
			CP.setText(Cp);
			VILLE.setText(Ville);
			TEL.setText(Tel);
			GSM.setText(Gsm);
			
			SECTEUR.setText(Secteur);			
			
			COMMENTAIRES.setText(Commentaire);
			//String CATEGORIE = CatégoriejComboBox.getSelectedItem().toString();
			
			
			
		}
				
	}

	
}




