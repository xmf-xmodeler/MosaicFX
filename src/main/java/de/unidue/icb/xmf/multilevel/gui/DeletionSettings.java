package de.unidue.icb.xmf.multilevel.gui;

public class DeletionSettings {

	private boolean globalSettings;
	
	private boolean deleteAll;
	
	private boolean deleteSlotValues;
	
	private boolean deleteOnlyNull;
	
	/**
	 * @param globalSettings
	 * @param deleteAll
	 * @param deleteSlotValues
	 * @param deleteOnlyNull
	 */
	public DeletionSettings(boolean globalSettings, boolean deleteAll,
			boolean deleteSlotValues, boolean deleteOnlyNull) {
		super();
		this.globalSettings = globalSettings;
		this.deleteAll = deleteAll;
		this.deleteSlotValues = deleteSlotValues;
		this.deleteOnlyNull = deleteOnlyNull;
	}

	/**
	 * @return the globalSettings
	 */
	public boolean isGlobalSettings() {
		return globalSettings;
	}

	/**
	 * @param globalSettings the globalSettings to set
	 */
	public void setGlobalSettings(boolean globalSettings) {
		this.globalSettings = globalSettings;
	}

	/**
	 * @return the deleteAll
	 */
	public boolean isDeleteAll() {
		return deleteAll;
	}

	/**
	 * @param deleteAll the deleteAll to set
	 */
	public void setDeleteAll(boolean deleteAll) {
		this.deleteAll = deleteAll;
	}

	/**
	 * @return the deleteSlotValues
	 */
	public boolean isDeleteSlotValues() {
		return deleteSlotValues;
	}

	/**
	 * @param deleteSlotValues the deleteSlotValues to set
	 */
	public void setDeleteSlotValues(boolean deleteSlotValues) {
		this.deleteSlotValues = deleteSlotValues;
	}

	/**
	 * @return the deleteOnlyNull
	 */
	public boolean isDeleteOnlyNull() {
		return deleteOnlyNull;
	}

	/**
	 * @param deleteOnlyNull the deleteOnlyNull to set
	 */
	public void setDeleteOnlyNull(boolean deleteOnlyNull) {
		this.deleteOnlyNull = deleteOnlyNull;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "DeletionSettings [globalSettings=" + globalSettings
				+ ", deleteAll=" + deleteAll + ", deleteSlotValues="
				+ deleteSlotValues + ", deleteOnlyNull=" + deleteOnlyNull + "]";
	}
	
	
}
