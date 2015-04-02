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
		map.put("S", new FormatType("S", short.class, "getShort", "getShortArray"));
		map.put("I", new FormatType("I", int.class, "getInt", "getIntArray"));
		map.put("D", new FormatType("D", double.class, "getDouble", "getDoubleArray"));
		map.put("F", new FormatType("F", float.class, "getFloat", "getFloatArray"));
		map.put("X", new FormatType("X", long.class, "getLong", "getLongArray"));
		map.put("L", new FormatType("L", long.class, "getLong", "getLongArray"));
		map.put("C", new FormatType("C", String.class, "getString", "getStringArray"));
		typesMap = Collections.unmodifiableMap(map);
	}

}
