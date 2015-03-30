/**
 * 
 */
package org.csstudio.utility.pvmanager.dim;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;


import org.epics.pvmanager.dim.DimDataSource;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Client to be regestered to the extension point.
 * 
 * @author berryma4
 * 
 */
@SuppressWarnings("deprecation")
public class DimDataSourceFromPreferences extends DimDataSource {

	private static Logger log = Logger
			.getLogger(DimDataSourceFromPreferences.class.getName());
	private static String server;

	/**
	 * 
	 */

	static {
		final IPreferencesService prefs = Platform.getPreferencesService();


		server = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.DNS_SERVER,
				"localhost", null);
	}

	private static DimDataSource builder() {

		 DimDataSource source = new DimDataSource(server);

		return source;
		
	}

	public DimDataSourceFromPreferences() {
		super(builder());
	}

}
