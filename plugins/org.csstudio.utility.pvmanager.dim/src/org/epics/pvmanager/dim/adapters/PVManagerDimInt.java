package org.epics.pvmanager.dim.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.epics.pvmanager.dim.AdaptorUtil;
import org.epics.pvmanager.dim.FormatType;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import com.google.common.primitives.Ints;

import dim.DimInfo;

public class PVManagerDimInt extends DimInfo implements PVManagerDim {
	List<FormatType> formatList = new ArrayList<FormatType>();
	VType message = null;

	public PVManagerDimInt(String name, List<FormatType> formatList) {
		super(name, (int) -99999999);
		this.formatList = formatList;
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
							value = getInt();
							testConnection((int) value);
						} else {
							List<Integer> array = new ArrayList<Integer>();
							for (int i = 0; i < quantity.get(); i++) {
								int popInt = getInt();
								testConnection(popInt);
								array.add(popInt);
							}
							value = Ints.toArray(array);
						}
					} else {
						value = getInt();
						testConnection((int) value);
					}
					
					vtype = ValueFactory.toVType(value,
							ValueFactory.alarmNone(),
							ValueFactory.newTime(Timestamp.of(getTimestamp())),
							ValueFactory.displayNone());
				}
			} else if (formatList.size() > 1) {
				

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
	
	private void testConnection(int value){
		if (value ==-99999999) {
			disconnected();
			return;
		} else {
			connected();
		}
	}
}
