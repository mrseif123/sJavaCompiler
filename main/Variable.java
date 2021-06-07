package oop.ex6.main;

/**
 * A class that represents the Variable object which has a name, modifier type, type. We also overridden the
 * equals & hashCode functions so we will be able to put this object in a  Set  or a HashMap.
 */
class Variable {

	final String varName;
	String type;
	boolean isInitialized;
	boolean isFinal;

	/** Constructors */
	Variable(String varName) {
		this.varName = varName;
	}

	Variable(String varName, String type, boolean isInitialized, boolean isFinal) {
		this.varName = varName;
		this.type = type;
		this.isInitialized = isInitialized;
		this.isFinal = isFinal;
	}

	/**
	 * A function that overrides the Object Class equals function and compares between  2 Variable according
	 * to their names.
	 * @param o The other object.
	 * @return True if 2 Variables have the same name, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Variable variable = (Variable) o;
		return varName.equals(variable.varName);
	}

	/**
	 * @return The Variable name hashCode.
	 */
	@Override
	public int hashCode() {
		return varName.hashCode();
	}
}
