/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.views.attributes.widget;

import org.eclipse.birt.report.designer.internal.ui.dialogs.FormatNumberLayoutPeer;

/**
 * Format number page for formatting numbers.
 */

public class FormatNumberDescriptor extends FormatDescriptor {

	/**
	 * Constructs a page for formatting numbers, default aligns the page vertically.
	 * 
	 * @param parent The container
	 * @param style  The style of the page
	 */
	public FormatNumberDescriptor() {
		this(PAGE_ALIGN_VIRTICAL, true);
	}

	/**
	 * Constructs a page for formatting numbers.
	 * 
	 * @param parent        The container
	 * @param style         The style of the page
	 * @param pageAlignment Aligns the page vertically(PAGE_ALIGN_VIRTICAL) or
	 *                      horizontally(PAGE_ALIGN_HORIZONTAL).
	 */
	public FormatNumberDescriptor(int pageAlignment, boolean isFormStyle) {
		this(pageAlignment, isFormStyle, true);
	}

	/**
	 * Constructs a page for formatting numbers.
	 * 
	 * @param parent        The container
	 * @param style         The style of the page
	 * @param pageAlignment Aligns the page vertically(PAGE_ALIGN_VIRTICAL) or
	 *                      horizontally(PAGE_ALIGN_HORIZONTAL).
	 * @param showLocale    whether the UI to set locale will be shown
	 */
	public FormatNumberDescriptor(int pageAlignment, boolean isFormStyle, boolean showLocale) {
		setFormStyle(isFormStyle);

		layoutPeer = new FormatNumberLayoutPeer(pageAlignment, isFormStyle, showLocale);
	}
}