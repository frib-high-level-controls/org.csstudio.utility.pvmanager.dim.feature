package org.epics.pvmanager.dim.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
				String type = formatList.get(0).getFunctionCode().toUpperCase();
				Optional<Integer> quantity = formatList.get(0).getQuantity();

				if (AdaptorUtil.typesMap.containsKey(type)) {
					if (quantity.isPresent()) {
						if (quantity.get() == 1) {
							value = getString();
							testConnection((String) value);
						} else {
							List<String> array = new ArrayList<String>();
							for (int i = 0; i < quantity.get(); i++) {
								String popString = getString();
								testConnection(popString);
								array.add(popString);
							}
							value = array.toArray();
						}
					} else {
						value = getString();
						testConnection((String) value);
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
						if (quantity.isPresent()) {
							value = (quantity.get() == 1) ? getBoolean(): getBooleanArray();
						} else {
							value = getBoolean();
						}
						vtype = ValueFactory.toVType(value, ValueFactory
								.alarmNone(), ValueFactory.newTime(Timestamp
								.of(getTimestamp())), ValueFactory
								.displayNone());
					}
					if (typeOverride == VByte.class) {
						if (quantity.isPresent()) {
							value = (quantity.get() == 1) ? getByte(): getByteArray();
						} else {
							value = getByte();
						}
						vtype = ValueFactory.toVType(value, ValueFactory
								.alarmNone(), ValueFactory.newTime(Timestamp
								.of(getTimestamp())), ValueFactory
								.displayNone());
					}
				}

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
