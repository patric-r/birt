/*************************************************************************************
 * Copyright (c) 2004 Actuate Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Actuate Corporation - Initial implementation.
 ************************************************************************************/

package org.eclipse.birt.report.designer.ui.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * IReportEditorPage is the base interface for a report editor page contribute
 * to BIRT report editors.
 */
public interface IReportEditorPage extends IFormPage {

	/**
	 * invoke on page was brought to report editor top.
	 * 
	 * @param prePage the last top page.
	 * @return
	 */
	public boolean onBroughtToTop(IReportEditorPage prePage);

	/**
	 * Set the page stale type.
	 * 
	 * @param type
	 */
	public void markPageStale(int type);

	/**
	 * Get the page stale type.
	 * 
	 * @return
	 */
	public int getStaleType();

	/**
	 * Set page input.
	 * 
	 * @param input
	 */
	public void setInput(IEditorInput input);
}
