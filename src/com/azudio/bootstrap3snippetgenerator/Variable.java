package com.azudio.bootstrap3snippetgenerator;

public class Variable {

	private String defaultValue;
	private String name;
	private String description;

	/**
	 * @param name
	 */
	public Variable(String name) {
		this(name, "");
	}

	/**
	 * @param defaultValue
	 * @param name
	 * @param description
	 */
	public Variable(String name, String description) {
		this(name, description, null);
	}

	/**
	 * @param defaultValue
	 * @param name
	 * @param description
	 */
	public Variable(String name, String description, String defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
