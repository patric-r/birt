/***********************************************************************
 * Copyright (c) 2009 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.nLayout.area;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.birt.report.engine.content.IHyperlinkAction;

public interface IImageArea extends IArea {

	String getImageUrl();

	byte[] getImageData();

	String getHelpText();

	String getExtension();

	String getMIMEType();

	HashMap<String, String> getParameters();

	void addImageMap(int[] peak, IHyperlinkAction action);

	ArrayList<IImageMap> getImageMapDescription();

	interface IImageMap {
		public int[] getVertices();

		public IHyperlinkAction getAction();
	}
}
