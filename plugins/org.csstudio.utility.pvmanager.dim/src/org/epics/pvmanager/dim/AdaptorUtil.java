package org.epics.pvmanager.dim;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AdaptorUtil {
	private AdaptorUtil() {

	}

	public static final Map<String, FormatType> typesMap;
	static {
		Map<String, FormatType> map = new HashMap<String, FormatType>();
		map.put("S", new FormatType(short.class, "getShort", "getShortArray"));
		map.put("I", new FormatType(int.class, "getInt", "getIntArray"));
		map.put("F", new FormatType(float.class, "getFloat", "getFloatArray"));
		map.put("D",
				new FormatType(double.class, "getDouble", "getDoubleArray"));
		map.put("X", new FormatType(long.class, "getLong", "getLongArray"));
		map.put("L", new FormatType(long.class, "getLong", "getLongArray"));
		map.put("C",
				new FormatType(String.class, "getString", "getStringArray"));
		typesMap = Collections.unmodifiableMap(map);
	}

}
