/*******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/
package org.eclipse.birt.report.engine.script.internal;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.script.IDataSourceInstanceHandle;
import org.eclipse.birt.data.engine.api.script.IScriptDataSourceEventHandler;
import org.eclipse.birt.report.engine.api.script.eventhandler.IScriptedDataSetEventHandler;
import org.eclipse.birt.report.engine.api.script.eventhandler.IScriptedDataSourceEventHandler;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.script.internal.instance.DataSourceInstance;
import org.eclipse.birt.report.model.api.ModuleUtil;
import org.eclipse.birt.report.model.api.ScriptDataSourceHandle;
import org.eclipse.birt.report.model.elements.interfaces.IScriptDataSourceModel;

public class ScriptDataSourceScriptExecutor extends DataSourceScriptExecutor implements IScriptDataSourceEventHandler {

	private static final String OPEN = "OPEN";

	private static final String CLOSE = "CLOSE";

	private IScriptedDataSourceEventHandler scriptedEventHandler;

	public ScriptDataSourceScriptExecutor(ScriptDataSourceHandle dataSourceHandle, ExecutionContext context)
			throws BirtException {
		super(dataSourceHandle, context);
	}

	protected void initEventHandler() {
		super.initEventHandler();
		if (eventHandler != null) {
			try {
				scriptedEventHandler = (IScriptedDataSourceEventHandler) eventHandler;
			} catch (ClassCastException e) {
				addClassCastException(context, e, dataSourceHandle, IScriptedDataSetEventHandler.class);
			}
		}
	}

	public void handleOpen(IDataSourceInstanceHandle dataSource) {
		initEventHandler();
		try {
			String id = ModuleUtil.getScriptUID(dataSourceHandle.getPropertyHandle(IScriptDataSourceModel.OPEN_METHOD));
			ScriptStatus status = super.handleJS(dataSource.getScriptScope(), dataSource.getName(), OPEN,
					((ScriptDataSourceHandle) dataSourceHandle).getOpen(), id);
			if (status.didRun())
				return;
			if (scriptedEventHandler != null)
				scriptedEventHandler.open(new DataSourceInstance(dataSource));
		} catch (Exception e) {
			addException(context, e);
		}
	}

	public void handleClose(IDataSourceInstanceHandle dataSource) {
		initEventHandler();
		try {
			String id = ModuleUtil
					.getScriptUID(dataSourceHandle.getPropertyHandle(IScriptDataSourceModel.CLOSE_METHOD));
			ScriptStatus status = handleJS(dataSource.getScriptScope(), dataSource.getName(), CLOSE,
					((ScriptDataSourceHandle) dataSourceHandle).getClose(), id);
			if (status.didRun())
				return;
			if (scriptedEventHandler != null)
				scriptedEventHandler.close(new DataSourceInstance(dataSource));
		} catch (Exception e) {
			addException(context, e);
		}
	}
}
