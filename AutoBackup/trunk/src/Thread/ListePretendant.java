package Thread;

import java.util.ArrayList;

/**
 * @author smardine
 */
public class ListePretendant extends ArrayList<Pretendant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8170185926536026557L;

	// private final ArrayList<Pretendant> pretendants = new
	// ArrayList<Pretendant>();

	/**
	 * constructeur
	 */

	public ListePretendant() {

	}

	/**
	 * Obtenir la liste
	 * @return ListePretandant, la liste complete des elements
	 */
	public ListePretendant getListePrestendant() {
		return this;
	}

	/**
	 * cette fonction a pour but d'ajouter un element a la liste, uiquement si
	 * celui n'est pas deja dedans.
	 * @param p -Pretendant, un element a ajouter a la liste
	 */
	public void ajout(Pretendant p) {
		boolean ajout = true;
		for (Pretendant p2 : this) {
			if (p2.getChemin().equals(p.getChemin())) {
				ajout = false;
			}
		}

		if (ajout) {
			this.add(p);
		}
	}

}
