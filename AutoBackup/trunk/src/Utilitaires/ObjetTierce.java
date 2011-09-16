package Utilitaires;

public class ObjetTierce<T, U, V> {

	/**
	 * Objet contenant n'importe quel type d'element
	 * @param <T> type element 0
	 * @param <U> type element 1
	 * @param p_element0 element 0
	 * @param p_element1 element 1
	 * @return ObjetPair
	 */
	public static <T, U, V> ObjetTierce<T, U, V> create(T p_element0,
			U p_element1, V p_element2) {
		return new ObjetTierce<T, U, V>(p_element0, p_element1, p_element2);
	}

	private T element0;
	private U element1;
	private V element2;

	/** Empty constructor. */
	public ObjetTierce() {

	}

	/**
	 * Full constructor.
	 * @param p_element0 premier element
	 * @param p_element1 deuxieme element
	 */
	public ObjetTierce(T p_element0, U p_element1, V p_element2) {
		element0 = p_element0;
		element1 = p_element1;
		element2 = p_element2;
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

	public V getElement2() {
		return element2;
	}

	public void setElement2(V element2) {
		this.element2 = element2;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object p_obj) {
		if ((p_obj == null) || !(p_obj instanceof ObjetTierce)) {
			return false;
		}
		ObjetTierce pair = (ObjetTierce) p_obj;
		return equalsElement(getElement0(), pair.getElement0())
				&& (equalsElement(getElement1(), pair.getElement1()) && (equalsElement(
						getElement2(), pair.getElement2())));
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
				^ ((getElement1() == null) ? 0 : getElement1().hashCode()
						^ ((getElement2() == null) ? 0 : getElement2()
								.hashCode()));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.valueOf(getElement0()) + " - "
				+ String.valueOf(getElement1()) + " - "
				+ String.valueOf(getElement2());
	}
}
