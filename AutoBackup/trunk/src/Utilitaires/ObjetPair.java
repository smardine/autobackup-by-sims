package Utilitaires;

public class ObjetPair<T, U> {

	/**
	 * Objet contenant n'importe quel type d'element
	 * @param <T> type element 0
	 * @param <U> type element 1
	 * @param p_element0 element 0
	 * @param p_element1 element 1
	 * @return ObjetPair
	 */
	public static <T, U> ObjetPair<T, U> create(T p_element0, U p_element1) {
		return new ObjetPair<T, U>(p_element0, p_element1);
	}

	private T element0;
	private U element1;

	/** Empty constructor. */
	public ObjetPair() {

	}

	/**
	 * Full constructor.
	 * @param p_element0 premier element
	 * @param p_element1 deuxieme element
	 */
	public ObjetPair(T p_element0, U p_element1) {
		element0 = p_element0;
		element1 = p_element1;
	}

	/**
	 * @return the element0
	 */
	public T getElement0() {
		return element0;
	}

	/**
	 * @param p_element0 the element0 to set
	 */
	public void setElement0(T p_element0) {
		element0 = p_element0;
	}

	/**
	 * @return the element1
	 */
	public U getElement1() {
		return element1;
	}

	/**
	 * @param p_element1 the element1 to set
	 */
	public void setElement1(U p_element1) {
		element1 = p_element1;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object p_obj) {
		if ((p_obj == null) || !(p_obj instanceof ObjetPair)) {
			return false;
		}
		ObjetPair pair = (ObjetPair) p_obj;
		return equalsElement(getElement0(), pair.getElement0())
				&& (equalsElement(getElement1(), pair.getElement1()));
	}

	/**
	 * @param p_reference element de reference
	 * @param p_aComparer element a comparer
	 * @return vrai si les elements sont egaux
	 */
	private boolean equalsElement(Object p_reference, Object p_aComparer) {
		if (p_reference == null) {
			return p_aComparer == null;
		}
		return p_reference.equals(p_aComparer);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return ((getElement0() == null) ? 0 : getElement0().hashCode())
				^ ((getElement1() == null) ? 0 : getElement1().hashCode());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.valueOf(getElement0()) + " - "
				+ String.valueOf(getElement1());
	}
}
