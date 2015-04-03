package org.epics.pvmanager.dim.adapters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.epics.pvmanager.dim.AdaptorUtil;
import org.epics.pvmanager.dim.FormatType;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VByte;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import dim.DimInfo;

public class PVManagerDimMixedFormat extends DimInfo implements PVManagerDim {
	List<FormatType> formatList = new ArrayList<FormatType>();
	VType message = null;

	public PVManagerDimMixedFormat(String name, List<FormatType> formatList) {
		super(name, "DISCONNECTED");
		this.formatList = formatList;
	}

	public void infoHandler() {
		String name = getName();
		VType vtype = null;

		Object value = null;
		if (!formatList.isEmpty()) {
			if (formatList.size() > 1) {
				List<Class<?>> types = new ArrayList<Class<?>>();
				List<String> names = new ArrayList<String>();
				List<Object> values = new ArrayList<Object>();
				for (FormatType format : formatList) {
					String functionCode = format.getFunctionCode().toUpperCase();
					if (AdaptorUtil.typesMap.containsKey(functionCode)) {
						//types.add(AdaptorUtil.typesMap.get(functionCode).getClassType());
						types.add(String.class);
						names.add(functionCode);
						try {
							Method method = this.getClass().getMethod(
									AdaptorUtil.typesMap.get(functionCode).getFunction());
							List<Object> array = new ArrayList<Object>();
							if (format.getQuantity().isPresent()) {
								if (format.getQuantity().get() == 1) {
									value = method.invoke(this);
									testConnection(value);
									array.add(value.toString());
								} else {
									for (int i = 0; i < format.getQuantity().get(); i++) {
										Object pop = method.invoke(this);
										testConnection(pop);
										array.add(pop.toString());
									}
								}
							} else {
								value = method.invoke(this);
								testConnection(value);
								array.add(value.toString());
							}
							values.add(array);
						} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							values.add("");
						}
					} else {
						continue;
					}

				}

				vtype = ValueFactory.newVTable(types, names, values);

			}
			message = vtype;
			message();

		}
	}

	@Override
	public void disconnected() {

	}

	@Override
	public void connected() {

	}

	@Override
	public void message() {

	}

	@Override
	public VType getMessage() {
		return message;
	}
	
	private void testConnection(Object value) {
		if (value instanceof String) {
			if (value.equals("DISCONNECTED")) {
				disconnected();
				return;
			} else {
				connected();
			}
		} else {
			connected();
		}
	}
}
