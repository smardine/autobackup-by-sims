package Enum;

import java.util.Vector;

public enum EnChoixRecherche {
	CONTIENT(0, "Contient"), EXACTE(1, "Expression excate"), FINI(2, "Fini par"), COMMENCE(
			3, "Commence par");

	private int id;
	private String lib;

	EnChoixRecherche(int p_id, String p_lib) {
		id = p_id;
		lib = p_lib;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLib() {
		return lib;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

	public static Vector<String> getListLib() {
		Vector<String> v = new Vector<String>();
		EnChoixRecherche[] tab = values();

		for (EnChoixRecherche ligne : tab) {
			v.add(ligne.getLib());
		}
		return v;

	}

}
