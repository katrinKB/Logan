/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.iteratec.logan.actions;

import java.io.File;

import de.iteratec.logan.editor.CustomTextEditor;
import de.iteratec.logan.service.IOUtils;
import de.iteratec.logan.utils.ProjectUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.FileEditorInput;


public class OpenFileAction extends Action implements IWorkbenchWindowActionDelegate {

  private IWorkbenchWindow fWindow;
  private String           filterPath;

  public OpenFileAction() {
    setEnabled(true);
  }

  @Override
  public void dispose() {
    fWindow = null;
  }

  @Override
  public void init(IWorkbenchWindow window) {
    fWindow = window;
  }

  @Override
  public void run(IAction action) {
    run();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
  }

  @Override
  public void run() {
    String selectedFileName = chooseFile();
    openEditor(selectedFileName);
  }

  public void openEditor(String selectedFileName) {
    IFile file = getFile(selectedFileName);
    if (file != null) {
      IWorkbenchPage page = fWindow.getActivePage();
      try {
        page.openEditor(new FileEditorInput(file), CustomTextEditor.ID);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
  }

  private IFile getFile(String selectedFileName) {
    try {
      if (selectedFileName == null) {
        return null;
      }

      if (IOUtils.isZipFile(selectedFileName)) {
        File combinedFile = IOUtils.combineZipFileEntries(selectedFileName);
        selectedFileName = combinedFile.getAbsolutePath();
      }
      else if (IOUtils.isGzFile(selectedFileName)) {
        File combinedFile = IOUtils.combineGzFileEntries(selectedFileName);
        selectedFileName = combinedFile.getAbsolutePath();
      }

      IProject project = ProjectUtils.openProject();
      filterPath = new File(selectedFileName).getParent();
      IFile file = ProjectUtils.addFileToProject(project, selectedFileName);

      return file;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private String chooseFile() {
    FileDialog fileDialog = new FileDialog(fWindow.getShell(), SWT.OPEN);
    fileDialog.setFilterPath(filterPath);

    return fileDialog.open();
  }
}
