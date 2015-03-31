package org.epics.pvmanager.dim;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VByte;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import dim.DimBrowser;
import dim.DimInfo;
import dim.DimClient;
import dim.DimExitHandler;
import dim.DimErrorHandler;

public class DimChannelHandler extends MultiplexedChannelHandler<DimChannelHandler, Object> {

	final String channelName;
	final Class CType;
	List<FormatType> formatList = new ArrayList<FormatType>();
	final boolean writeTrue;
	DimInfo dimInfo;

	public DimChannelHandler(String channelName) {
		super(channelName);
		
		if(channelName.endsWith("<VBoolean>")){
			this.channelName = channelName.split("<")[0];
			this.CType = VBoolean.class;
		} else if (channelName.endsWith("<VByte>")) {
			this.channelName = channelName.split("<")[0];
			this.CType = VByte.class;
		} else {
			this.channelName = channelName;
			this.CType = null;
		}
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
			if (this.writeTrue) {
				processMessage(ValueFactory.newVString("",
						ValueFactory.alarmNone(), ValueFactory.timeNow()));
			} else {
				if (formatList.size()==1){
					String functionCode = formatList.get(0).functionCode;
					switch(functionCode){
					case "I":
					case "S":
						dimInfo = new PVManagerDim(channelName, (int)-99999999);
						break;
					case "F":
					case "X":
						dimInfo = new PVManagerDim(channelName, (float)-99999999);
						break;
					case "D":
						dimInfo = new PVManagerDim(channelName, (double)-99999999);
						break;
					case "C":
						dimInfo = new PVManagerDim(channelName, "-99999999");
						break;
					default:
						dimInfo = new PVManagerDim(channelName, "-99999999");
					}		
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
		} else if (newValue instanceof Integer) {
			readback = DimClient.sendCommand(channelName,
					((Integer) newValue).intValue());
		} else if (newValue instanceof Double) {
			readback = DimClient.sendCommand(channelName,
					((Double) newValue).doubleValue());
		} else if (newValue instanceof Float) {
			readback = DimClient.sendCommand(channelName,
					((Float) newValue).floatValue());
		} else if (newValue instanceof int[]) {
			readback = DimClient.sendCommand(channelName, (int[]) newValue);
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
			processMessage(ValueFactory.newVString("",
					ValueFactory.alarmNone(), ValueFactory.timeNow()));
		}
	}

	class PVManagerDim extends DimInfo {

		PVManagerDim(String name, String noLink) {
			super(name, noLink);
		}

		PVManagerDim(String name, int noLink) {
			super(name, noLink);
		}

		PVManagerDim(String name, double noLink) {
			super(name, noLink);
		}

		PVManagerDim(String name, float noLink) {
			super(name, noLink);
		}

		public void infoHandler() {
			String name = getName();
			VType vtype = null;

			// Not covering
			// byte dByte = getByte();
			// byte[] dByteArray = getByteArray();

			if (getInt() == -99999999 || getDouble() == -99999999 || getFloat() == -99999999 || getString() == "-99999999" ) {
				processConnection(null);
			} else {
				processConnection(DimChannelHandler.this);
			}

			Object value = null;
			if (!formatList.isEmpty()) {
				if (formatList.size() == 1) {
					String type = formatList.get(0).functionCode;
					Integer quantity = formatList.get(0).quantity;

					if (AdaptorUtil.typesMap.containsKey(type)) {

						try {
							Method method = this.getClass().getMethod(
									AdaptorUtil.typesMap.get(type).function);
							Method arrayMethod = this.getClass().getMethod(
									AdaptorUtil.typesMap.get(type).arrayFunction);
							value = ((quantity == 1) ? method.invoke(this)
									: arrayMethod.invoke(this));
						} catch (NoSuchMethodException | SecurityException
								| IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							value = null;
						}
						vtype = ValueFactory.toVType(value, ValueFactory
								.alarmNone(), ValueFactory.newTime(Timestamp
								.of(getTimestamp())), ValueFactory
								.displayNone());
					}
					if (type.matches("C")) {
						if (((String) value).contains("|")) {
							String[] rows = ((String) value).split("\n");
							List<String[]> rowscells = new ArrayList<String[]>();
							List<String> names = new ArrayList<String>();
							List<Object> columns = new ArrayList<Object>();
							List<Class<?>> types = new ArrayList<Class<?>>();
							int maxCol = 0;

							for (int i = 0; rows.length > i; i++) {
								String[] cells = rows[i].split("\\|");
								maxCol = cells.length>maxCol?cells.length:maxCol;
								rowscells.add(cells);
							}
							for (int k = 0; maxCol > k; k++) {
								List<String> column = new ArrayList<String>(Collections.nCopies(rows.length, ""));
								for (int j=0; rowscells.size()>j;j++){
									if (rowscells.get(j).length<maxCol){
										String[] rowcells = Arrays.copyOf(rowscells.get(j), maxCol);
										column.set(j, rowcells[k]);
									}else{
										column.set(j, rowscells.get(j)[k]);
									}
								}
								columns.add(column);
								types.add(String.class);
								names.add(String.valueOf(k));

							}

							vtype = ValueFactory.newVTable(types, names,
									columns);

						}
						 if (DimChannelHandler.this.CType==VBoolean.class) {
							 value = (quantity == 1) ? getBoolean() : getBooleanArray();
							 vtype = ValueFactory.toVType(value, ValueFactory.alarmNone(), ValueFactory.newTime(Timestamp.of(getTimestamp())),ValueFactory.displayNone());
						 }
						 if (DimChannelHandler.this.CType==VByte.class) {
							 value = (quantity == 1) ? getByte() : getByteArray();
							 vtype = ValueFactory.toVType(value, ValueFactory.alarmNone(), ValueFactory.newTime(Timestamp.of(getTimestamp())),ValueFactory.displayNone());
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
				processMessage(vtype);

			}
		}
	}

	
}
