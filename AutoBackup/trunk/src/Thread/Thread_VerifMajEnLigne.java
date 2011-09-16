package Thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import lecture_ecriture.ReadFile;
import zip.OutilsZip;
import Dialogue.Fen_Download_Maj_Appli;
import Dialogue.SendMailUsingAuthenticationWithAttachement;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.RecupDate;
import Utilitaires.VariableEnvironement;
import accesBDD.GestionDemandes;

public class Thread_VerifMajEnLigne extends Thread{
	
	protected String urlversion,urlsetup;
	protected JProgressBar progress;
	protected JLabel operation2;
	protected JLabel messageAFairePasser;

	public Thread_VerifMajEnLigne(String urlVersionIni, String urlSetupExe,JProgressBar progressBar,JLabel operation,JLabel Message) 
	{
		// TODO Auto-generated constructor stub
		
		urlversion=urlVersionIni;
		urlsetup=urlSetupExe;
		progress=progressBar;
		operation2=operation;
		messageAFairePasser = Message;
	}
	
		public void run() {
			InputStream input = null;
			FileOutputStream writeFile=null;	
			String cheminFichier=null;
			File fichier = null;
			int erreurOuverture=0;
			messageAFairePasser.setText(" Vérification de la présence d'une mise à jour");
			File setup = new File (GestionRepertoire.RecupRepTravail()+"\\setup_AutoBackup.exe");
			if (setup.exists()==true){
				boolean effacé = setup.delete();
				if (effacé==false){
					setup.deleteOnExit();
				}
			}
			
			
				try
				{
					messageAFairePasser.setText(" Récuperation de la version disponible sur le site");
					URL url = new URL(urlversion);
					URLConnection connection = url.openConnection();
					final int fileLength = connection.getContentLength();

					if ((fileLength == -1)||(fileLength==0))
					{
						System.out.println("Invalide URL or file.");
						erreurOuverture++;
						//return false;
					}
					
					input = connection.getInputStream();
					String fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
					if (fileName.contains("%20")==true){
						fileName=fileName.replaceAll("%20", " ");
					}
					if (fileName.contains("&amp;")==true){
						fileName=fileName.replaceAll("&amp;", " and ");
					}
					cheminFichier=GestionRepertoire.RecupRepTravail()+"\\"+fileName;
					
					fichier = new File (cheminFichier);
					writeFile = new FileOutputStream(cheminFichier);
					//lecture par segment de 4Mo
					byte[] buffer = new byte[4096*1024];
					int read;

					while ((read = input.read(buffer)) > 0){
						writeFile.write(buffer, 0, read);
														
					}
						
					
					writeFile.flush();
				}
				catch (IOException e)
				{
					System.out.println("Error while trying to download the file.");
					Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
					
				}
				finally
				{
					try
					{
						if (erreurOuverture==0){
							writeFile.close();
							input.close();	
						}
						
					}
					catch (IOException e)
					{
						Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
						//return false;
					}
				}
				//le telech est fini, on verifie la version hebergée sur le site
				ini_Manager.ConfigMgt versionSite = null;
				try {
					versionSite = new ini_Manager.ConfigMgt ("version.ini",GestionRepertoire.RecupRepTravail()+"\\",'[');
				} catch (NullPointerException e) {
					
					Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				} catch (IOException e) {
					
					Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				}
				String VersionDispo =versionSite.getValeurDe("version");
				ini_Manager.ConfigMgt versionInstallée = null;
				try {
					versionInstallée = new ini_Manager.ConfigMgt ("version.ini",GestionRepertoire.RecupRepTravail()+"\\IniFile\\",'[');
				} catch (NullPointerException e) {
					
					Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				} catch (IOException e) {
					
					Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
				}
				
				
				String VersionEnCours = versionInstallée.getValeurDe("version");
				boolean effacé = fichier.delete();
				if (effacé==false){
					fichier.deleteOnExit();
				}
				
				messageAFairePasser.setText(" Comparaison avec la version actuelle");
				
				if (VersionEnCours.equals(VersionDispo)==true){//la version est a jour, on regarde si on vient de faire une maj, il y a des fichier dans le repertoire "export
					try {
						messageAFairePasser.setText(" Vérification de la présence de fichier à importer");
						ImportSQLDansBDD (GestionRepertoire.RecupRepTravail()+"\\export\\",messageAFairePasser);
					} catch (IOException e) {
						
						Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
					}
				}
				
				if (VersionEnCours.equals(VersionDispo)==false){//il y a une autre version sur le site de maj
																//proposition de la maj a l'utilisateur
					int demandeMaj = JOptionPane.showConfirmDialog(null, 
							"La version "+VersionDispo+" est disponible\n" +
					" Voulez vous faire la mise à jour","?",JOptionPane.YES_NO_OPTION);		//si il repond oui, dl du setup puis execution
																//si il repond non => poursuite du programme
					if (demandeMaj==0){//maj acceptée
					
						
						/////////////////////////////////////////////////////
						///// ENVOI FICHIER TRACE AU SUPPORT A LA FERMETURE//
						/////////////////////////////////////////////////////
						
						String [] destinataire = {"s.mardine@gmail.com"};
						String from = "autobackup@laposte.net";
						String password = "gouranga08";
						String [] Files = {GestionRepertoire.RecupRepTravail()+"\\historique.txt",GestionRepertoire.RecupRepTravail()+"\\IniFile\\version.ini",GestionRepertoire.RecupRepTravail()+"\\IniFile\\AccesBdd.ini"};
						String Sujet = "Mise à jour AutoBackup";
						
						String MACHINE_NAME=VariableEnvironement.VarEnvSystem("COMPUTERNAME");
						String USERNAME=VariableEnvironement.VarEnvSystem("USERNAME");
						
											
						
						
						String Message = "L'ordinateur "+ MACHINE_NAME + "à accépté la mise à jour.\n\r" +
								"L'utilisateur qui a lancé la mise à jour est : " + USERNAME +"\n\r" +
										"La version téléchargée est : "+VersionDispo;
						SendMailUsingAuthenticationWithAttachement smtpMailSender = new SendMailUsingAuthenticationWithAttachement();
					    boolean succesEnvoiMail = false;
						try {
							succesEnvoiMail = smtpMailSender.postMail( destinataire, Sujet, Message, from, password, Files);
						} catch (MessagingException e2) {
							
							Utilitaires.Historique.ecrire ("Message d'erreur: "+e2);
						}
						
						if (succesEnvoiMail==false){//il y a eu un pb lors de l'envoi, on re essaye une fois
							
							try {
								succesEnvoiMail = smtpMailSender.postMail( destinataire, Sujet, Message, from, password, Files);
							} catch (MessagingException e2) {
								
								Utilitaires.Historique.ecrire ("Message d'erreur: "+e2);
							}
						}
						
						
						
						new Fen_Download_Maj_Appli();
						
						
					}
					if (demandeMaj==1){//maj refusée
										
						
					}
					
					
				
			}
				//return true;
			
	
		}

		private void ImportSQLDansBDD(String chemin,JLabel message) throws IOException {
		
		File Sauvegarde = new File (chemin+"\\Sauvegarde.export");
		File Fichier = new File (chemin+"\\Fichier.export");
		File ListeFichier = new File (chemin+"\\ListeFichier.export");
		File CheminSauvegarde = new File (chemin+"\\CheminSauvegarde.export");
		File CheminExclut = new File (chemin+"\\Exclut.export");
		File Horaire = new File (chemin+"\\Horaire.export");
		File Planif = new File (chemin+"\\Planif.export");
		
		
		if (Sauvegarde.exists()==true||Fichier.exists()==true||ListeFichier.exists()==true||CheminSauvegarde.exists()==true||CheminExclut.exists()==true||Horaire.exists()==true||Planif.exists()==true){
			message.setText("Compression des fichier .export");
			String Date = RecupDate.dateEtHeure();
			String FileName = Date+"_ImportAutoBackup.zip";
			File RepArchive = new File (GestionRepertoire.RecupRepTravail()+"\\archives\\");
			if (RepArchive.exists()==false){
				RepArchive.mkdirs();
			}
			try {
				OutilsZip.zipDir(chemin, GestionRepertoire.RecupRepTravail()+"\\archives\\"+FileName);
			} catch (FileNotFoundException e) {
				
				Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
			} catch (IOException e) {
				
				Utilitaires.Historique.ecrire ("Message d'erreur: "+e);
			}
		}
		
		
		
		if (Horaire.exists()==true){
			String DeleteTable = "delete from HORAIRE";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\Horaire.export");
				message.setText("insertion dans la table Horaire");
				int nbDimport = ReadFile.HoraireReadLineEtInsereEnBase(chemin+"\\Horaire.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Horaire.delete();
					if (succes1==false){
						Horaire.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Horaire(s) importé(s)");
				}
			}
		}
		if (Planif.exists()==true){
			String DeleteTable = "delete from PLANIF";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\Planif.export");
				message.setText("insertion dans la table PLANIF");
				int nbDimport = ReadFile.PlanifReadLineEtInsereEnBase(chemin+"\\Planif.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Horaire.delete();
					if (succes1==false){
						Horaire.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Planif(s) importée(s)");
				}
			}
		}
		if (Sauvegarde.exists()==true){
			String DeleteTable = "delete from SAUVEGARDE";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\Sauvegarde.export");
				message.setText("insertion dans la table SAUVEGARDE");
				int nbDimport = ReadFile.SauvegardeReadLineEtInsereEnBase(chemin+"\\Sauvegarde.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Sauvegarde.delete();
					if (succes1==false){
						Sauvegarde.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Sauvegarde(s) importée(s)");
				}
			}
			
			
			
		}
		if (Fichier.exists()==true){
			String DeleteTable = "delete from FICHIER";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				message.setText("insertion dans la table FICHIER");
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\Fichier.export");
				int nbDimport = ReadFile.FichierReadLineEtInsereEnBase(chemin+"\\Fichier.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Fichier.delete();
					if (succes1==false){
						Fichier.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Fichier(s) exclut(s) importé(s)");
				}
			}
			

		}
		if (ListeFichier.exists()==true){
			String DeleteTable = "delete from LISTE_FICHIER";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				message.setText("insertion dans la table LISTE_FICHIER");
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\ListeFichier.export");
				int nbDimport = ReadFile.ListeFichierReadLineEtInsereEnBase(chemin+"\\ListeFichier.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Fichier.delete();
					if (succes1==false){
						Fichier.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Liste de fichier(s)importée(s)");
				}
			}
			

		}
		if (CheminSauvegarde.exists()==true){
			String DeleteTable = "delete from CHEMIN_SAUVEGARDE";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				message.setText("insertion dans la table CHEMIN_SAUVEGARDE");
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\CheminSauvegarde.export");
				int nbDimport = ReadFile.CheminSauvegardeFichierReadLineEtInsereEnBase(chemin+"\\CheminSauvegarde.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Fichier.delete();
					if (succes1==false){
						Fichier.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Chemin de sauvegarde importé(s)");
				}
			}
			

		}
		
		if (CheminExclut.exists()==true){
			String DeleteTable = "delete from LISTE_EXCLUT";
			boolean succes = GestionDemandes.executeRequete(DeleteTable);
			if (succes==true){
				message.setText("insertion dans la table LISTE_EXCLUT");
				int nbligneAimporter = ReadFile.compteNbLigne(chemin+"\\Exclut.export");
				int nbDimport = ReadFile.ExclutReadLineEtInsereEnBase(chemin+"\\Exclut.export",nbligneAimporter,progress);
				if (nbDimport==nbligneAimporter){//on a eu autant d'import que de ligne a importer, tt s'est donc bien passé.
					boolean succes1 = Sauvegarde.delete();
					if (succes1==false){
						Sauvegarde.deleteOnExit();
					}
					Historique.ecrire(nbDimport +"Sauvegarde(s) importée(s)");
				}
			}
			
			
			
		}
		
		
		
	}

}
