/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.computation.withoutaxes;

/**
 * Coordinates
 */
public final class Coordinates {

	private final int iRow;

	private final int iColumn;

	/**
	 * The constructor.
	 * 
	 * @param iColumn
	 * @param iRow
	 */
	Coordinates(int iColumn, int iRow) {
		this.iColumn = iColumn;
		this.iRow = iRow;
	}

	/**
	 * @return Returns the column.
	 */
	public final int getColumn() {
		return iColumn;
	}

	/**
	 * @return Returns the row.
	 */
	public final int getRow() {
		return iRow;
	}
}
