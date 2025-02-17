
package org.eclipse.birt.report.designer.internal.ui.views.attributes.provider;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.metadata.IChoice;
import org.eclipse.birt.report.model.api.metadata.IChoiceSet;

public class TextPropertyDescriptorProvider extends PropertyDescriptorProvider implements ITextDescriptorProvider {

	public TextPropertyDescriptorProvider(String property, String element) {
		super(property, element);
	}

	public Object load() {
		String deValue = super.load().toString();
		IChoice choice = null;
		if (ScalarParameterHandle.DATA_TYPE_PROP.equals(getProperty())) {
			IChoiceSet dataType = DesignEngine.getMetaDataDictionary()
					.getChoiceSet(DesignChoiceConstants.CHOICE_PARAM_TYPE);
			choice = dataType.findChoice(deValue);
		} else if (ScalarParameterHandle.CONTROL_TYPE_PROP.equals(getProperty())) {
			IChoiceSet controlType = DesignEngine.getMetaDataDictionary()
					.getChoiceSet(DesignChoiceConstants.CHOICE_PARAM_CONTROL);
			choice = controlType.findChoice(deValue);
		}

		if (choice != null) {
			String displayName = choice.getDisplayName();
			if (displayName != null) {
				deValue = displayName;
			}
		}

		return deValue;
	}

	public void save(Object value) throws SemanticException {
		if (ScalarParameterHandle.CONTROL_TYPE_PROP.equals(getProperty())
				|| ScalarParameterHandle.DATA_TYPE_PROP.equals(getProperty())) {
			return;
		}
		super.save(value);
	}

	public boolean isEditable() {
		if (ModuleHandle.CREATED_BY_PROP.equals(getProperty()) || DataSetHandle.DATA_SOURCE_PROP.equals(getProperty())
				|| ScalarParameterHandle.CONTROL_TYPE_PROP.equals(getProperty())
				|| ScalarParameterHandle.DATA_TYPE_PROP.equals(getProperty())
				|| ReportDesignHandle.ICON_FILE_PROP.equals(getProperty())) {
			return false;
		}
		return !super.isReadOnly();
	}
}
