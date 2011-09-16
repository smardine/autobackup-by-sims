package Utilitaires;

import java.io.IOException;
import java.text.Normalizer;

public class ParseString {
	/**
	 * Parser un fichier csv de type C-xxx.csv
	 * @param stringToParse -String la chaine de caractere a verifier.
	 * @param Parser -String le delimiteur
	 * @param Fichier -String le fichier a parser
	 * @param structCD -int à 1 si on travaille sur la structure cd, à 0 sinon.
	 * @param site_maj -int à 1 si on travaille sur le site de maj, à 0 sinon.
	 */

	public static String parseConvention(String stringToParse,
			final String Parser, final String Fichier, final int structCD,
			final int site_maj) throws IOException {

		final int icodeActionConvention = 1, iListeCodeCategorie = 2, iListeSpecialite = 3, iMessageAuPS = 5, iIdentifiantOrgSign = 6, iLibelleOrgSign = 7, iTypeConv = 9, iCritereSecondaireConvention = 10, iNumOrgComplementaire = 12, iLibelleOrgComplementaire = 13, iNomDomaine = 22;

		final int indexOfComplementaire = Fichier.indexOf("Complémentaires");
		String repertoire = Fichier.substring(indexOfComplementaire)
				.toUpperCase();
		repertoire = addAntiSlach(repertoire);

		stringToParse = removeApostrophe(stringToParse);
		repertoire = removeApostrophe(repertoire);

		final String[] tabChaine = stringToParse.split(Parser);
		final String ETAT_MUTUELLE = tabChaine[icodeActionConvention - 1];

		final String LISTE_CODE_CATEGORIE = tabChaine[iListeCodeCategorie - 1];
		final String LISTE_SPECIALITE = tabChaine[iListeSpecialite - 1];
		final String MESSAGE_AU_PS = tabChaine[iMessageAuPS - 1];
		final String IDENTIFIANT_SIGNATAIRE = tabChaine[iIdentifiantOrgSign - 1];
		final String LIBELLE_SIGNATAIRE = tabChaine[iLibelleOrgSign - 1];
		final String TYPE_CONVENTION = tabChaine[iTypeConv - 1];
		final String CRITERE_SECONDAIRE = tabChaine[iCritereSecondaireConvention - 1];
		final String IDENTIFIANT_COMPLEMENTAIRE = tabChaine[iNumOrgComplementaire - 1];
		final String LIBELLE_COMPLEMENTAIRE = tabChaine[iLibelleOrgComplementaire - 1];
		final String DOMAINE = tabChaine[iNomDomaine - 1];

		// on a fini de traiter la ligne, on insere dans la base de données
		final String RequeteAjoutMutuelle = String
				.format(
						"INSERT INTO T_CONVENTIONS (CODE_ACTION, CODE_CATEGORIE, LISTE_SPECIALITE,MESSAGE_AU_PS,IDENT_SIGNATAIRE,LIBELLE_SIGNATAIRE,TYPE_CONVENTION,CRIT_SECONDAIRE,IDENT_COMPLEMENTAIRE,LIBELLE_COMPLEMENTAIRE,NOM_FICHIER,DOMAINE,STRUCT_CD,SITE_MAJ) "
								+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%d','%d')",
						ETAT_MUTUELLE, LISTE_CODE_CATEGORIE, LISTE_SPECIALITE,
						MESSAGE_AU_PS, IDENTIFIANT_SIGNATAIRE,
						LIBELLE_SIGNATAIRE, TYPE_CONVENTION,
						CRITERE_SECONDAIRE, IDENTIFIANT_COMPLEMENTAIRE,
						LIBELLE_COMPLEMENTAIRE, repertoire, DOMAINE, structCD,
						site_maj);

		final String RequeteAjoutMutuelleSansAccent = removeAccent(RequeteAjoutMutuelle);
		return RequeteAjoutMutuelleSansAccent.toUpperCase();
	}

	/**
	 * Parser un fichier csv de type R-xxx.csv
	 * @param stringToParse -String la chaine de caractere a verifier.
	 * @param Parser -String le delimiteur
	 * @param Fichier -String le fichier a parser
	 * @param structCD -int à 1 si on travaille sur la structure cd, à 0 sinon.
	 * @param siteMaj -int à 1 si on travaille sur le site de maj, à 0 sinon.
	 */
	public static String parseRegroupement(String stringToParse,
			final String Parser, final String Fichier, final int structCD,
			final int siteMaj) {
		final int icodeAction = 1, iListeCodeCategorieRegroupement = 2, iListeSpecialiteRegroupement = 3, iNumerocomplementaire = 4, ilibellecomplementaire = 5, iTypeConvention = 6, iCritereSecondaire = 8, iIdentifiantOrgSignataireRegroupement = 9, IlibelleOrgSignataireRegroupement = 10;
		final int indexOfComplementaire = Fichier.indexOf("Complémentaires");
		String repertoire = Fichier.substring(indexOfComplementaire)
				.toUpperCase();

		repertoire = addAntiSlach(repertoire);
		stringToParse = removeApostrophe(stringToParse);
		repertoire = removeApostrophe(repertoire);

		final String[] tabChaine = stringToParse.split(Parser);

		final String ETAT_MUTUELLE = tabChaine[icodeAction - 1];
		final String LISTE_CODE_CATEGORIE = tabChaine[iListeCodeCategorieRegroupement - 1];
		final String LISTE_SPECIALITE = tabChaine[iListeSpecialiteRegroupement - 1];
		final String IDENTIFIANT_COMPLEMENTAIRE = tabChaine[iNumerocomplementaire - 1];
		final String LIBELLE_COMPLEMENTAIRE = tabChaine[ilibellecomplementaire - 1];

		final String TYPE_CONVENTION = tabChaine[iTypeConvention - 1];
		final String CRITERE_SECONDAIRE = tabChaine[iCritereSecondaire - 1];
		final String IDENTIFIANT_SIGNATAIRE = tabChaine[iIdentifiantOrgSignataireRegroupement - 1];
		final String LIBELLE_SIGNATAIRE = tabChaine[IlibelleOrgSignataireRegroupement - 1];

		final String RequeteAjoutMutuelle = String
				.format(
						"INSERT INTO T_REGROUPEMENT (CODE_ACTION, CODE_CATEGORIE, LISTE_SPECIALITE,IDENT_COMPLEMENTAIRE,LIBELLE_COMPLEMENTAIRE,TYPE_CONVENTION,CRIT_SECONDAIRE,IDENT_SIGNATAIRE,LIBELLE_SIGNATAIRE,NOM_FICHIER,STRUCT_CD,SITE_MAJ)"
								+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%d','%d')",
						ETAT_MUTUELLE, LISTE_CODE_CATEGORIE, LISTE_SPECIALITE,
						IDENTIFIANT_COMPLEMENTAIRE, LIBELLE_COMPLEMENTAIRE,
						TYPE_CONVENTION, CRITERE_SECONDAIRE,
						IDENTIFIANT_SIGNATAIRE, LIBELLE_SIGNATAIRE, repertoire,
						structCD, siteMaj);

		final String RequeteAjoutMutuelleSansAccent = removeAccent(RequeteAjoutMutuelle);
		return RequeteAjoutMutuelleSansAccent.toUpperCase();
	}

	/**
	 * Parser un fichier csv de type C-xxx.csv dans import CSV
	 * @param stringToParse -String la chaine de caractere a verifier.
	 * @param Parser -String le delimiteur
	 * @param fichier -String le fichier a parser
	 * @param structCD -int à 1 si on travaille sur la structure cd, à 0 sinon.
	 * @param siteMaj -int à 1 si on travaille sur le site de maj, à 0 sinon.
	 */
	public static String parseConventionImportCSV(String stringToParse,
			final String Parser, String fichier, final int structCD,
			final int siteMaj) {

		final int icodeActionConvention = 1, iListeCodeCategorie = 2, iListeSpecialite = 3, iMessageAuPS = 5, iIdentifiantOrgSign = 6, iLibelleOrgSign = 7, iTypeConv = 9, iCritereSecondaireConvention = 10, iNumOrgComplementaire = 12, iLibelleOrgComplementaire = 13, iNomDomaine = 22;
		String ETAT_MUTUELLE = null, LISTE_CODE_CATEGORIE = null, LISTE_SPECIALITE = null, MESSAGE_AU_PS = null, IDENTIFIANT_SIGNATAIRE = null, LIBELLE_SIGNATAIRE = null, TYPE_CONVENTION = null, CRITERE_SECONDAIRE = null, IDENTIFIANT_COMPLEMENTAIRE = null, LIBELLE_COMPLEMENTAIRE = null, DOMAINE = null;

		fichier = addAntiSlach(fichier);
		stringToParse = removeApostrophe(stringToParse);
		fichier = removeApostrophe(fichier);

		final String[] tabChaine = stringToParse.split(Parser);
		final int nbchamps = tabChaine.length;
		if (nbchamps >= (icodeActionConvention - 1)) {
			ETAT_MUTUELLE = tabChaine[icodeActionConvention - 1];
		}
		if (nbchamps >= (iListeCodeCategorie - 1)) {
			LISTE_CODE_CATEGORIE = tabChaine[icodeActionConvention - 1];
		}
		if (nbchamps >= (iListeSpecialite - 1)) {
			LISTE_SPECIALITE = tabChaine[iListeSpecialite - 1];
		}
		if (nbchamps >= (iMessageAuPS - 1)) {
			MESSAGE_AU_PS = tabChaine[iMessageAuPS - 1];
		}
		if (nbchamps >= (iIdentifiantOrgSign - 1)) {
			IDENTIFIANT_SIGNATAIRE = tabChaine[iIdentifiantOrgSign - 1];
		}
		if (nbchamps >= (iLibelleOrgSign - 1)) {
			LIBELLE_SIGNATAIRE = tabChaine[iLibelleOrgSign - 1];
		}
		if (nbchamps >= (iTypeConv - 1)) {
			TYPE_CONVENTION = tabChaine[iTypeConv - 1];
		}
		if (nbchamps >= (iCritereSecondaireConvention - 1)) {
			CRITERE_SECONDAIRE = tabChaine[iCritereSecondaireConvention - 1];
		}
		if (nbchamps >= (iNumOrgComplementaire - 1)) {
			IDENTIFIANT_COMPLEMENTAIRE = tabChaine[iNumOrgComplementaire - 1];
		}
		if (nbchamps >= (iLibelleOrgComplementaire - 1)) {
			LIBELLE_COMPLEMENTAIRE = tabChaine[iLibelleOrgComplementaire - 1];
		}
		if (nbchamps >= (iNomDomaine - 1)) {
			DOMAINE = tabChaine[iNomDomaine - 1];
		}

		final String RequeteAjoutMutuelle = String
				.format(
						"INSERT INTO T_CONVENTIONS_IMPORT_CSV (CODE_ACTION, CODE_CATEGORIE, LISTE_SPECIALITE,MESSAGE_AU_PS,IDENT_SIGNATAIRE,LIBELLE_SIGNATAIRE,TYPE_CONVENTION,CRIT_SECONDAIRE,IDENT_COMPLEMENTAIRE,LIBELLE_COMPLEMENTAIRE,NOM_FICHIER,DOMAINE,STRUCT_CD,SITE_MAJ)"
								+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%d','%d')",
						ETAT_MUTUELLE, LISTE_CODE_CATEGORIE, LISTE_SPECIALITE,
						MESSAGE_AU_PS, IDENTIFIANT_SIGNATAIRE,
						LIBELLE_SIGNATAIRE, TYPE_CONVENTION,
						CRITERE_SECONDAIRE, IDENTIFIANT_COMPLEMENTAIRE,
						LIBELLE_COMPLEMENTAIRE, fichier, DOMAINE, structCD,
						siteMaj);

		final String RequeteAjoutMutuelleSansAccent = removeAccent(RequeteAjoutMutuelle);
		return RequeteAjoutMutuelleSansAccent.toUpperCase();
	}

	/**
	 * Parser un fichier csv de type R-xxx.csv dans import csv.
	 * @param stringToParse -String la chaine de caractere a verifier.
	 * @param Parser -String le delimiteur
	 * @param fichier -String le fichier a parser
	 * @param structCD -int à 1 si on travaille sur la structure cd, à 0 sinon.
	 * @param siteMaj -int à 1 si on travaille sur le site de maj, à 0 sinon.
	 */
	public static String parseRegroupementImportCSV(String stringToParse,
			final String Parser, String fichier, final int structCD,
			final int siteMaj) {

		final int icodeAction = 1, iListeCodeCategorieRegroupement = 2, iListeSpecialiteRegroupement = 3, iNumerocomplementaire = 4, ilibellecomplementaire = 5, iTypeConvention = 6, iCritereSecondaire = 8, iIdentifiantOrgSignataireRegroupement = 9, IlibelleOrgSignataireRegroupement = 10;
		fichier = addAntiSlach(fichier);
		stringToParse = removeApostrophe(stringToParse);
		fichier = removeApostrophe(fichier);

		final String[] tabChaine = stringToParse.split(Parser);
		final String ETAT_MUTUELLE = tabChaine[icodeAction - 1];
		final String LISTE_CODE_CATEGORIE = tabChaine[iListeCodeCategorieRegroupement - 1];
		final String LISTE_SPECIALITE = tabChaine[iListeSpecialiteRegroupement - 1];
		final String IDENTIFIANT_COMPLEMENTAIRE = tabChaine[iNumerocomplementaire - 1];
		final String LIBELLE_COMPLEMENTAIRE = tabChaine[ilibellecomplementaire - 1];
		final String TYPE_CONVENTION = tabChaine[iTypeConvention - 1];
		final String CRITERE_SECONDAIRE = tabChaine[iCritereSecondaire - 1];
		final String IDENTIFIANT_SIGNATAIRE = tabChaine[iIdentifiantOrgSignataireRegroupement - 1];
		final String LIBELLE_SIGNATAIRE = tabChaine[IlibelleOrgSignataireRegroupement - 1];

		final String RequeteAjoutMutuelle = String
				.format(
						"INSERT INTO T_REGROUPEMENT_IMPORT_CSV (CODE_ACTION, CODE_CATEGORIE, LISTE_SPECIALITE,IDENT_COMPLEMENTAIRE,LIBELLE_COMPLEMENTAIRE,TYPE_CONVENTION,CRIT_SECONDAIRE,IDENT_SIGNATAIRE,LIBELLE_SIGNATAIRE,NOM_FICHIER,STRUCT_CD,SITE_MAJ)"
								+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%d','%d')",
						ETAT_MUTUELLE, LISTE_CODE_CATEGORIE, LISTE_SPECIALITE,
						IDENTIFIANT_COMPLEMENTAIRE, LIBELLE_COMPLEMENTAIRE,
						TYPE_CONVENTION, CRITERE_SECONDAIRE,
						IDENTIFIANT_SIGNATAIRE, LIBELLE_SIGNATAIRE, fichier,
						structCD, siteMaj);

		final String RequeteAjoutMutuelleSansAccent = removeAccent(RequeteAjoutMutuelle);
		return RequeteAjoutMutuelleSansAccent.toUpperCase();
	}

	/**
	 * Parser un fichier csv de type correspondance.csv dans import CSV
	 * @param stringToParse -String la chaine de caractere a verifier.
	 * @param Parser -String le delimiteur
	 * @param fichier -String le fichier a parser
	 * @param structCD -int à 1 si on travaille sur la structure cd, à 0 sinon.
	 * @param siteMaj -int à 1 si on travaille sur le site de maj, à 0 sinon.
	 */
	public static String parseCorrespondanceImportCSV(String stringToParse,
			final String Parser, String fichier, final int structCD,
			final int siteMaj) {

		final int iMutNum = 1, icodeorgassocie = 2, iident_org_compl = 3, ilibelle_org_compl = 4;
		fichier = addAntiSlach(fichier);
		stringToParse = removeApostrophe(stringToParse);
		fichier = removeApostrophe(fichier);

		final String[] tabChaine = stringToParse.split(Parser);
		final String MUT_NUM = tabChaine[iMutNum - 1];
		final String CODE_ORG_ASSOCIE = tabChaine[icodeorgassocie - 1];
		final String IDENT_ORG_COMPL = tabChaine[iident_org_compl - 1];
		final String LIB_ORG_COMPL = tabChaine[ilibelle_org_compl - 1];

		final String RequeteAjoutMutuelle = String
				.format(
						"INSERT INTO T_CORRESPONDANCE (MUTNUM, ORG_ASSOCIE, IDENT_ORG_COMPL,LIBELLE_ORG_COMPL,NOM_FICHIER,struct_cd,site_maj)"
								+ "VALUES ('%s','%s','%s','%s','%s','%d','%d')",
						MUT_NUM, CODE_ORG_ASSOCIE, IDENT_ORG_COMPL,
						LIB_ORG_COMPL, fichier, structCD, siteMaj);

		final String RequeteAjoutMutuelleSansAccent = removeAccent(RequeteAjoutMutuelle);
		return RequeteAjoutMutuelleSansAccent.toUpperCase();
	}

	/**
	 * Retirer les accent d'une chaine de caractere
	 * @param source -String la chaine de caractere a traiter.
	 * @return la chaine de caractere corrigée. -String
	 */
	public static String removeAccent(final String source) {
		return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll(
				"[^\\p{ASCII}]", "");
	}

	/**
	 * Retirer les apostrophe d'une chaine de caractere
	 * @param source -String la chaine de caractere a traiter.
	 * @return la chaine de caractere corrigée. -String
	 */
	public static String removeApostrophe(final String source) {
		return source.replaceAll("'", " ");
	}

	/**
	 * remplacer les antislash par des slash
	 * @param source -String la chaine de caractere a traiter.
	 * @return la chaine de caractere corrigée. -String
	 */
	private static String addAntiSlach(final String source) {

		return source.replace('\\', '/');
	}

	/**
	 * Retirer les apostrophe d'une chaine de caractere
	 * @param source -String la chaine de caractere a traiter.
	 * @return la chaine de caractere corrigée. -String
	 */
	public static String removeCrochet(final String source) {
		final String source1 = source.replace("[", "(");
		final String source2 = source1.replace("]", ")");
		return source2;
	}

	public static String removeParenthese(final String source) {
		final String source1 = source.replace("(", "");
		final String source2 = source1.replace(")", "");
		return source2;
	}

	/**
	 * Retirer les apostrophe d'une chaine de caractere
	 * @param source -String la chaine de caractere a traiter.
	 * @return la chaine de caractere corrigée. -String
	 */
	public static String removevirgule(final String source) {
		// String source1 = source.replace(",", "");
		// String source2 =source1.replace("]",")");
		return source.replace(",", "");
	}

	public static String remplaceVirguleParPointVirgule(final String source) {

		return source.replace(",", ";");
	}

	public static String removeLineFeed(final String source) {

		return source.replace("\n", "Line_feed");
	}

	public static String removeCageReturn(final String source) {

		return source.replace("\r", "Cage_Return");
	}

	public static String remplaceLineFeed(final String source) {

		return source.replace("Line_feed", "\n");
	}

	public static String remplaceCageReturn(final String source) {

		return source.replace("Cage_Return", "\r");
	}

	/*
	 * public static String removeFirstSpace(String source) { for (int
	 * i=0;i<source.length();i++){ if (Character.isLetter(source.charAt(i))){
	 * return source.substring(i); } } return source; }
	 */

}
