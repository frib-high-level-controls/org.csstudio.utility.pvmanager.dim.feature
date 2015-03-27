package org.epics.pvmanager.dim;

public class FormatType {
	Class<?> classType;
	String function;
	String arrayFunction;

	public FormatType(Class<?> classType, String function,
			String arrayFunction) {
		this.classType = classType;
		this.function = function;
		this.arrayFunction = arrayFunction;
	}
}
