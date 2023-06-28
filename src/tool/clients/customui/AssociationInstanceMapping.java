package tool.clients.customui;

import java.util.Objects;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AssociationInstanceMapping {
	private final String associationName;
	private final String relInstanceName;
	
	private int hashcode;
	
	public AssociationInstanceMapping(FmmlxAssociation association, FmmlxObject relInstance) {
		this.associationName = association.getName();
		this.relInstanceName = relInstance.getName();
		this.hashcode = Objects.hash(association, relInstance);
	}
	
	public String getAssoc() {
		return this.associationName;
	}
	
	public String getInstanceName() {
		return this.relInstanceName;
	}
	
	@Override
	public boolean equals(Object o) {
		if( this == o ) {
			return true;
		}
		if( o == null || getClass() != o.getClass()) {
			return false;
		}
		AssociationInstanceMapping that = (AssociationInstanceMapping) o;
		return that.associationName == this.associationName && that.relInstanceName == this.relInstanceName;
	}
	
	@Override
	public int hashCode() {
		return this.hashcode;
	}
}