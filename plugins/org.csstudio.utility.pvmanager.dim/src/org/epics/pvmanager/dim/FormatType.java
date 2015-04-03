package org.epics.pvmanager.dim;

import java.util.Optional;

public class FormatType {
	Class<?> classType;
	String function;
	String arrayFunction;
	Optional<Integer> quantity;
    String functionCode;

	public FormatType(String functionCode, Class<?> classType, String function,
			String arrayFunction) {
		this.functionCode = functionCode;
		this.classType = classType;
		this.function = function;
		this.arrayFunction = arrayFunction;
	}

	public FormatType(String functionCode, Optional<Integer> quantity) {
		this.functionCode = functionCode;
		this.quantity = quantity;
	}
	
	

	public Class<?> getClassType() {
		return classType;
	}

	public void setClassType(Class<?> classType) {
		this.classType = classType;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getArrayFunction() {
		return arrayFunction;
	}

	public void setArrayFunction(String arrayFunction) {
		this.arrayFunction = arrayFunction;
	}

	public Optional<Integer> getQuantity() {
		return quantity;
	}

	public void setQuantity(Optional<Integer> quantity) {
		this.quantity = quantity;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
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
