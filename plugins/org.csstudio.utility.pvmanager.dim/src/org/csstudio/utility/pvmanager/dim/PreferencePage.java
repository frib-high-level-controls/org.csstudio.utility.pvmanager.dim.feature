package org.csstudio.utility.pvmanager.dim;


import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor dimDNS;


	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
				org.csstudio.utility.pvmanager.dim.Activator.PLUGIN_ID));
		setMessage("DIM Client Preferences");
		setDescription("DIM preference page");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		dimDNS = new StringFieldEditor(PreferenceConstants.DNS_SERVER,
				"DIM DNS Address:", getFieldEditorParent());
		dimDNS.setEmptyStringAllowed(false);
		addField(dimDNS);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

}
