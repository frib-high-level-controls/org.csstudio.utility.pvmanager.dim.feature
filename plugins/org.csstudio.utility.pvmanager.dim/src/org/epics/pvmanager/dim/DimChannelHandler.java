package org.epics.pvmanager.dim;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.dim.adapters.PVManagerDimDouble;
import org.epics.pvmanager.dim.adapters.PVManagerDimFloat;
import org.epics.pvmanager.dim.adapters.PVManagerDimInt;
import org.epics.pvmanager.dim.adapters.PVManagerDimLong;
import org.epics.pvmanager.dim.adapters.PVManagerDimShort;
import org.epics.pvmanager.dim.adapters.PVManagerDimString;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VByte;
import org.epics.vtype.VDouble;
import org.epics.vtype.VFloat;
import org.epics.vtype.VInt;
import org.epics.vtype.VLong;
import org.epics.vtype.VShort;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import dim.DimBrowser;
import dim.DimInfo;
import dim.DimClient;
import dim.DimExitHandler;
import dim.DimErrorHandler;

public class DimChannelHandler extends MultiplexedChannelHandler<DimChannelHandler, Object> {

	final String channelName;
	final Class typeOverride;
	List<FormatType> formatList = new ArrayList<FormatType>();
	final boolean writeTrue;
	DimInfo dimInfo;

	public DimChannelHandler(String channelName) {
		super(channelName);
		this.channelName = channelName.split("<")[0];
		String[] srvcs = DimBrowser.getServices(this.channelName);
		String formatString = DimBrowser.getFormat(this.channelName);
		
		String[] format = formatString.split(";");
		for(String formatItem:format) {
			String[] typeWithQuanity = formatItem.split(":");
			String functionCode = typeWithQuanity[0];
			Integer quantity = 1;
			if (typeWithQuanity.length > 1) {
				quantity = Integer.valueOf(typeWithQuanity[1]);
			}
			formatList.add(new FormatType(functionCode,quantity));
		}
		
		if(channelName.endsWith("<VBoolean>")){
			this.typeOverride = VBoolean.class;
			formatList.get(0).functionCode = "C";
		} else if (channelName.endsWith("<VByte>")) {
			this.typeOverride = VByte.class;
			formatList.get(0).functionCode = "C";
		} else if (channelName.endsWith("<VString>")) {
			this.typeOverride = VString.class;
			formatList.get(0).functionCode = "C";
		} else if (channelName.endsWith("<VInt>")) {
			this.typeOverride = VInt.class;
			formatList.set(0, new FormatType("I",1));
		} else if (channelName.endsWith("<VShort>")) {
			this.typeOverride = VShort.class;
			formatList.get(0).functionCode = "S";
		} else if (channelName.endsWith("<VDouble>")) {
			this.typeOverride = VDouble.class;
			formatList.get(0).functionCode = "D";
		} else if (channelName.endsWith("<VFloat>")) {
			this.typeOverride = VFloat.class;
			formatList.get(0).functionCode = "F";
		} else if (channelName.endsWith("<VLong>")) {
			this.typeOverride = VLong.class;
			formatList.get(0).functionCode = "L";
		} else {
			this.typeOverride = null;
		}
		
		if (srvcs.length == 1) {
			this.writeTrue = DimBrowser.isCommand(srvcs[0]);
		} else {
			this.writeTrue = false;
		}
	}

	@Override
	protected void connect() {
		try {

			// Write channels return -1,0,1 : disconnected, not delivered,
			// success
			if (formatList.size() == 1) {
				String functionCode = formatList.get(0).functionCode;
				switch (functionCode) {
				case "S":
					dimInfo = new PVManagerDimShort(channelName, formatList) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
					break;
				case "I":
					dimInfo = new PVManagerDimInt(channelName, formatList) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
					break;
				case "L":
					dimInfo = new PVManagerDimLong(channelName, formatList) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
					break;
				case "D":
					dimInfo = new PVManagerDimDouble(channelName, formatList) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
					break;
				case "F":
				case "X":
					dimInfo = new PVManagerDimFloat(channelName, formatList) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
					break;
				case "C":
				default:
					dimInfo = new PVManagerDimString(channelName, formatList, typeOverride) {

						@Override
						public void message() {
							processMessage(getMessage());
						}

						@Override
						public void connected() {
							processConnection(DimChannelHandler.this);
						}

						@Override
						public void disconnected() {
							processConnection(null);
						}
					};
				}

				
				DimExitHandler exid = new DimExitHandler() {
					public void exitHandler(int code) {
						processConnection(null);
						System.out.println("Exit: " + code);
					}
				};

				DimErrorHandler erid = new DimErrorHandler() {
					public void errorHandler(int severity, int code, String msg) {
						if (code == DIMSVCDUPLC)
							System.out.println("Service already declared");
						System.out.println("Error: " + msg + " sev: "
								+ severity);
						processConnection(null);
					}
				};
			}
			processConnection(this);
		} catch (Exception ex) {
			reportExceptionToAllReadersAndWriters(ex);
		}

	}

	@Override
	protected void disconnect() {
		if (dimInfo != null)
			dimInfo.releaseService();
		processConnection(null);
	}

	@Override
	protected boolean isConnected(DimChannelHandler payload) {

		return payload == null ? false : true;
	}

	@Override
	protected boolean isWriteConnected(DimChannelHandler payload) {
		return this.writeTrue;
	}

	@Override
	protected void write(Object newValue, ChannelWriteCallback callback) {
		int readback = 1;
		if (newValue instanceof String) {
			readback = DimClient.sendCommand(channelName, (String) newValue);
		} else if (newValue instanceof Short) {
			readback = DimClient.sendCommand(channelName,((Short) newValue).doubleValue());
		} else if (newValue instanceof Integer) {
			readback = DimClient.sendCommand(channelName,((Integer) newValue).intValue());
		} else if (newValue instanceof Long) {
			readback = DimClient.sendCommand(channelName,((Long) newValue).doubleValue());
		} else if (newValue instanceof Double) {
			readback = DimClient.sendCommand(channelName,((Double) newValue).doubleValue());
		} else if (newValue instanceof Float) {
			readback = DimClient.sendCommand(channelName,((Float) newValue).floatValue());
		} else if (newValue instanceof short[]) {
			//readback = DimClient.sendCommand(channelName, (short[]) newValue);
		} else if (newValue instanceof int[]) {
			readback = DimClient.sendCommand(channelName, (int[]) newValue);
		} else if (newValue instanceof long[]) {
			//	readback = DimClient.sendCommand(channelName, (long[]) newValue);
		} else if (newValue instanceof double[]) {
			readback = DimClient.sendCommand(channelName, (double[]) newValue);
		} else if (newValue instanceof float[]) {
			readback = DimClient.sendCommand(channelName, (float[]) newValue);
		} else {
			throw new RuntimeException("Unsupported type for Dim: "
					+ newValue.getClass());
		}

		if (readback == -1) {
			processConnection(null);
		} else if (readback == 0) {
			processConnection(this);
			processMessage(ValueFactory.newVString("", ValueFactory.newAlarm(
					AlarmSeverity.MAJOR, "Message Not Received"), ValueFactory
					.timeNow()));
		} else if (readback == 1) {
			processConnection(this);
		}
	}
	
}
