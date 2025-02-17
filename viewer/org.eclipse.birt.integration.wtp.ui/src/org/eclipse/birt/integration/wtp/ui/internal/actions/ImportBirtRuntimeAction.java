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

package org.eclipse.birt.integration.wtp.ui.internal.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.integration.wtp.ui.internal.dialogs.BirtConfigurationDialog;
import org.eclipse.birt.integration.wtp.ui.internal.resource.BirtWTPMessages;
import org.eclipse.birt.integration.wtp.ui.internal.util.Logger;
import org.eclipse.birt.integration.wtp.ui.internal.webapplication.WebAppBean;
import org.eclipse.birt.integration.wtp.ui.internal.wizards.BirtWizardUtil;
import org.eclipse.birt.integration.wtp.ui.internal.wizards.IBirtWizardConstants;
import org.eclipse.birt.integration.wtp.ui.internal.wizards.SimpleImportOverwriteQuery;
import org.eclipse.birt.integration.wtp.ui.project.facet.BirtFacetUtilFactory;
import org.eclipse.birt.integration.wtp.ui.project.facet.IBirtFacetUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;

/**
 * Import BIRT runtime component into a J2EE Dynamic Web Project
 * 
 */
public class ImportBirtRuntimeAction extends Action implements IWorkbenchWindowActionDelegate, IBirtWizardConstants {

	private IStructuredSelection fSelection = null;
	private IProject project = null;

	/**
	 * Birt deployment settings
	 */
	private Map properties;

	/**
	 * default constructor
	 */
	public ImportBirtRuntimeAction() {
		super();
		this.properties = new HashMap();
	}

	/**
	 * Initialize
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * Invoke selectionChanged event
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		boolean bEnable = false;
		if (selection instanceof IStructuredSelection) {
			fSelection = (IStructuredSelection) selection;
			bEnable = validateSelected(fSelection);
		}
		((Action) action).setEnabled(bEnable);
	}

	/**
	 * check if it is a J2EE Dynamic Web Project
	 */
	protected boolean isValidProject(IProject fProject) {
		return JavaEEProjectUtilities.isDynamicWebProject(fProject);
	}

	/**
	 * Invoke selectionChanged method to check selected project.
	 */
	protected boolean validateSelected(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return false;

		fSelection = (IStructuredSelection) selection;

		// if IJavaProject
		Object selectedProject = fSelection.getFirstElement();
		if (selectedProject instanceof IJavaProject)
			selectedProject = ((IJavaProject) selectedProject).getProject();

		// Not a project, return false
		if (!(selectedProject instanceof IProject))
			return false;

		project = (IProject) selectedProject;
		return isValidProject(project);
	}

	/**
	 * Action dispose implemention
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// Default
	}

	/**
	 * Execute Action
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			// initialize webapp settings from Extension
			BirtWizardUtil.initWebapp(this.properties);

			IModelProvider modelProvider = ModelProviderManager.getModelProvider(project);
			final IBirtFacetUtil util = BirtFacetUtilFactory.getInstance(modelProvider.getModelObject());

			// initialize webapp settings from existed web.xml
			util.initializeWebapp(this.properties, project);

			IWorkbenchWindow window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
			BirtConfigurationDialog dialog = new BirtConfigurationDialog(window.getShell(),
					(Map) properties.get(EXT_CONTEXT_PARAM));
			dialog.open();
			if (dialog.getReturnCode() == Window.CANCEL)
				return;

			// import birt runtime component
			doImport(window, dialog.isClear());
		} catch (Exception e) {
			Logger.logException(e);
		}
	}

	/**
	 * handle action to clear some old birt runtime files
	 * 
	 * @param webContentPath
	 * @param monitor
	 * @throws Exception
	 */
	protected void doClearAction(IPath webContentPath, IProgressMonitor monitor) throws Exception {
		// remove the root folder
		IPath webPath = webContentPath;
		if (webPath.segmentCount() > 0)
			webPath = webPath.removeFirstSegments(1);

		// get conflict resources
		Map map = BirtWizardUtil.initConflictResources(null);

		// clear
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String folder = (String) entry.getKey();
			if (folder == null)
				continue;

			// get the target folder
			IPath path = webPath.append(folder);
			IFolder tempFolder = project.getFolder(path);
			if (tempFolder == null || !tempFolder.exists())
				continue;

			List files = (List) entry.getValue();
			if (files == null || files.size() <= 0) {
				// delete the whole folder
				tempFolder.delete(true, monitor);
			} else {
				// delete the defined files
				tempFolder.accept(new LibResourceVisitor(monitor, files), IResource.DEPTH_INFINITE, false);
			}
		}
	}

	/**
	 * action to import birt runtime component
	 * 
	 * @param window
	 * @param isClear
	 * @throws Exception
	 */
	protected void doImport(IWorkbenchWindow window, boolean isClear) throws Exception {
		ProgressMonitorDialog monitor = null;
		try {
			// web content folder
			IPath webContentPath = BirtWizardUtil.getWebContentPath(project);

			// do import birt runtime
			monitor = new ProgressMonitorDialog(window.getShell());
			monitor.open();

			// check whether clears the old birt runtime files
			if (isClear)
				doClearAction(webContentPath, monitor.getProgressMonitor());

			// import birt runtime component
			BirtWizardUtil.doImports(project, null, webContentPath, monitor.getProgressMonitor(),
					new ImportOverwriteQuery(monitor.getShell()));

			// process defined folders
			BirtWizardUtil.processCheckFolder(properties, project, webContentPath.toFile().getName(),
					monitor.getProgressMonitor());

			// configurate web.xml
			processConfiguration(monitor.getProgressMonitor(), monitor.getShell());
		} finally {
			// closs dialog
			if (monitor != null) {
				monitor.close();
			}
		}
	}

	/**
	 * Process BIRT deployment configuration.
	 * <p>
	 * Save user-defined settings into web.xml file.
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	protected void processConfiguration(final IProgressMonitor monitor, Shell shell) throws CoreException {
		final SimpleImportOverwriteQuery query = new SimpleImportOverwriteQuery();
		IModelProvider modelProvider = ModelProviderManager.getModelProvider(project);
		IPath modelPath = new Path("WEB-INF").append("web.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		boolean exists = project.getProjectRelativePath().append(modelPath).toFile().exists();
		if (BirtFacetUtilFactory.isWebApp25(modelProvider.getModelObject()) && !exists) {
			modelPath = IModelProvider.FORCESAVE;
		}

		final IBirtFacetUtil util = BirtFacetUtilFactory.getInstance(modelProvider.getModelObject());
		modelProvider.modify(new Runnable() {
			public void run() {
				util.configureWebApp((WebAppBean) properties.get(EXT_WEBAPP), project, query, monitor);
				util.configureContextParam((Map) properties.get(EXT_CONTEXT_PARAM), project, query, monitor);
				util.configureListener((Map) properties.get(EXT_LISTENER), project, query, monitor);

				util.configureServlet((Map) properties.get(EXT_SERVLET), project, query, monitor);

				util.configureServletMapping((Map) properties.get(EXT_SERVLET_MAPPING), project, query, monitor);

				util.configureFilter((Map) properties.get(EXT_FILTER), project, query, monitor);

				util.configureFilterMapping((Map) properties.get(EXT_FILTER_MAPPING), project, query, monitor);

				util.configureTaglib((Map) properties.get(EXT_TAGLIB), project, query, monitor);
			}
		}, modelPath);
	}

	/**
	 * Implement IResourceVisitor to clear the old birt runtime jar files under lib
	 * folder.
	 * 
	 */
	private static class LibResourceVisitor implements IResourceVisitor {

		// progress monitor
		private IProgressMonitor monitor;

		// file list
		private List files;

		/**
		 * default constructor
		 * 
		 * @param monitor
		 */
		public LibResourceVisitor(IProgressMonitor monitor, List files) {
			this.monitor = monitor;
			this.files = files;
		}

		/**
		 * handle the resources.
		 * 
		 * @param resource
		 * @exception CoreException
		 */
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (file == null || files == null)
					return true;

				Iterator it = files.iterator();
				while (it.hasNext()) {
					String name = (String) it.next();
					if (name != null && file.getName().startsWith(name)) {
						file.delete(true, monitor);
						break;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Implement IOverwriteQuery for importing process
	 * 
	 */
	private static class ImportOverwriteQuery implements IOverwriteQuery {

		// if all
		private boolean isALL = false;

		private Shell shell;

		/**
		 * default constructor
		 * 
		 * @param shell
		 */
		public ImportOverwriteQuery(Shell shell) {
			this.shell = shell;
		}

		/**
		 * Open confirm dialog
		 * 
		 * @param file
		 * @return
		 */
		private int openDialog(final String file) {
			final int[] result = { IDialogConstants.CANCEL_ID };
			shell.getDisplay().syncExec(new Runnable() {

				public void run() {
					String title = BirtWTPMessages.BIRTOverwriteQuery_title;
					String msg = NLS.bind(BirtWTPMessages.BIRTOverwriteQuery_message, file);
					String[] options = { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
							IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.CANCEL_LABEL };
					MessageDialog dialog = new MessageDialog(shell, title, null, msg, MessageDialog.QUESTION, options,
							0);
					result[0] = dialog.open();
				}
			});
			return result[0];
		}

		/**
		 * wait to query overwrite result. If has selected ALL, always return ALL.
		 */
		public String queryOverwrite(String file) {
			if (isALL)
				return ALL;

			String[] returnCodes = { YES, NO, ALL, CANCEL };
			int returnVal = openDialog(file);
			String result = returnVal < 0 ? CANCEL : returnCodes[returnVal];

			// check if selected ALL
			isALL = result.equalsIgnoreCase(ALL) ? true : false;

			return result;
		}
	}
}
