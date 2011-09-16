package Dialogue;

import ini_Manager.ConfigMgt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import Thread.CopyOfThread_Sauvegarde;
import Thread.Thread_RefreshUI;
import Utilitaires.GestionRepertoire;
import Utilitaires.Historique;
import Utilitaires.ManipFichier;
import Utilitaires.RecupDate;
import Utilitaires.VariableEnvironement;
import accesBDD.GestionDemandes;

public class FEN_Princ extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel Panel_Sauvegarde = null;
	private JScrollPane jScrollPane = null;
	private JList jList_Ajout = null;
	private JButton jButton_AjoutDossier = null;
	private JButton jButton_AjoutFichier = null;
	private JButton jButton_EffaceListe = null;
	private JButton jButton_EffaceLigne = null;
	private JTextField jTextField_CheminSauvegarde = null;
	private JButton jButton_ParcourirSauvegarde = null;
	private JLabel jLabel = null;
	private JLabel jLabel_Date = null;
	private JLabel jLabel_Version = null;
	private JButton jButton_Pause = null;
	private JButton jButton_Go = null;
	private JButton jButton_Arret = null;
	private JProgressBar jProgressBar_EnCours = null;
	private JProgressBar jProgressBar_Total = null;
	protected DefaultListModel ModeleDeListe, ModeleDeListeVisu,
			ModeleDeListeExclu;
	private JButton jButton_Enregistre_ListeDossier = null;
	private JTabbedPane jTabbedPane = null;
	private JLabel jLabel_Operation = null;
	private CopyOfThread_Sauvegarde save;

	private JPanel jPanel = null;
	private JScrollPane jScrollPane1 = null;
	private JList jList_Visu = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel_Date_Sauvegarde = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel_Taille_Sauvegarde = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel_ID_Sauvegarde = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JButton jButton3 = null;
	private JScrollPane jScrollPane2 = null;
	private JList jList_Exclut = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JButton jButton_AjoutDossierExclut = null;
	private JButton jButton_AjoutFichierExclut = null;
	private JButton jButton_EffaceListeExclut = null;
	private JButton jButton_EffaceLigneExclut = null;
	private JButton jButton4_SaveExclusion = null;
	private JButton jButton5_Refresh = null;
	private JPanel jPanel1 = null;
	private JCheckBox jCheckBoxLundi = null;
	private JCheckBox jCheckBoxMardi = null;
	private JCheckBox jCheckBoxMercredi = null;
	private JCheckBox jCheckBoxJeudi = null;
	private JCheckBox jCheckBoxVendredi = null;
	private JCheckBox jCheckBoxSamedi = null;
	private JCheckBox jCheckBoxDimanche = null;
	private JButton jButton6 = null;
	private JTextField jTextFieldHeure = null;
	private JTextField jTextFieldMinutes = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JTabbedPane jTabbedPane1 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private int enpause = 0;
	private JButton jButton_SauvegardeOk = null;
	private JButton jButton_SauvegardeNok = null;
	private JCheckBox jCheckBox_ArretMachine = null;
	private JCheckBox jCheckBox_EnvoiMailAuSt = null;
	private JButton jButtonRechercheDansSauvegarde = null;

	/**
	 * This is the default constructor
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	public FEN_Princ() {
		super();
		ModeleDeListe = new DefaultListModel();
		ModeleDeListeVisu = new DefaultListModel();
		ModeleDeListeExclu = new DefaultListModel();
		initialize();

		Thread_RefreshUI t = new Thread_RefreshUI(jLabel_Date, jLabel_Version,
				jList_Ajout, ModeleDeListe, jList_Exclut, ModeleDeListeExclu,
				jList_Visu, ModeleDeListeVisu, jTextField_CheminSauvegarde,
				jTextFieldHeure, jTextFieldMinutes, jCheckBox_EnvoiMailAuSt,
				jCheckBox_ArretMachine, jCheckBoxLundi, jCheckBoxMardi,
				jCheckBoxMercredi, jCheckBoxJeudi, jCheckBoxVendredi,
				jCheckBoxSamedi, jCheckBoxDimanche);
		t.start();

		this.setVisible(true);

	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(900, 632);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/LogoPrincipal.png")));
		this.setResizable(false);
		this.setLocationRelativeTo(null); // On centre la fenêtre sur l'écran
		this.setContentPane(getJContentPane());
		this.setTitle("AutoBackup");
		this.setVisible(true);
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(final java.awt.event.WindowEvent e) {

				exit();

			}
		});
		this.setLocationRelativeTo(null);

	}

	@SuppressWarnings("deprecation")
	protected void exit() {
		final File enCours = new File(GestionRepertoire.RecupRepTravail()
				+ "/enCours.txt");

		if ((save != null) && (enCours.exists())) {
			final int fermetureDemandée = JOptionPane
					.showConfirmDialog(
							this,
							"Voulez-vous fermer le programme ? \n\r Une sauvegarde est en cours... ",
							"", JOptionPane.YES_NO_OPTION); // si il repond oui,
			// dl du setup puis
			// execution
			// si il repond non => poursuite du programme
			if (fermetureDemandée == 0) {// arret acceptée
				if (save != null) {// la sauvegarde est encore en cours et
					// l'utilisateur demande a fermer le
					// logiciel
					save.stop();
					int ID_SAUVEGARDE = 0;
					try {
						ID_SAUVEGARDE = Integer
								.parseInt(GestionDemandes
										.executeRequeteEtRetourne1Champ("SELECT MAX (ID_SAUVEGARDE) FROM SAUVEGARDE"));
					} catch (final NumberFormatException e1) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e1);
					} catch (final SQLException e1) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e1);
					}
					String RequetteDelete = "DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE="
							+ ID_SAUVEGARDE;
					final boolean succes1 = GestionDemandes
							.executeRequete(RequetteDelete);

					RequetteDelete = "DELETE FROM FICHIER WHERE ID_SAUVEGARDE="
							+ ID_SAUVEGARDE;
					final boolean succes2 = GestionDemandes
							.executeRequete(RequetteDelete);

					if (succes1 && succes2) {

						Historique
								.ecrire("Effacement des infos de la sauvegarde dans la base de données réussi.");

					}
					if (!succes1 || !succes2) {

						Historique
								.ecrire("Effacement des infos de la sauvegarde dans la base de données échoué.");

					}

					final File fbTexte = new File(GestionRepertoire
							.RecupRepTravail()
							+ "\\fbserver.txt");
					final boolean succesDelete = fbTexte.delete();
					if (succesDelete == false) {
						fbTexte.deleteOnExit();
					}

					if (jCheckBox_EnvoiMailAuSt.isSelected()) {
						// ///////////////////////////////////////////////////
						// /// ENVOI FICHIER TRACE AU SUPPORT A LA FERMETURE//
						// ///////////////////////////////////////////////////

						final String[] destinataire = { "s.mardine@gmail.com" };
						final String from = "autobackup@laposte.net";
						final String password = "gouranga08";
						final String[] Files = {
								GestionRepertoire.RecupRepTravail()
										+ "\\historique.txt",
								GestionRepertoire.RecupRepTravail()
										+ "\\IniFile\\version.ini",
								GestionRepertoire.RecupRepTravail()
										+ "\\IniFile\\AccesBdd.ini" };
						final String Sujet = "Utilisation AutoBackup";

						final String MACHINE_NAME = VariableEnvironement
								.VarEnvSystem("COMPUTERNAME");
						final String USERNAME = VariableEnvironement
								.VarEnvSystem("USERNAME");

						ConfigMgt config = null;
						try {
							config = new ConfigMgt("version.ini",
									GestionRepertoire.RecupRepTravail()
											+ "\\IniFile\\", '[');
						} catch (final NullPointerException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						} catch (final IOException e1) {
							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						}
						final String VERSION = config.getValeurDe("version");

						final String Message = "L'ordinateur "
								+ MACHINE_NAME
								+ "à utilisé ce logiciel.\n\r"
								+ "L'utilisateur qui a lancé le logiciel est : "
								+ USERNAME + "\n\r"
								+ "La version utlilisée est : " + VERSION;
						final SendMailUsingAuthenticationWithAttachement smtpMailSender = new SendMailUsingAuthenticationWithAttachement();
						boolean succesEnvoiMail = false;
						try {
							succesEnvoiMail = smtpMailSender.postMail(
									destinataire, Sujet, Message, from,
									password, Files);
						} catch (final MessagingException e2) {
							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e2);
						}

						if (succesEnvoiMail == false) {// il y a eu un pb lors
							// de l'envoi, on re
							// essaye une fois

							try {
								succesEnvoiMail = smtpMailSender.postMail(
										destinataire, Sujet, Message, from,
										password, Files);
							} catch (final MessagingException e2) {
								Utilitaires.Historique
										.ecrire("Message d'erreur: " + e2);
							}
						}
					}

					this.setVisible(false);
					new Fen_Fermeture();
				}
			}
		}

		else {
			final int fermetureDemandée1 = JOptionPane.showConfirmDialog(this,
					"Voulez-vous fermer le programme ? \n\r ", "",
					JOptionPane.YES_NO_OPTION); // si il repond oui, dl du setup
			// puis execution
			// si il repond non => poursuite du programme
			if (fermetureDemandée1 == 0) {
				final File fbTexte = new File(GestionRepertoire
						.RecupRepTravail()
						+ "\\fbserver.txt");
				final boolean succesDelete = fbTexte.delete();
				if (succesDelete == false) {
					fbTexte.deleteOnExit();
				}

				if (jCheckBox_EnvoiMailAuSt.isSelected()) {
					// ///////////////////////////////////////////////////
					// /// ENVOI FICHIER TRACE AU SUPPORT A LA FERMETURE//
					// ///////////////////////////////////////////////////

					final String[] destinataire = { "s.mardine@gmail.com" };
					final String from = "autobackup@laposte.net";
					final String password = "gouranga08";
					final String[] Files = {
							GestionRepertoire.RecupRepTravail()
									+ "\\historique.txt",
							GestionRepertoire.RecupRepTravail()
									+ "\\IniFile\\version.ini",
							GestionRepertoire.RecupRepTravail()
									+ "\\IniFile\\AccesBdd.ini" };
					final String Sujet = "Utilisation AutoBackup";

					final String MACHINE_NAME = VariableEnvironement
							.VarEnvSystem("COMPUTERNAME");
					final String USERNAME = VariableEnvironement
							.VarEnvSystem("USERNAME");

					ConfigMgt config = null;
					try {
						config = new ConfigMgt("version.ini", GestionRepertoire
								.RecupRepTravail()
								+ "\\IniFile\\", '[');
					} catch (final NullPointerException e1) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e1);
					} catch (final IOException e1) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e1);
					}
					final String VERSION = config.getValeurDe("version");

					final String Message = "L'ordinateur " + MACHINE_NAME
							+ "à utilisé ce logiciel.\n\r"
							+ "L'utilisateur qui a lancé le logiciel est : "
							+ USERNAME + "\n\r" + "La version utlilisée est : "
							+ VERSION;
					final SendMailUsingAuthenticationWithAttachement smtpMailSender = new SendMailUsingAuthenticationWithAttachement();
					boolean succesEnvoiMail = false;
					try {
						succesEnvoiMail = smtpMailSender.postMail(destinataire,
								Sujet, Message, from, password, Files);
					} catch (final MessagingException e2) {

						Utilitaires.Historique
								.ecrire("Message d'erreur: " + e2);
					}

					if (succesEnvoiMail == false) {// il y a eu un pb lors de
						// l'envoi, on re essaye une
						// fois

						try {
							succesEnvoiMail = smtpMailSender.postMail(
									destinataire, Sujet, Message, from,
									password, Files);
						} catch (final MessagingException e2) {
							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e2);
						}
					}
				}

				this.setVisible(false);
				new Fen_Fermeture();
			}
		}
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTabbedPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes Panel_Sauvegarde
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanel_Sauvegarde() {
		if (Panel_Sauvegarde == null) {
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(447, 57, 419, 22));
			jLabel5.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel5.setText(" Dossier(s) / Fichier(s) à exclure");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(9, 57, 426, 22));
			jLabel4.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel4.setText(" Dossier(s) / Fichier(s) à sauvegarder");
			jLabel_Operation = new JLabel();
			jLabel_Operation.setBounds(new Rectangle(11, 530, 662, 22));
			jLabel_Operation.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel_Operation.setText("");
			jLabel_Version = new JLabel();
			jLabel_Version.setBounds(new Rectangle(690, 527, 73, 26));
			jLabel_Version.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel_Version.setText(" Version");
			jLabel_Date = new JLabel();
			jLabel_Date.setBounds(new Rectangle(777, 528, 87, 26));
			jLabel_Date.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel_Date.setText(" date");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(11, 327, 450, 21));
			jLabel.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel.setText(" Emplacement Sauvegarde");
			Panel_Sauvegarde = new JPanel();
			Panel_Sauvegarde.setLayout(null);
			Panel_Sauvegarde.setBackground(new Color(238, 238, 238));
			Panel_Sauvegarde.add(getJScrollPane(), null);
			Panel_Sauvegarde.add(getJButton_AjoutDossier(), null);
			Panel_Sauvegarde.add(getJButton_AjoutFichier(), null);
			Panel_Sauvegarde.add(getJButton_EffaceListe(), null);
			Panel_Sauvegarde.add(getJButton_EffaceLigne(), null);
			Panel_Sauvegarde.add(getJTextField_CheminSauvegarde(), null);
			Panel_Sauvegarde.add(getJButton_ParcourirSauvegarde(), null);
			Panel_Sauvegarde.add(jLabel, null);
			Panel_Sauvegarde.add(jLabel_Date, null);
			Panel_Sauvegarde.add(jLabel_Version, null);
			Panel_Sauvegarde.add(getJButton_Pause(), null);
			Panel_Sauvegarde.add(getJButton_Go(), null);
			Panel_Sauvegarde.add(getJButton_Arret(), null);
			Panel_Sauvegarde.add(getJProgressBar_EnCours(), null);
			Panel_Sauvegarde.add(getJProgressBar_Total(), null);
			Panel_Sauvegarde.add(getJButton_Enregistre_ListeDossier(), null);
			Panel_Sauvegarde.add(jLabel_Operation, null);
			Panel_Sauvegarde.add(getJScrollPane2(), null);
			Panel_Sauvegarde.add(jLabel4, null);
			Panel_Sauvegarde.add(jLabel5, null);
			Panel_Sauvegarde.add(getJButton_AjoutDossierExclut(), null);
			Panel_Sauvegarde.add(getJButton_AjoutFichierExclut(), null);
			Panel_Sauvegarde.add(getJButton_EffaceListeExclut(), null);
			Panel_Sauvegarde.add(getJButton_EffaceLigneExclut(), null);
			Panel_Sauvegarde.add(getJButton4_SaveExclusion(), null);
			Panel_Sauvegarde.add(getJButton_SauvegardeOk(), null);
			Panel_Sauvegarde.add(getJButton_SauvegardeNok(), null);
		}
		return Panel_Sauvegarde;
	}

	/**
	 * This method initializes jScrollPane
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(11, 81, 426, 175));
			jScrollPane.setName("Dossiers/fichiers à sauvegarder");
			jScrollPane.setFont(new Font("Candara", Font.PLAIN, 12));
			jScrollPane.setViewportView(getJList_Ajout());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jList_Ajout
	 * @return javax.swing.JList
	 */
	private JList getJList_Ajout() {
		if (jList_Ajout == null) {
			jList_Ajout = new JList(ModeleDeListe);

			jList_Ajout.setDragEnabled(true);
			// jList_Ajout.setVisibleRowCount(-1);
			jList_Ajout.getSelectionModel().setSelectionMode(
					ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			jList_Ajout.setFont(new Font("Candara", Font.PLAIN, 12));
			jList_Ajout.setTransferHandler(new TransferHandler() {

				/**
			 * 
			 */
				private static final long serialVersionUID = -1691671205869896738L;

				public boolean canImport(
						final TransferHandler.TransferSupport info) {
					// we only import FileList
					if (!info
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						return false;
					}

					final JList.DropLocation dl = (JList.DropLocation) info
							.getDropLocation();
					if (dl.getIndex() == -1) {
						return false;
					}
					return true;
				}

				public boolean importData(
						final TransferHandler.TransferSupport info) {
					if (!info.isDrop()) {
						return false;
					}

					// Check for String flavor
					if (!info
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						displayDropLocation("On ne met que des chemin dans cette liste.");
						return false;
					}

					final JList.DropLocation dl = (JList.DropLocation) info
							.getDropLocation();
					final DefaultListModel listModel = (DefaultListModel) jList_Ajout
							.getModel();
					final int index = dl.getIndex();

					final boolean insert = dl.isInsert();
					// Get the current string under the drop.
					// String value = (String)listModel.getElementAt(index);

					// Get the string that is being dropped.
					final Transferable t = info.getTransferable();
					Object data;
					try {
						data = t.getTransferData(DataFlavor.javaFileListFlavor)
								.toString();
					} catch (final Exception e) {
						JOptionPane.showMessageDialog(null, e);
						return false;
					}

					if (insert) {
						String ligne = data.toString();
						ligne = ligne.replace("[", "");
						ligne = ligne.replace("]", "");
						if (ligne.contains(",") == true) {// il y a plusieur
							// fichier ou
							// dossier à inserer
							final String[] tabChaine = ligne.split(",");
							// on recupere le nombre de ligne a inserer
							final int nbDeLigneàInserer = tabChaine.length;
							for (int i = 0; i < nbDeLigneàInserer; i++) {
								listModel.add(index, tabChaine[i]);
							}
						} else {
							listModel.add(index, ligne);
						}

					} else {
						listModel.set(index, data);
					}
					return true;

				}

				private void displayDropLocation(final String string) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, string);
						}
					});
				}

				public int getSourceActions(final JComponent c) {
					return COPY;
				}

				protected Transferable createTransferable(final JComponent c) {
					final JList list = (JList) c;
					final Object[] values = list.getSelectedValues();

					final StringBuffer buff = new StringBuffer();

					for (int i = 0; i < values.length; i++) {
						final Object val = values[i];
						buff.append(val == null ? "" : val.toString());
						if (i != values.length - 1) {
							buff.append("\n");
						}
					}
					return new StringSelection(buff.toString());
				}
			});
			jList_Ajout.setDropMode(DropMode.ON_OR_INSERT);

		}
		return jList_Ajout;
	}

	/**
	 * This method initializes jButton_AjoutDossier
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_AjoutDossier() {
		if (jButton_AjoutDossier == null) {
			jButton_AjoutDossier = new JButton();
			jButton_AjoutDossier.setBounds(new Rectangle(11, 271, 50, 50));
			jButton_AjoutDossier.setIcon(new ImageIcon(getClass().getResource(
					"/ajouter_dossier.png")));
			jButton_AjoutDossier.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton_AjoutDossier
					.setToolTipText("Sélectionner un dossier à ajouter");
			jButton_AjoutDossier.setText("");
			jButton_AjoutDossier
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final String Dossier = ManipFichier.OpenFolder();
							ModeleDeListe.addElement(Dossier);
						}
					});
		}
		return jButton_AjoutDossier;
	}

	/**
	 * This method initializes jButton_AjoutFichier
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_AjoutFichier() {
		if (jButton_AjoutFichier == null) {
			jButton_AjoutFichier = new JButton();
			jButton_AjoutFichier.setBounds(new Rectangle(78, 271, 50, 50));
			jButton_AjoutFichier.setIcon(new ImageIcon(getClass().getResource(
					"/ajouter_fichiers.png")));
			jButton_AjoutFichier.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton_AjoutFichier
					.setToolTipText("Sélectionner un fichier à ajouter");
			jButton_AjoutFichier.setText("");
			jButton_AjoutFichier
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final String Fichier = ManipFichier
									.OpenFile("", "");
							ModeleDeListe.addElement(Fichier);
						}
					});
		}
		return jButton_AjoutFichier;
	}

	/**
	 * This method initializes jButton_EffaceListe
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_EffaceListe() {
		if (jButton_EffaceListe == null) {
			jButton_EffaceListe = new JButton();
			jButton_EffaceListe.setBounds(new Rectangle(142, 271, 105, 50));
			jButton_EffaceListe.setIcon(new ImageIcon(getClass().getResource(
					"/corbeille.png")));
			jButton_EffaceListe.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton_EffaceListe.setToolTipText("Effacer la liste entière");
			jButton_EffaceListe.setText("Liste");
			jButton_EffaceListe
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							ModeleDeListe.removeAllElements();

						}
					});
		}
		return jButton_EffaceListe;
	}

	/**
	 * This method initializes jButton_EffaceLigne
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_EffaceLigne() {
		if (jButton_EffaceLigne == null) {
			jButton_EffaceLigne = new JButton();
			jButton_EffaceLigne.setBounds(new Rectangle(261, 271, 105, 50));
			jButton_EffaceLigne.setIcon(new ImageIcon(getClass().getResource(
					"/corbeille.png")));
			jButton_EffaceLigne.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton_EffaceLigne.setToolTipText("Effacer la ligne sélectionnée");
			jButton_EffaceLigne.setText("Ligne");
			jButton_EffaceLigne
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final int nbDeLigne = jList_Ajout.getModel()
									.getSize();
							if (nbDeLigne == 0) {
								JOptionPane.showMessageDialog(null,
										"Aucune ligne séléctionnée", "Erreur",
										JOptionPane.WARNING_MESSAGE);
							} else {
								for (int i = 0; i < nbDeLigne; i++) {
									if (jList_Ajout.isSelectedIndex(i) == true) {// si
										// la
										// ligne
										// est
										// selectionnée,
										// on
										// la
										// supprime
										ModeleDeListe.removeElementAt(i);

									}

								}
							}

						}
					});
		}
		return jButton_EffaceLigne;
	}

	/**
	 * This method initializes jTextField_CheminSauvegarde
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_CheminSauvegarde() {
		if (jTextField_CheminSauvegarde == null) {
			jTextField_CheminSauvegarde = new JTextField();
			jTextField_CheminSauvegarde.setBounds(new Rectangle(11, 352, 450,
					29));
			jTextField_CheminSauvegarde.setFont(new Font("Candara", Font.PLAIN,
					12));
		}
		return jTextField_CheminSauvegarde;
	}

	/**
	 * This method initializes jButton_ParcourirSauvegarde
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_ParcourirSauvegarde() {
		if (jButton_ParcourirSauvegarde == null) {
			jButton_ParcourirSauvegarde = new JButton();
			jButton_ParcourirSauvegarde.setBounds(new Rectangle(479, 351, 99,
					28));
			jButton_ParcourirSauvegarde.setFont(new Font("Candara", Font.PLAIN,
					12));
			jButton_ParcourirSauvegarde
					.setHorizontalTextPosition(SwingConstants.CENTER);
			jButton_ParcourirSauvegarde.setText(" Parcourir");
			jButton_ParcourirSauvegarde
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final String cheminSauvegarde = ManipFichier
									.OpenFolder();

							jTextField_CheminSauvegarde
									.setText(cheminSauvegarde);
						}
					});
		}
		return jButton_ParcourirSauvegarde;
	}

	/**
	 * This method initializes jButton_Pause
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Pause() {
		if (jButton_Pause == null) {
			jButton_Pause = new JButton();
			jButton_Pause.setBounds(new Rectangle(134, 404, 50, 50));
			jButton_Pause.setIcon(new ImageIcon(getClass().getResource(
					"/pause.png")));
			jButton_Pause.setEnabled(false);

			jButton_Pause.setText("");
			jButton_Pause.setVisible(false);
			jButton_Pause
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							if (save != null) {
								save.pause();

								enpause++;
							}

							jButton_Go.setEnabled(true);
							jButton_Pause.setEnabled(false);

						}
					});
		}
		return jButton_Pause;
	}

	/**
	 * This method initializes jButton_Go
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Go() {
		if (jButton_Go == null) {
			jButton_Go = new JButton();
			jButton_Go.setBounds(new Rectangle(11, 399, 50, 50));
			jButton_Go.setHorizontalTextPosition(SwingConstants.CENTER);
			jButton_Go.setIcon(new ImageIcon(getClass().getResource(
					"/lecture.png")));
			jButton_Go.setToolTipText("Lancer la sauvegarde");

			jButton_Go.setText("");
			jButton_Go.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {

					final File cheminDestination = new File(
							jTextField_CheminSauvegarde.getText());
					boolean succes = GestionDemandes
							.executeRequete("DELETE FROM CHEMIN_SAUVEGARDE ");

					succes = GestionDemandes
							.executeRequete("INSERT INTO CHEMIN_SAUVEGARDE (EMPLACEMENT_DE_SAUVEGARDE) VALUES ('"
									+ jTextField_CheminSauvegarde.getText()
									+ "')");

					if (enpause > 0) {

						save.reprise();
						jButton_Go.setEnabled(false);
						jButton_Pause.setEnabled(true);
						jButton_Arret.setEnabled(true);
						jButton_SauvegardeOk.setVisible(false);
						jButton_SauvegardeNok.setVisible(false);
						enpause = 0;
					} else {
						if (cheminDestination.exists() == true) {
							if ((jTextField_CheminSauvegarde.getText().equals(
									"") == false)
									&& (jList_Ajout.getModel().getSize() != 0)
									&& (succes == true)) {

								ModeleDeListeExclu.removeAllElements();

								GestionDemandes
										.executeRequeteEtAfficheJList(
												"SELECT a.EMPLACEMENT_FICHIER FROM LISTE_EXCLUT a",
												ModeleDeListeExclu);

								jButton5_Refresh.setEnabled(false);
								jButton_Go.setEnabled(false);
								jButton_Pause.setEnabled(true);
								jButton_Arret.setEnabled(true);
								jButton_SauvegardeOk.setVisible(false);
								jButton_SauvegardeNok.setVisible(false);
								save = new CopyOfThread_Sauvegarde(
										jTextField_CheminSauvegarde.getText(),
										jProgressBar_EnCours,
										jProgressBar_Total, jLabel_Operation,
										jList_Ajout, ModeleDeListe,
										jList_Exclut, ModeleDeListeExclu,
										jButton_Pause, jButton_Go,
										jButton_Arret, jButton5_Refresh,
										jButton_SauvegardeOk,
										jButton_SauvegardeNok,
										jCheckBox_ArretMachine);

								save.start();
							} else {
								JOptionPane
										.showMessageDialog(
												null,
												"Impossible de lancer la sauvegarde \n\r Veuillez remplir le chemin de la sauvegarde ",
												"Erreur",
												JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane
									.showMessageDialog(
											null,
											"Impossible de lancer la sauvegarde \n\r Le chemin de destination n'est pas accessible ",
											"Erreur", JOptionPane.ERROR_MESSAGE);
						}
					}

				}
			});

		}
		return jButton_Go;
	}

	/**
	 * This method initializes jButton_Arret
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Arret() {
		if (jButton_Arret == null) {
			jButton_Arret = new JButton();
			jButton_Arret.setBounds(new Rectangle(411, 400, 50, 50));
			jButton_Arret.setIcon(new ImageIcon(getClass().getResource(
					"/stop.png")));
			jButton_Arret.setEnabled(false);
			jButton_Arret.setToolTipText("Arrêter la sauvegarde");
			jButton_Arret.setText("");
			jButton_Arret
					.addActionListener(new java.awt.event.ActionListener() {

						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final int arretDemandé = JOptionPane
									.showConfirmDialog(
											null,
											"Voulez vous arreter la sauvegarde?",
											"", JOptionPane.YES_NO_OPTION); // si
							// il
							// repond
							// oui, dl
							// du setup
							// puis
							// execution
							// si il repond non => poursuite du programme
							if (arretDemandé == 0) {// arret acceptée

								Historique
										.ecrire("Arret de la sauvegarde demandé par l'utilisateur");

								save.termine();

								Thread_RefreshUI t = new Thread_RefreshUI(
										jLabel_Date, jLabel_Version,
										jList_Ajout, ModeleDeListe,
										jList_Exclut, ModeleDeListeExclu,
										jList_Visu, ModeleDeListeVisu,
										jTextField_CheminSauvegarde,
										jTextFieldHeure, jTextFieldMinutes,
										jCheckBox_EnvoiMailAuSt,
										jCheckBox_ArretMachine, jCheckBoxLundi,
										jCheckBoxMardi, jCheckBoxMercredi,
										jCheckBoxJeudi, jCheckBoxVendredi,
										jCheckBoxSamedi, jCheckBoxDimanche);
								t.start();

							}

						}
					});
		}
		return jButton_Arret;
	}

	/**
	 * This method initializes jProgressBar_EnCours
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar_EnCours() {
		if (jProgressBar_EnCours == null) {
			jProgressBar_EnCours = new JProgressBar();
			jProgressBar_EnCours.setBounds(new Rectangle(11, 466, 846, 25));
			jProgressBar_EnCours.setFont(new Font("Candara", Font.BOLD, 14));
			jProgressBar_EnCours.setBackground(new Color(238, 238, 238));
			jProgressBar_EnCours.setStringPainted(true);
		}
		return jProgressBar_EnCours;
	}

	/**
	 * This method initializes jProgressBar_Total
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar_Total() {
		if (jProgressBar_Total == null) {
			jProgressBar_Total = new JProgressBar();
			jProgressBar_Total.setBounds(new Rectangle(11, 499, 846, 25));
			jProgressBar_Total.setFont(new Font("Candara", Font.BOLD, 14));
			jProgressBar_Total.setBackground(new Color(238, 238, 238));
			jProgressBar_Total.setStringPainted(true);
		}
		return jProgressBar_Total;
	}

	/**
	 * This method initializes jButton_Enregistre_ListeDossier
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Enregistre_ListeDossier() {
		if (jButton_Enregistre_ListeDossier == null) {
			jButton_Enregistre_ListeDossier = new JButton();
			jButton_Enregistre_ListeDossier.setBounds(new Rectangle(387, 271,
					50, 50));
			jButton_Enregistre_ListeDossier.setIcon(new ImageIcon(getClass()
					.getResource("/enregistrer.png")));
			jButton_Enregistre_ListeDossier.setFont(new Font("Candara",
					Font.PLAIN, 12));
			jButton_Enregistre_ListeDossier
					.setToolTipText("Enregistrer la liste");
			jButton_Enregistre_ListeDossier.setText("");
			jButton_Enregistre_ListeDossier
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							int enregistrement_succe = 0;
							final int nbDeLigne = jList_Ajout.getModel()
									.getSize();
							if (nbDeLigne == 0) {
								final boolean succes = GestionDemandes
										.executeRequete("DELETE FROM LISTE_FICHIER");
								if (succes == true) {

									JOptionPane.showMessageDialog(null,
											"Enregistrement Ok", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									Historique
											.ecrire("Effacement reussi de la table LISTE_FICHIER avec la requete DELETE FROM LISTE_FICHIER");

								} else {

									JOptionPane.showMessageDialog(null,
											"Enregistrement erreur", "Erreur",
											JOptionPane.WARNING_MESSAGE);
									Historique
											.ecrire("Effacement échoué de la table LISTE_FICHIER avec la requete DELETE FROM LISTE_FICHIER");

								}

							} else {
								int nbEnregistrementPresent = 0;
								// on commence par vider la table LISTE_FICHIER
								boolean succes = GestionDemandes
										.executeRequete("DELETE FROM LISTE_FICHIER");

								for (int i = 0; i < nbDeLigne; i++) {

									final Object objet = ModeleDeListe
											.getElementAt(i);
									final String LigneAInserer = objet
											.toString();
									// on verifie si cette ligne n'est pas deja
									// presente dans la bdd
									try {
										nbEnregistrementPresent = Integer
												.parseInt(GestionDemandes
														.executeRequeteEtRetourne1Champ("SELECT count(*) FROM LISTE_FICHIER WHERE EMPLACEMENT_FICHIER = '"
																+ LigneAInserer
																+ "'"));
									} catch (final SQLException e1) {

										Utilitaires.Historique
												.ecrire("Message d'erreur: "
														+ e1);
									}
									if (nbEnregistrementPresent == 0) {
										succes = GestionDemandes
												.executeRequete("INSERT INTO LISTE_FICHIER VALUES ('"
														+ LigneAInserer + "')");
										if (succes == true) {

											enregistrement_succe++;
											// JOptionPane.showMessageDialog(null,
											// "Enregistrement Ok",
											// "Ok",
											// JOptionPane.INFORMATION_MESSAGE);
											Historique
													.ecrire("Enregistrement dans la base d'un nouveau fichier/dossier à sauvegarder :"
															+ LigneAInserer);

										} else {

											JOptionPane
													.showMessageDialog(
															null,
															"Effacement erreur",
															"Erreur",
															JOptionPane.WARNING_MESSAGE);
											Historique
													.ecrire("Erreur lors de l'enregistrement d'un nouveau fichier/dossier à sauvegarder :"
															+ LigneAInserer);

										}
									}
								}

								if (enregistrement_succe == nbDeLigne) {
									JOptionPane.showMessageDialog(null,
											"Enregistrement Ok", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									// Historique.ecrire("Enregistrement dans la base d'un nouveau fichier/dossier à sauvegarder :"
									// +LigneAInserer);
								}
							}

						}
					});
		}
		return jButton_Enregistre_ListeDossier;
	}

	/**
	 * This method initializes jTabbedPane
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBounds(new Rectangle(3, 14, 884, 583));
			jTabbedPane.addTab("Sauvegarde", null, getPanel_Sauvegarde(), null);
			jTabbedPane.addTab("Visualisation", null, getJPanel(), null);
			jTabbedPane.addTab("Paramètres", null, getJPanel1(), null);
			jTabbedPane.addMouseListener(new java.awt.event.MouseListener() {
				public void mouseClicked(final java.awt.event.MouseEvent e) {

					final File enCours = new File(GestionRepertoire
							.RecupRepTravail()
							+ "/enCours.txt");
					if (!enCours.exists()) {
						ModeleDeListeVisu.removeAllElements();
						jLabel_Date_Sauvegarde.setText("");
						jLabel_Taille_Sauvegarde.setText("");
						jLabel_ID_Sauvegarde.setText("");
						int NbDeSauvegardeAAfficher = 0;
						try {
							NbDeSauvegardeAAfficher = Integer
									.parseInt(GestionDemandes
											.executeRequeteEtRetourne1Champ("SELECT count(*) FROM SAUVEGARDE "));
						} catch (final NumberFormatException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						} catch (final SQLException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						}

						if (NbDeSauvegardeAAfficher != 0) {
							GestionDemandes
									.executeRequeteEtAfficheJList(
											"SELECT a.EMPLACEMENT_SAUVEGARDE FROM SAUVEGARDE a ORDER by a.ID_SAUVEGARDE",
											ModeleDeListeVisu);
						}
					}

				}

				@Override
				public void mouseEntered(final MouseEvent arg0) {

				}

				@Override
				public void mouseExited(final MouseEvent arg0) {

				}

				@Override
				public void mousePressed(final MouseEvent arg0) {

				}

				@Override
				public void mouseReleased(final MouseEvent arg0) {

				}

			});
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel_ID_Sauvegarde = new JLabel();
			jLabel_ID_Sauvegarde.setBounds(new Rectangle(195, 334, 271, 23));
			jLabel_ID_Sauvegarde.setText("");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(33, 334, 149, 23));
			jLabel3.setText(" N° de la sauvegarde :");
			jLabel_Taille_Sauvegarde = new JLabel();
			jLabel_Taille_Sauvegarde
					.setBounds(new Rectangle(194, 287, 271, 23));
			jLabel_Taille_Sauvegarde.setText("");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(32, 287, 149, 23));
			jLabel2.setText(" Taille de la sauvegarde :");
			jLabel_Date_Sauvegarde = new JLabel();
			jLabel_Date_Sauvegarde.setBounds(new Rectangle(194, 238, 271, 23));
			jLabel_Date_Sauvegarde.setText("");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(32, 238, 149, 23));
			jLabel1.setText(" Date de la sauvegarde : ");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getJScrollPane1(), null);
			jPanel.add(jLabel1, null);
			jPanel.add(jLabel_Date_Sauvegarde, null);
			jPanel.add(jLabel2, null);
			jPanel.add(jLabel_Taille_Sauvegarde, null);
			jPanel.add(jLabel3, null);
			jPanel.add(jLabel_ID_Sauvegarde, null);
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton2(), null);
			jPanel.add(getJButton3(), null);
			jPanel.add(getJButton5_Refresh(), null);
			jPanel.add(getJButtonRechercheDansSauvegarde(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane1
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setBounds(new Rectangle(14, 14, 841, 198));
			jScrollPane1.setViewportView(getJList_Visu());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jList_Visu
	 * @return javax.swing.JList
	 */
	private JList getJList_Visu() {
		if (jList_Visu == null) {
			jList_Visu = new JList(ModeleDeListeVisu);
			jList_Visu.addMouseListener(new java.awt.event.MouseListener() {
				public void mouseClicked(final java.awt.event.MouseEvent e) {
					System.out.println("mouseClicked()");

					final int IdxLigne = jList_Visu.getSelectedIndex();
					if (IdxLigne == -1) {
						return;
					}
					final String Emplacement = ModeleDeListeVisu.getElementAt(
							IdxLigne).toString();
					final String getIdSauvegardeEtDateSauvegarde = "SELECT a.ID_SAUVEGARDE, a.DATE_SAUVEGARDE FROM SAUVEGARDE a WHERE EMPLACEMENT_SAUVEGARDE='"
							+ Emplacement + "'";

					final String liste = GestionDemandes
							.executeRequeteEtRetourne1ListeDeValeur(getIdSauvegardeEtDateSauvegarde);
					System.out.println(liste);
					final String[] tabChaine = liste.split(";");
					final String ID_SAUVEGARDE = tabChaine[0].trim();
					final long dateLong = Long.parseLong(tabChaine[1].trim());
					final File Sauvegarde = new File(Emplacement);
					final long TailleFichier = Sauvegarde.length();

					if (TailleFichier == 0) {
						jLabel_Taille_Sauvegarde.setText("0 octets");
						final boolean exist = Sauvegarde.exists();
						if (exist == true) {
							final int AccepteSuppr = JOptionPane
									.showConfirmDialog(
											null,
											"La sauvegarde existe mais elle est vide\n\r Voulez vous la supprimer?",
											"?", JOptionPane.YES_NO_OPTION); // si
							// il
							// repond
							// oui,
							// dl
							// du
							// setup
							// puis
							// execution
							// si il repond non => poursuite du programme
							if (AccepteSuppr == 0) {// suppr acceptée
								final boolean succes = Sauvegarde.delete();
								if (succes == false) {
									Sauvegarde.deleteOnExit();

								}
								final boolean succeDeleteFichier = GestionDemandes
										.executeRequete("DELETE FROM FICHIER WHERE ID_SAUVEGARDE = "
												+ Integer
														.parseInt(ID_SAUVEGARDE));
								final boolean succesDeleteSauvegarde = GestionDemandes
										.executeRequete("DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE = "
												+ Integer
														.parseInt(ID_SAUVEGARDE));
								jLabel_Date_Sauvegarde.setText("");
								jLabel_Taille_Sauvegarde.setText("");
								jLabel_ID_Sauvegarde.setText("");
								if (succeDeleteFichier == true
										&& succesDeleteSauvegarde == true) {
									JOptionPane.showMessageDialog(null,
											"Suppression reussie", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									ModeleDeListeVisu.removeAllElements();

								}
								int NbDeSauvegardeAAfficher = 0;
								try {
									NbDeSauvegardeAAfficher = Integer
											.parseInt(GestionDemandes
													.executeRequeteEtRetourne1Champ("SELECT count(*) FROM LISTE_FICHIER "));
								} catch (final NumberFormatException e1) {

									Utilitaires.Historique
											.ecrire("Message d'erreur: " + e1);
								} catch (final SQLException e1) {

									Utilitaires.Historique
											.ecrire("Message d'erreur: " + e1);
								}

								if (NbDeSauvegardeAAfficher != 0) {
									GestionDemandes
											.executeRequeteEtAfficheJList(
													"SELECT a.EMPLACEMENT_SAUVEGARDE FROM SAUVEGARDE a ORDER by a.ID_SAUVEGARDE",
													ModeleDeListeVisu);
								}

							}
						} else {
							final int AccepteSuppr = JOptionPane
									.showConfirmDialog(
											null,
											"La sauvegarde n'existe plus ou n'est pas accessible pour le moment\n\r Voulez vous supprimer cette reference de la base de données?"); // si
							// il
							// repond
							// oui,
							// dl
							// du
							// setup
							// puis
							// execution
							if (AccepteSuppr == 0) {// suppr acceptée
								final boolean succes = Sauvegarde.delete();
								if (succes == false) {
									Sauvegarde.deleteOnExit();

								}
								jLabel_Date_Sauvegarde.setText("");
								jLabel_Taille_Sauvegarde.setText("");
								jLabel_ID_Sauvegarde.setText("");
								final boolean succeDeleteFichier = GestionDemandes
										.executeRequete("DELETE FROM FICHIER WHERE ID_SAUVEGARDE = "
												+ Integer
														.parseInt(ID_SAUVEGARDE));
								final boolean succesDeleteSauvegarde = GestionDemandes
										.executeRequete("DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE = "
												+ Integer
														.parseInt(ID_SAUVEGARDE));

								if (succeDeleteFichier == true
										&& succesDeleteSauvegarde == true) {
									JOptionPane.showMessageDialog(null,
											"Suppression reussie", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									ModeleDeListeVisu.removeAllElements();
								}
								int NbDeSauvegardeAAfficher = 0;
								try {
									NbDeSauvegardeAAfficher = Integer
											.parseInt(GestionDemandes
													.executeRequeteEtRetourne1Champ("SELECT count(*) FROM LISTE_FICHIER "));
								} catch (final NumberFormatException e1) {

									Utilitaires.Historique
											.ecrire("Message d'erreur: " + e1);
								} catch (final SQLException e1) {

									Utilitaires.Historique
											.ecrire("Message d'erreur: " + e1);
								}

								if (NbDeSauvegardeAAfficher != 0) {
									GestionDemandes
											.executeRequeteEtAfficheJList(
													"SELECT a.EMPLACEMENT_SAUVEGARDE FROM SAUVEGARDE a ORDER by a.ID_SAUVEGARDE",
													ModeleDeListeVisu);
								}

							}

						}

					}

					if (TailleFichier > 1024 && TailleFichier < 10240) {
						final double TailleAvec2ChiffreApresLaVirgule = floor(
								TailleFichier, 2, 0.0d);
						jLabel_Taille_Sauvegarde
								.setText(TailleAvec2ChiffreApresLaVirgule
										+ " o");
					}
					if (TailleFichier >= 10240 && TailleFichier < (1024 * 1024)) {
						final double TailleAvec2ChiffreApresLaVirgule = floor(
								TailleFichier / 1024, 2, 0.0d);
						jLabel_Taille_Sauvegarde
								.setText(TailleAvec2ChiffreApresLaVirgule
										+ " ko");
					}
					if (TailleFichier >= (1024 * 1024)
							&& TailleFichier < (1024 * 1024 * 1024)) {
						final double TailleAvec2ChiffreApresLaVirgule = floor(
								TailleFichier / (1024 * 1024), 2, 0.0d);
						jLabel_Taille_Sauvegarde
								.setText(TailleAvec2ChiffreApresLaVirgule
										+ " Mo");
					}
					if (TailleFichier >= (1024 * 1024 * 1024)) {

						final double TailleAvec2ChiffreApresLaVirgule = floor(
								new Double(TailleFichier / (1024 * 1024 * 1024)),
								2, 0.0d);

						jLabel_Taille_Sauvegarde
								.setText(TailleAvec2ChiffreApresLaVirgule
										+ " Go");
					}

					jLabel_Date_Sauvegarde.setText(RecupDate
							.LongToDate(dateLong));
					jLabel_ID_Sauvegarde.setText(ID_SAUVEGARDE);

				}

				public void mousePressed(final java.awt.event.MouseEvent e) {
				}

				public void mouseReleased(final java.awt.event.MouseEvent e) {
				}

				public void mouseEntered(final java.awt.event.MouseEvent e) {
				}

				public void mouseExited(final java.awt.event.MouseEvent e) {
				}
			});
		}
		return jList_Visu;
	}

	public static double floor(final double a, final int decimales,
			final double plus) {
		final double p = Math.pow(10.0, decimales);
		// return Math.floor((a*p) + 0.5) / p; // avec arrondi éventuel (sans
		// arrondi >>>> + 0.0
		return Math.floor((a * p) + plus) / p;
	}

	/**
	 * This method initializes jButton1
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setBounds(new Rectangle(518, 231, 161, 37));
			jButton1.setFont(new Font("Candara", Font.BOLD, 12));
			jButton1.setText("Effacer Sauvegarde");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {

					final String chemin = ModeleDeListeVisu.getElementAt(
							jList_Visu.getSelectedIndex()).toString();
					final int ID_SAUVEGARDE = Integer
							.parseInt(jLabel_ID_Sauvegarde.getText());
					final File Sauvegarde = new File(chemin);
					final int AccepteSuppr = JOptionPane.showConfirmDialog(
							null,
							"Voulez-vous vraiment supprimer la sauvegarde suivante :\n\r numéro : "
									+ ID_SAUVEGARDE
									+ "\n\r chemin de la sauvegarde : "
									+ chemin, "?", JOptionPane.YES_NO_OPTION); // si
					// il
					// repond
					// oui,
					// suppression
					// de
					// la
					// sauvegarde
					if (AccepteSuppr == 0) {// suppr acceptée
						final boolean succes = Sauvegarde.delete();
						if (succes == false) {
							Sauvegarde.deleteOnExit();

						}
						final boolean succeDeleteFichier = GestionDemandes
								.executeRequete("DELETE FROM FICHIER WHERE ID_SAUVEGARDE = "
										+ Integer.parseInt(jLabel_ID_Sauvegarde
												.getText()));
						final boolean succesDeleteSauvegarde = GestionDemandes
								.executeRequete("DELETE FROM SAUVEGARDE WHERE ID_SAUVEGARDE = "
										+ Integer.parseInt(jLabel_ID_Sauvegarde
												.getText()));

						if (succeDeleteFichier == true
								&& succesDeleteSauvegarde == true) {
							JOptionPane.showMessageDialog(null,
									"Suppression réussie", "Ok",
									JOptionPane.INFORMATION_MESSAGE);
							ModeleDeListeVisu.removeAllElements();

						}
						jLabel_Date_Sauvegarde.setText("");
						jLabel_Taille_Sauvegarde.setText("");
						jLabel_ID_Sauvegarde.setText("");
						int NbDeSauvegardeAAfficher = 0;
						try {
							NbDeSauvegardeAAfficher = Integer
									.parseInt(GestionDemandes
											.executeRequeteEtRetourne1Champ("SELECT count(*) FROM SAUVEGARDE "));
						} catch (final NumberFormatException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						} catch (final SQLException e1) {

							Utilitaires.Historique.ecrire("Message d'erreur: "
									+ e1);
						}

						if (NbDeSauvegardeAAfficher != 0) {
							GestionDemandes
									.executeRequeteEtAfficheJList(
											"SELECT a.EMPLACEMENT_SAUVEGARDE FROM SAUVEGARDE a ORDER by a.ID_SAUVEGARDE",
											ModeleDeListeVisu);
						}

					}

				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2
	 * @return javax.swing.JButton
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setBounds(new Rectangle(518, 279, 161, 38));
			jButton2.setFont(new Font("Candara", Font.BOLD, 12));
			jButton2.setText("Ouvrir Sauvegarde");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {

					final String chemin = ModeleDeListeVisu.getElementAt(
							jList_Visu.getSelectedIndex()).toString();
					Utilitaires.OpenWithDefaultViewer.open(chemin);
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButton3
	 * @return javax.swing.JButton
	 */
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setBounds(new Rectangle(518, 328, 161, 34));
			jButton3.setFont(new Font("Candara", Font.BOLD, 12));
			jButton3.setText("Ouvrir le répertoire");
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {

					final String chemin = ModeleDeListeVisu.getElementAt(
							jList_Visu.getSelectedIndex()).toString();
					final String cheminSeul = chemin.substring(0, chemin
							.lastIndexOf("\\"));
					Utilitaires.OpenWithDefaultViewer.open(cheminSeul);
				}
			});
		}
		return jButton3;
	}

	/**
	 * This method initializes jScrollPane2
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setLocation(new Point(448, 81));
			jScrollPane2.setSize(new Dimension(419, 175));
			jScrollPane2.setViewportView(getJList_Exclut());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes jList_Exclut
	 * @return javax.swing.JList
	 */
	private JList getJList_Exclut() {
		if (jList_Exclut == null) {
			jList_Exclut = new JList(ModeleDeListeExclu);
			jList_Exclut.setFont(new Font("Candara", Font.PLAIN, 12));
			jList_Exclut.setTransferHandler(new TransferHandler() {

				/**
			 * 
			 */
				private static final long serialVersionUID = -1691671205869896738L;

				public boolean canImport(
						final TransferHandler.TransferSupport info) {
					// we only import FileList
					if (!info
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						return false;
					}

					final JList.DropLocation dl = (JList.DropLocation) info
							.getDropLocation();
					if (dl.getIndex() == -1) {
						return false;
					}
					return true;
				}

				public boolean importData(
						final TransferHandler.TransferSupport info) {
					if (!info.isDrop()) {
						return false;
					}

					// Check for String flavor
					if (!info
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						displayDropLocation("On ne met que des chemin dans cette liste.");
						return false;
					}

					final JList.DropLocation dl = (JList.DropLocation) info
							.getDropLocation();
					final DefaultListModel listModel = (DefaultListModel) jList_Exclut
							.getModel();
					final int index = dl.getIndex();

					final boolean insert = dl.isInsert();
					// Get the current string under the drop.
					// String value = (String)listModel.getElementAt(index);

					// Get the string that is being dropped.
					final Transferable t = info.getTransferable();
					Object data;
					try {
						data = t.getTransferData(DataFlavor.javaFileListFlavor)
								.toString();
					} catch (final Exception e) {
						JOptionPane.showMessageDialog(null, e);
						return false;
					}

					if (insert) {
						String ligne = data.toString();
						ligne = ligne.replace("[", "");
						ligne = ligne.replace("]", "");
						if (ligne.contains(",") == true) {// il y a plusieur
							// fichier ou
							// dossier à inserer
							final String[] tabChaine = ligne.split(",");
							// on recupere le nombre de ligne a inserer
							final int nbDeLigneàInserer = tabChaine.length;
							for (int i = 0; i < nbDeLigneàInserer; i++) {
								listModel.add(index, tabChaine[i]);
							}
						} else {
							listModel.add(index, ligne);
						}

					} else {
						listModel.set(index, data);
					}
					return true;

				}

				private void displayDropLocation(final String string) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, string);
						}
					});
				}

				public int getSourceActions(final JComponent c) {
					return COPY;
				}

				protected Transferable createTransferable(final JComponent c) {
					final JList list = (JList) c;
					final Object[] values = list.getSelectedValues();

					final StringBuffer buff = new StringBuffer();

					for (int i = 0; i < values.length; i++) {
						final Object val = values[i];
						buff.append(val == null ? "" : val.toString());
						if (i != values.length - 1) {
							buff.append("\n");
						}
					}
					return new StringSelection(buff.toString());
				}
			});
			jList_Exclut.setDropMode(DropMode.ON_OR_INSERT);

		}
		return jList_Exclut;
	}

	/**
	 * This method initializes jButton_AjoutDossierExclut
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_AjoutDossierExclut() {
		if (jButton_AjoutDossierExclut == null) {
			jButton_AjoutDossierExclut = new JButton();
			jButton_AjoutDossierExclut
					.setBounds(new Rectangle(448, 271, 50, 50));
			jButton_AjoutDossierExclut.setIcon(new ImageIcon(getClass()
					.getResource("/ajouter_dossier.png")));
			jButton_AjoutDossierExclut.setText("");
			jButton_AjoutDossierExclut
					.setToolTipText("Sélectionner un dossier à ajouter");
			jButton_AjoutDossierExclut.setFont(new Font("Candara", Font.PLAIN,
					12));
			jButton_AjoutDossierExclut
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final String Dossier = ManipFichier.OpenFolder();
							ModeleDeListeExclu.addElement(Dossier);
						}
					});
		}
		return jButton_AjoutDossierExclut;
	}

	/**
	 * This method initializes jButton_AjoutFichierExclut
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_AjoutFichierExclut() {
		if (jButton_AjoutFichierExclut == null) {
			jButton_AjoutFichierExclut = new JButton();
			jButton_AjoutFichierExclut
					.setBounds(new Rectangle(515, 271, 50, 50));
			jButton_AjoutFichierExclut.setIcon(new ImageIcon(getClass()
					.getResource("/ajouter_fichiers.png")));
			jButton_AjoutFichierExclut.setText("");
			jButton_AjoutFichierExclut
					.setToolTipText("Sélectionner un fichier à ajouter");
			jButton_AjoutFichierExclut.setFont(new Font("Candara", Font.PLAIN,
					12));
			jButton_AjoutFichierExclut
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final String Fichier = ManipFichier
									.OpenFile("", "");
							ModeleDeListeExclu.addElement(Fichier);
						}
					});
		}
		return jButton_AjoutFichierExclut;
	}

	/**
	 * This method initializes jButton_EffaceListeExclut
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_EffaceListeExclut() {
		if (jButton_EffaceListeExclut == null) {
			jButton_EffaceListeExclut = new JButton();
			jButton_EffaceListeExclut
					.setBounds(new Rectangle(579, 271, 105, 50));
			jButton_EffaceListeExclut.setIcon(new ImageIcon(getClass()
					.getResource("/corbeille.png")));
			jButton_EffaceListeExclut.setText("Liste");
			jButton_EffaceListeExclut
					.setToolTipText("Effacer la liste entière");
			jButton_EffaceListeExclut.setFont(new Font("Candara", Font.PLAIN,
					12));
			jButton_EffaceListeExclut
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							ModeleDeListeExclu.removeAllElements();
							/*
							 * boolean succes =GestionDemandes.executeRequete(
							 * "DELETE FROM LISTE_EXCLUT"); if (succes==true){
							 * try { JOptionPane.showMessageDialog(null,
							 * "Effacement Ok", "Ok",
							 * JOptionPane.INFORMATION_MESSAGE);
							 * Historique.ecrire(
							 * "Effacement reussi de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT"
							 * ); } catch (IOException e1) { } }else{ try {
							 * JOptionPane.showMessageDialog(null,
							 * "Effacement erreur", "Erreur",
							 * JOptionPane.WARNING_MESSAGE);Historique.ecrire(
							 * "Effacement échoué de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT"
							 * ); } catch (IOException e1) { } }
							 */
						}

					});
		}
		return jButton_EffaceListeExclut;
	}

	/**
	 * This method initializes jButton_EffaceLigneExclut
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_EffaceLigneExclut() {
		if (jButton_EffaceLigneExclut == null) {
			jButton_EffaceLigneExclut = new JButton();
			jButton_EffaceLigneExclut
					.setBounds(new Rectangle(698, 271, 105, 50));
			jButton_EffaceLigneExclut.setIcon(new ImageIcon(getClass()
					.getResource("/corbeille.png")));
			jButton_EffaceLigneExclut.setText("Ligne");
			jButton_EffaceLigneExclut
					.setToolTipText("Effacer la ligne sélectionnée");
			jButton_EffaceLigneExclut.setFont(new Font("Candara", Font.PLAIN,
					12));
			jButton_EffaceLigneExclut
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final int nbDeLigne = jList_Exclut.getModel()
									.getSize();
							if (nbDeLigne == 0) {
								JOptionPane.showMessageDialog(null,
										"Aucune ligne", "Erreur",
										JOptionPane.WARNING_MESSAGE);
							} else {
								for (int i = 0; i < nbDeLigne; i++) {
									if (jList_Exclut.isSelectedIndex(i) == true) {// si
										// la
										// ligne
										// est
										// selectionnée,
										// on
										// la
										// supprime
										ModeleDeListeExclu.removeElementAt(i);
										/*
										 * String LigneAEffacer =
										 * ModeleDeListeExclu
										 * .getElementAt(i).toString(); boolean
										 * succes =
										 * GestionDemandes.executeRequete(
										 * "DELETE FROM LISTE_EXCLUT where EMPLACEMENT_FICHIER = '"
										 * +LigneAEffacer+"'"); if
										 * (succes==true){ try {
										 * JOptionPane.showMessageDialog(null,
										 * "Effacement Ok", "Ok",
										 * JOptionPane.INFORMATION_MESSAGE);
										 * Historique.ecrire(
										 * "Effacement reussi de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT where EMPLACEMENT_FICHIER = '"
										 * +LigneAEffacer+"'"); } catch
										 * (IOException e1) { } }else{ try {
										 * JOptionPane.showMessageDialog(null,
										 * "Effacement erreur", "Erreur",
										 * JOptionPane.WARNING_MESSAGE);
										 * Historique.ecrire(
										 * "Effacement échoué de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT where EMPLACEMENT_FICHIER = '"
										 * +LigneAEffacer+"'"); } catch
										 * (IOException e1) { } }
										 */

									}

								}
							}

						}
					});
		}
		return jButton_EffaceLigneExclut;
	}

	/**
	 * This method initializes jButton4_SaveExclusion
	 * @return javax.swing.JButton
	 */
	private JButton getJButton4_SaveExclusion() {
		if (jButton4_SaveExclusion == null) {
			jButton4_SaveExclusion = new JButton();
			jButton4_SaveExclusion.setBounds(new Rectangle(817, 271, 50, 50));
			jButton4_SaveExclusion.setIcon(new ImageIcon(getClass()
					.getResource("/enregistrer.png")));
			jButton4_SaveExclusion.setText("");
			jButton4_SaveExclusion.setToolTipText("Enregistrer la liste");
			jButton4_SaveExclusion.setFont(new Font("Candara", Font.PLAIN, 12));
			jButton4_SaveExclusion
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							final int nbDeLigne = jList_Exclut.getModel()
									.getSize();
							int eneregistrement_succe = 0;
							if (nbDeLigne == 0) {
								final boolean succes = GestionDemandes
										.executeRequete("DELETE FROM LISTE_EXCLUT");
								if (succes == true) {

									JOptionPane.showMessageDialog(null,
											"Effacement Ok", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									Historique
											.ecrire("Effacement reussi de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT");

								} else {

									JOptionPane.showMessageDialog(null,
											"Effacement erreur", "Erreur",
											JOptionPane.WARNING_MESSAGE);
									Historique
											.ecrire("Effacement échoué de la table LISTE_EXCLUT avec la requete DELETE FROM LISTE_EXCLUT");

								}
							} else {
								int nbEnregistrementPresent = 0;
								// on commence par vider la table LISTE_FICHIER
								boolean succes = GestionDemandes
										.executeRequete("DELETE FROM LISTE_EXCLUT");

								for (int i = 0; i < nbDeLigne; i++) {

									final Object objet = ModeleDeListeExclu
											.getElementAt(i);
									final String LigneAInserer = objet
											.toString();
									// on verifie si cette ligne n'est pas deja
									// presente dans la bdd
									try {
										nbEnregistrementPresent = Integer
												.parseInt(GestionDemandes
														.executeRequeteEtRetourne1Champ("SELECT count(*) FROM LISTE_EXCLUT WHERE EMPLACEMENT_FICHIER = '"
																+ LigneAInserer
																+ "'"));
									} catch (final SQLException e1) {

									}
									if (nbEnregistrementPresent == 0) {
										succes = GestionDemandes
												.executeRequete("INSERT INTO LISTE_EXCLUT VALUES ('"
														+ LigneAInserer + "')");
										if (succes == true) {

											eneregistrement_succe++;
											Historique
													.ecrire("Enregistrement dans la base d'un nouveau fichier/dossier à exclure :"
															+ LigneAInserer);

										} else {

											JOptionPane
													.showMessageDialog(
															null,
															"Enregistrement erreur",
															"Erreur",
															JOptionPane.WARNING_MESSAGE);
											Historique
													.ecrire("Erreur lors de l'enregistrement d'un nouveau fichier/dossier à exclure :"
															+ LigneAInserer);

										}
									}

								}
								if (eneregistrement_succe == nbDeLigne) {
									JOptionPane.showMessageDialog(null,
											"Enregistrement Ok", "Ok",
											JOptionPane.INFORMATION_MESSAGE);
									// Historique.ecrire("Enregistrement dans la base d'un nouveau fichier/dossier à exclure :"
									// +LigneAInserer);
								}

							}

						}
					});
		}
		return jButton4_SaveExclusion;
	}

	/**
	 * This method initializes jButton5_Refresh
	 * @return javax.swing.JButton
	 */
	private JButton getJButton5_Refresh() {
		if (jButton5_Refresh == null) {
			jButton5_Refresh = new JButton();
			jButton5_Refresh.setBounds(new Rectangle(724, 235, 50, 50));
			jButton5_Refresh.setIcon(new ImageIcon(getClass().getResource(
					"/rafraichir.png")));
			jButton5_Refresh
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent e) {

							jLabel_Date_Sauvegarde.setText("");
							jLabel_Taille_Sauvegarde.setText("");
							jLabel_ID_Sauvegarde.setText("");

							Thread_RefreshUI t = new Thread_RefreshUI(
									jLabel_Date, jLabel_Version, jList_Ajout,
									ModeleDeListe, jList_Exclut,
									ModeleDeListeExclu, jList_Visu,
									ModeleDeListeVisu,
									jTextField_CheminSauvegarde,
									jTextFieldHeure, jTextFieldMinutes,
									jCheckBox_EnvoiMailAuSt,
									jCheckBox_ArretMachine, jCheckBoxLundi,
									jCheckBoxMardi, jCheckBoxMercredi,
									jCheckBoxJeudi, jCheckBoxVendredi,
									jCheckBoxSamedi, jCheckBoxDimanche);
							t.start();

						}
					});
		}
		return jButton5_Refresh;
	}

	/**
	 * This method initializes jPanel1
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel7 = new JLabel();
			jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel7.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel7.setBounds(new Rectangle(331, 40, 54, 23));
			jLabel7.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel7.setText("mm");
			jLabel6 = new JLabel();
			jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel6.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel6.setBounds(new Rectangle(239, 39, 35, 25));
			jLabel6.setFont(new Font("Candara", Font.PLAIN, 12));
			jLabel6.setText("hh");
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.add(getJButton6(), null);
			jPanel1.add(getJTabbedPane1(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jCheckBoxLundi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLundi() {
		if (jCheckBoxLundi == null) {
			jCheckBoxLundi = new JCheckBox();
			jCheckBoxLundi.setText("Lundi");
			jCheckBoxLundi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxLundi.setBounds(new Rectangle(31, 19, 115, 20));
		}
		return jCheckBoxLundi;
	}

	/**
	 * This method initializes jCheckBoxMardi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMardi() {
		if (jCheckBoxMardi == null) {
			jCheckBoxMardi = new JCheckBox();
			jCheckBoxMardi.setText("Mardi");
			jCheckBoxMardi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxMardi.setBounds(new Rectangle(31, 66, 115, 20));
		}
		return jCheckBoxMardi;
	}

	/**
	 * This method initializes jCheckBoxMercredi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMercredi() {
		if (jCheckBoxMercredi == null) {
			jCheckBoxMercredi = new JCheckBox();
			jCheckBoxMercredi.setText("Mercredi");
			jCheckBoxMercredi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxMercredi.setBounds(new Rectangle(31, 104, 115, 20));
		}
		return jCheckBoxMercredi;
	}

	/**
	 * This method initializes jCheckBoxJeudi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxJeudi() {
		if (jCheckBoxJeudi == null) {
			jCheckBoxJeudi = new JCheckBox();
			jCheckBoxJeudi.setText("Jeudi");
			jCheckBoxJeudi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxJeudi.setBounds(new Rectangle(31, 152, 115, 20));
		}
		return jCheckBoxJeudi;
	}

	/**
	 * This method initializes jCheckBoxVendredi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxVendredi() {
		if (jCheckBoxVendredi == null) {
			jCheckBoxVendredi = new JCheckBox();
			jCheckBoxVendredi.setText("Vendredi");
			jCheckBoxVendredi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxVendredi.setBounds(new Rectangle(31, 195, 115, 20));
		}
		return jCheckBoxVendredi;
	}

	/**
	 * This method initializes jCheckBoxSamedi
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSamedi() {
		if (jCheckBoxSamedi == null) {
			jCheckBoxSamedi = new JCheckBox();
			jCheckBoxSamedi.setText("Samedi");
			jCheckBoxSamedi.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxSamedi.setBounds(new Rectangle(31, 241, 115, 20));
		}
		return jCheckBoxSamedi;
	}

	/**
	 * This method initializes jCheckBoxDimanche
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDimanche() {
		if (jCheckBoxDimanche == null) {
			jCheckBoxDimanche = new JCheckBox();
			jCheckBoxDimanche.setText("Dimanche");
			jCheckBoxDimanche.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBoxDimanche.setBounds(new Rectangle(31, 289, 115, 20));
		}
		return jCheckBoxDimanche;
	}

	/**
	 * This method initializes jButton6
	 * @return javax.swing.JButton
	 */
	private JButton getJButton6() {
		if (jButton6 == null) {
			jButton6 = new JButton();
			jButton6.setBounds(new Rectangle(356, 518, 159, 32));
			jButton6.setText("Enregistrer");
			jButton6.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {

					int erreur = 0;
					final boolean lundi = jCheckBoxLundi.isSelected();
					if (lundi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'LUNDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'LUNDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final boolean mardi = jCheckBoxMardi.isSelected();
					if (mardi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'MARDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'MARDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}
					final boolean mercredi = jCheckBoxMercredi.isSelected();
					if (mercredi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'MERCREDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'MERCREDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final boolean jeudi = jCheckBoxJeudi.isSelected();
					if (jeudi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'JEUDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'JEUDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final boolean vendredi = jCheckBoxVendredi.isSelected();
					if (vendredi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'VENDREDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'VENDREDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final boolean samedi = jCheckBoxSamedi.isSelected();
					if (samedi) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'SAMEDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'SAMEDI' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final boolean dimanche = jCheckBoxDimanche.isSelected();
					if (dimanche) {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 1 where JOUR like 'DIMANCHE' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}

					} else {
						final String Update = "UPDATE PLANIF set IS_SELECTED = 0 where JOUR like 'DIMANCHE' ";
						final boolean succes = GestionDemandes
								.executeRequete(Update);
						if (!succes) {
							erreur++;
						}
					}

					final int heure = Integer.parseInt(jTextFieldHeure
							.getText());
					final int minutes = Integer.parseInt(jTextFieldMinutes
							.getText());

					final String UpdateHeure = "UPDATE HORAIRE set HEURE="
							+ heure + ";";
					final boolean succes = GestionDemandes
							.executeRequete(UpdateHeure);
					if (!succes) {
						erreur++;
					}
					final String UpdateMinutes = "UPDATE HORAIRE set MINUTES="
							+ minutes + ";";
					final boolean succes1 = GestionDemandes
							.executeRequete(UpdateMinutes);
					if (!succes1) {
						erreur++;
					}

					final boolean ARRET_MACHINE = jCheckBox_ArretMachine
							.isSelected();
					if (ARRET_MACHINE) {
						final String UpdateArret_Machine = "UPDATE HORAIRE set ARRET_MACHINE=1";
						final boolean succes11 = GestionDemandes
								.executeRequete(UpdateArret_Machine);
						if (!succes11) {
							erreur++;
						}
					} else {
						final String UpdateArret_Machine = "UPDATE HORAIRE set ARRET_MACHINE=0";
						final boolean succes11 = GestionDemandes
								.executeRequete(UpdateArret_Machine);
						if (!succes11) {
							erreur++;
						}
					}
					final boolean ENVOI_MAIL = jCheckBox_EnvoiMailAuSt
							.isSelected();
					if (ENVOI_MAIL) {
						final String UpdateEnvoiMail = "UPDATE HORAIRE set ENVOI_MAIL_ST=1";
						final boolean succes11 = GestionDemandes
								.executeRequete(UpdateEnvoiMail);
						if (!succes11) {
							erreur++;
						}
					} else {
						final String UpdateEnvoiMail = "UPDATE HORAIRE set ENVOI_MAIL_ST=0";
						final boolean succes11 = GestionDemandes
								.executeRequete(UpdateEnvoiMail);
						if (!succes11) {
							erreur++;
						}
					}

					if (erreur > 0) {
						JOptionPane.showMessageDialog(null,
								"Erreur à l'enregistrement", "Erreur",
								JOptionPane.WARNING_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Enregistrement reussi", "Ok",
								JOptionPane.INFORMATION_MESSAGE);
					}

				}
			});
		}
		return jButton6;
	}

	/**
	 * This method initializes jTextFieldHeure
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldHeure() {
		if (jTextFieldHeure == null) {
			jTextFieldHeure = new JTextField();
			jTextFieldHeure.setText("Heure");
			jTextFieldHeure.setFont(new Font("Candara", Font.PLAIN, 12));
			jTextFieldHeure.setBounds(new Rectangle(187, 36, 53, 31));
		}
		return jTextFieldHeure;
	}

	/**
	 * This method initializes jTextFieldMinutes
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldMinutes() {
		if (jTextFieldMinutes == null) {
			jTextFieldMinutes = new JTextField();
			jTextFieldMinutes.setText("Minute");
			jTextFieldMinutes.setFont(new Font("Candara", Font.PLAIN, 12));
			jTextFieldMinutes.setBounds(new Rectangle(277, 35, 55, 32));
		}
		return jTextFieldMinutes;
	}

	/**
	 * This method initializes jTabbedPane1
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane1() {
		if (jTabbedPane1 == null) {
			jTabbedPane1 = new JTabbedPane();
			jTabbedPane1.setBounds(new Rectangle(1, 3, 870, 508));
			jTabbedPane1.addTab("Planification", null, getJPanel2(), null);
			jTabbedPane1.addTab("Paramètres de sauvegardes", null,
					getJPanel3(), null);
		}
		return jTabbedPane1;
	}

	/**
	 * This method initializes jPanel2
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.add(getJCheckBoxLundi(), null);
			jPanel2.add(getJCheckBoxMardi(), null);
			jPanel2.add(getJCheckBoxMercredi(), null);
			jPanel2.add(getJCheckBoxJeudi(), null);
			jPanel2.add(getJCheckBoxVendredi(), null);
			jPanel2.add(getJCheckBoxSamedi(), null);
			jPanel2.add(getJCheckBoxDimanche(), null);
			jPanel2.add(getJTextFieldHeure(), null);
			jPanel2.add(jLabel6, null);
			jPanel2.add(getJTextFieldMinutes(), null);
			jPanel2.add(jLabel7, null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(null);
			jPanel3.add(getJCheckBox_ArretMachine(), null);
			jPanel3.add(getJCheckBox_EnvoiMailAuSt(), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jButton_SauvegardeOk
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_SauvegardeOk() {
		if (jButton_SauvegardeOk == null) {
			jButton_SauvegardeOk = new JButton();
			jButton_SauvegardeOk
					.setHorizontalTextPosition(SwingConstants.TRAILING);
			jButton_SauvegardeOk.setPreferredSize(new Dimension(80, 80));
			jButton_SauvegardeOk.setLocation(new Point(584, 329));
			jButton_SauvegardeOk.setSize(new Dimension(214, 60));
			jButton_SauvegardeOk.setText("Sauvegarde Ok");
			jButton_SauvegardeOk.setHorizontalAlignment(SwingConstants.CENTER);
			jButton_SauvegardeOk.setIcon(new ImageIcon(getClass().getResource(
					"/ok.png")));
			jButton_SauvegardeOk.setVisible(false);
		}
		return jButton_SauvegardeOk;
	}

	/**
	 * This method initializes jButton_SauvegardeNok
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_SauvegardeNok() {
		if (jButton_SauvegardeNok == null) {
			jButton_SauvegardeNok = new JButton();
			jButton_SauvegardeNok.setIcon(new ImageIcon(getClass().getResource(
					"/erreur.png")));
			jButton_SauvegardeNok.setLocation(new Point(584, 398));
			jButton_SauvegardeNok.setSize(new Dimension(214, 60));
			jButton_SauvegardeNok.setText("Sauvegarde échouée");
			jButton_SauvegardeNok.setVisible(false);
		}
		return jButton_SauvegardeNok;
	}

	/**
	 * This method initializes jCheckBox_ArretMachine
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox_ArretMachine() {
		if (jCheckBox_ArretMachine == null) {
			jCheckBox_ArretMachine = new JCheckBox();
			jCheckBox_ArretMachine.setBounds(new Rectangle(21, 28, 314, 27));
			jCheckBox_ArretMachine.setSelected(false);
			jCheckBox_ArretMachine.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBox_ArretMachine
					.setText(" Eteindre l'ordinateur à la fin de la sauvegarde");
		}
		return jCheckBox_ArretMachine;
	}

	/**
	 * This method initializes jCheckBox_EnvoiMailAuSt
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox_EnvoiMailAuSt() {
		if (jCheckBox_EnvoiMailAuSt == null) {
			jCheckBox_EnvoiMailAuSt = new JCheckBox();
			jCheckBox_EnvoiMailAuSt.setBounds(new Rectangle(21, 61, 342, 20));
			jCheckBox_EnvoiMailAuSt
					.setToolTipText("Ceci comprend uniquement les historiques et les fichiers de configuration");
			jCheckBox_EnvoiMailAuSt
					.setFont(new Font("Candara", Font.PLAIN, 12));
			jCheckBox_EnvoiMailAuSt
					.setText(" Envoyer les traces par mail à la fermeture du logiciel");
		}
		return jCheckBox_EnvoiMailAuSt;
	}

	/**
	 * This method initializes jButtonRechercheDansSauvegarde
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonRechercheDansSauvegarde() {
		if (jButtonRechercheDansSauvegarde == null) {
			jButtonRechercheDansSauvegarde = new JButton();

			jButtonRechercheDansSauvegarde.setFont(new Font("Candara",
					Font.BOLD, 12));
			jButtonRechercheDansSauvegarde.setBounds(new Rectangle(511, 375,
					175, 33));
			jButtonRechercheDansSauvegarde
					.setText("Rechercher fichier/dossier");
			jButtonRechercheDansSauvegarde
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							new Fen_RechSauvegarde();
						}
					});
		}
		return jButtonRechercheDansSauvegarde;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
