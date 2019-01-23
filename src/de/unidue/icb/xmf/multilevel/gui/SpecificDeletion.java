package de.unidue.icb.xmf.multilevel.gui;

public class SpecificDeletion {
	private int definitionLayer;
	private int instantiationLayer;
	
	private int deletionUntilLayer;
	
//	private boolean deleteSlotValues;
	
//	public static final int deleteMode_All = 1;
//	public static final int deleteMode_NullValue = 2;
//	public static final int deleteMode_Expression = 3;
//	
//	private int deleteMode;
//	private String deleteExpression;
	/**
	 * @param definitionLayer
	 * @param instantiationLayer
	 * @param deletionDepth
	 * @param deleteSlotValues
	 * @param deleteAll
	 * @param deleteNullValues
	 * @param deleteWithExpression
	 * @param deleteExpression
	 */
//	public SpecificDeletion(int definitionLayer, int instantiationLayer,
//			int deletionUntilLayer, boolean deleteSlotValues, int deleteMode, String deleteExpression) {
	public SpecificDeletion(int definitionLayer, int instantiationLayer, int deletionUntilLayer) {		
		super();
		this.definitionLayer = definitionLayer;
		this.instantiationLayer = instantiationLayer;
		this.deletionUntilLayer = deletionUntilLayer;
//		this.deleteSlotValues = deleteSlotValues;
//		this.deleteMode = deleteMode;
//		this.deleteExpression = deleteExpression;
	}
	
	public SpecificDeletion(int definitionLayer, int instantiationLayer) {
		super();
		this.definitionLayer = definitionLayer;
		this.instantiationLayer = instantiationLayer;
		this.deletionUntilLayer = definitionLayer;
//		this.deleteSlotValues = false;
//		this.deleteMode = deleteMode_All ;
//		this.deleteExpression = "";
	}

	/**
	 * @return the definitionLayer
	 */
	public int getDefinitionLayer() {
		return definitionLayer;
	}

	/**
	 * @param definitionLayer the definitionLayer to set
	 */
	public void setDefinitionLayer(int definitionLayer) {
		this.definitionLayer = definitionLayer;
	}

	/**
	 * @return the instantiationLayer
	 */
	public int getInstantiationLayer() {
		return instantiationLayer;
	}

	/**
	 * @param instantiationLayer the instantiationLayer to set
	 */
	public void setInstantiationLayer(int instantiationLayer) {
		this.instantiationLayer = instantiationLayer;
	}

	/**
	 * @return the deletionDepth
	 */
	public int getDeletionUntilLayer() {
		return deletionUntilLayer;
	}

	/**
	 * @param deletionDepth the deletionDepth to set
	 */
	public void setDeletionUntilLayer(int deletionUntilLayer) {
		this.deletionUntilLayer = deletionUntilLayer;
	}
//
//	/**
//	 * @return the deleteSlotValues
//	 */
//	public boolean isDeleteSlotValues() {
//		return deleteSlotValues;
//	}
//
//	/**
//	 * @param deleteSlotValues the deleteSlotValues to set
//	 */
//	public void setDeleteSlotValues(boolean deleteSlotValues) {
//		this.deleteSlotValues = deleteSlotValues;
//	}

//	/**
//	 * @return the deleteExpression
//	 */
//	public String getDeleteExpression() {
//		return deleteExpression;
//	}
//
//	/**
//	 * @param deleteExpression the deleteExpression to set
//	 */
//	public void setDeleteExpression(String deleteExpression) {
//		this.deleteExpression = deleteExpression;
//	}
//	
//	/**
//	 * @return the deleteMode
//	 */
//	public int getDeleteMode() {
//		return deleteMode;
//	}
//
//	/**
//	 * @param deleteMode the deleteMode to set
//	 */
//	public void setDeleteMode(int deleteMode) {
//		this.deleteMode = deleteMode;
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "SpecificDeletion [definitionLayer=" + definitionLayer
				+ ", instantiationLayer=" + instantiationLayer
				+ ", deletionUntilLayer=" + deletionUntilLayer 
//				+ ", deleteSlotValues="	+ deleteSlotValues 
//				+ ", deleteMode=" + deleteMode
//				+ ", deleteExpression=" + deleteExpression 
				+ "]";
	}
	
}
