package org.epics.pvmanager.dim;

public class FormatType {
	Class<?> classType;
	String function;
	String arrayFunction;
	Integer quantity;
	String functionCode;

	public FormatType(String functionCode, Class<?> classType, String function,
			String arrayFunction) {
		this.functionCode = functionCode;
		this.classType = classType;
		this.function = function;
		this.arrayFunction = arrayFunction;
	}

	public FormatType(String functionCode, Integer quantity) {
		this.functionCode = functionCode;
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((functionCode == null) ? 0 : functionCode.hashCode());
		result = prime * result
				+ ((quantity == null) ? 0 : quantity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormatType other = (FormatType) obj;
		if (functionCode == null) {
			if (other.functionCode != null)
				return false;
		} else if (!functionCode.equals(other.functionCode))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		return true;
	}

	
	
}
