package org.epics.pvmanager.dim.adapters;

import org.epics.vtype.VType;

public interface PVManagerDim {

	public void disconnected();
	
	public void connected();
	
	public void message();
	
	public VType getMessage();
}
