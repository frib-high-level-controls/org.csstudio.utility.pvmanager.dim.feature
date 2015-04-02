package org.epics.pvmanager.dim.adapters;

import java.util.ArrayList;
import java.util.List;

import org.epics.pvmanager.dim.AdaptorUtil;
import org.epics.pvmanager.dim.FormatType;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import com.google.common.primitives.Floats;

import dim.DimInfo;

public class PVManagerDimFloat extends DimInfo implements PVManagerDim {
	List<FormatType> formatList = new ArrayList<FormatType>();
	VType message = null;

	public PVManagerDimFloat(String name, List<FormatType> formatList) {
		super(name, (float) -99999999);
		this.formatList = formatList;
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
						value = getFloat();
						testConnection((float)value);
					} else{
						List<Float> array = new ArrayList<Float>();
						for(int i=0;i<quantity;i++){
							float popFloat = getFloat();
							testConnection(popFloat);
							array.add(popFloat);
						}
						value = Floats.toArray(array);
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
	
	private void testConnection(float value){
		if (value ==-99999999) {
			disconnected();
			return;
		} else {
			connected();
		}
	}
}
