/*
 * Copyright 2012 Dart project authors.
 * 
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.sdbg.debug.ui.internal.launch;

import com.github.sdbg.debug.ui.internal.SDBGDebugUIPlugin;
import com.github.sdbg.debug.ui.internal.dialogs.ManageLaunchesDialog;
import com.github.sdbg.ui.actions.InstrumentedAction;
import com.github.sdbg.ui.instrumentation.UIInstrumentationBuilder;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * An action to open the manage launch configurations dialog.
 */
public class ManageLaunchesAction extends InstrumentedAction {
  private IWorkbenchWindow window;

  public ManageLaunchesAction(IWorkbenchWindow window) {
    super("Manage Launches...");

    this.window = window;

    setActionDefinitionId("com.github.sdbg.debug.ui.launchDialog");
    setImageDescriptor(SDBGDebugUIPlugin.getImageDescriptor("obj16/manage_launches.png"));
  }

  @Override
  public void doRun(Event event, UIInstrumentationBuilder instrumentation) {
    ManageLaunchesDialog dialog = new ManageLaunchesDialog(window);

    dialog.open();
  }
}
