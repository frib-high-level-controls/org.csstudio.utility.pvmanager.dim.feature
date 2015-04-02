package org.epics.pvmanager.dim.adapters;

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

public class PVManagerDimString extends DimInfo implements PVManagerDim {
	List<FormatType> formatList = new ArrayList<FormatType>();
	Class typeOverride;
	VType message = null;

	public PVManagerDimString(String name, List<FormatType> formatList, Class typeOverride) {
		super(name, "DISCONNECTED");
		this.formatList = formatList;
		this.typeOverride = typeOverride;
	}

	public void infoHandler() {
		String name = getName();
		VType vtype = null;

		Object value = null;
		if (!formatList.isEmpty()) {
			if (formatList.size() == 1) {
				String type = formatList.get(0).getFunctionCode();
				Integer quantity = formatList.get(0).getQuantity();

				if (AdaptorUtil.typesMap.containsKey(type)) {
					if (quantity == 1){ 
						value = getString();
						testConnection((String)value);
					} else{
						List<String> array = new ArrayList<String>();
						for(int i=0;i<quantity;i++){
							String popString = getString();
							testConnection(popString);
							array.add(popString);
						}
						value = array.toArray();
					}
					
					vtype = ValueFactory.toVType(value,
							ValueFactory.alarmNone(),
							ValueFactory.newTime(Timestamp.of(getTimestamp())),
							ValueFactory.displayNone());
				}
				if (type.matches("C")) {
					if (value instanceof String[]) {
						value = ((String[]) value).toString();
					} 
					if (((String) value).contains("|")) {
						String[] rows = ((String) value).split("\n");
						List<String[]> rowscells = new ArrayList<String[]>();
						List<String> names = new ArrayList<String>();
						List<Object> columns = new ArrayList<Object>();
						List<Class<?>> types = new ArrayList<Class<?>>();
						int maxCol = 0;

						for (int i = 0; rows.length > i; i++) {
							String[] cells = rows[i].split("\\|");
							maxCol = cells.length > maxCol ? cells.length
									: maxCol;
							rowscells.add(cells);
						}
						for (int k = 0; maxCol > k; k++) {
							List<String> column = new ArrayList<String>(
									Collections.nCopies(rows.length, ""));
							for (int j = 0; rowscells.size() > j; j++) {
								if (rowscells.get(j).length < maxCol) {
									String[] rowcells = Arrays.copyOf(
											rowscells.get(j), maxCol);
									column.set(j, rowcells[k]);
								} else {
									column.set(j, rowscells.get(j)[k]);
								}
							}
							columns.add(column);
							types.add(String.class);
							names.add(String.valueOf(k));

						}

						vtype = ValueFactory.newVTable(types, names, columns);

					}
					if (typeOverride == VBoolean.class) {
						value = (quantity == 1) ? getBoolean()
								: getBooleanArray();
						vtype = ValueFactory.toVType(value, ValueFactory
								.alarmNone(), ValueFactory.newTime(Timestamp
								.of(getTimestamp())), ValueFactory
								.displayNone());
					}
					if (typeOverride == VByte.class) {
						value = (quantity == 1) ? getByte() : getByteArray();
						vtype = ValueFactory.toVType(value, ValueFactory
								.alarmNone(), ValueFactory.newTime(Timestamp
								.of(getTimestamp())), ValueFactory
								.displayNone());
					}
				}

			} else if (formatList.size() > 1) {
				// List<Class<?>> types = new ArrayList<Class<?>>();
				// List<String> names = new ArrayList<String>();
				// List<Object> values = new ArrayList<Object>();
				// for (String item:format){
				// String[] typeWithQuanity = item.split(":");
				// String type = typeWithQuanity[0];
				// int quantity = 1;
				// if (typeWithQuanity.length>1){
				// quantity = Integer.valueOf(typeWithQuanity[1]);
				// }
				// if(AdaptorUtil.typesMap.containsKey(type)){
				// types.add(typesMap.get(type).classType);
				// names.add(type);
				// try {
				// Method method =
				// this.getClass().getMethod(AdaptorUtil.typesMap.get(type).function);
				// Method arrayMethod =
				// this.getClass().getMethod(AdaptorUtil.typesMap.get(type).arrayFunction);
				// values.add((quantity ==
				// 1)?method.invoke(this):arrayMethod.invoke(this));
				// } catch (NoSuchMethodException | SecurityException |
				// IllegalAccessException | IllegalArgumentException |
				// InvocationTargetException e) {
				// values.add(null);
				// }
				// } else if (type.equals("C")){
				// // check string, boolean crap
				// } else {
				// continue;
				// }
				//
				//
				//
				// }
				//
				// vtype = ValueFactory.newVTable(types, names, values);

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
	
	private void testConnection(String value){
		if (value.equals("DISCONNECTED")) {
			disconnected();
			return;
		} else {
			connected();
		}
	}
}
