/*
 * Copyright (c) 2013, the Dart project authors.
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
package com.github.sdbg.debug.ui.internal.browser;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.github.sdbg.debug.core.SDBGLaunchConfigWrapper;
import com.github.sdbg.debug.ui.internal.SDBGDebugUIPlugin;
import com.github.sdbg.debug.ui.internal.util.LaunchTargetComposite;

/**
 * Main launch tab for Browser launch configurations
 */
public class BrowserMainTab extends AbstractLaunchConfigurationTab {

  private static Font italicFont;

  private static Font getItalicFont(Font font) {
    if (italicFont == null) {
      FontData data = font.getFontData()[0];

      italicFont = new Font(Display.getDefault(), new FontData(
          data.getName(),
          data.getHeight(),
          SWT.ITALIC));
    }

    return italicFont;
  }

  private Text dart2jsFlagsText;
  private int hIndent = 20;
  private Button runDart2jsButton;
  private LaunchTargetComposite launchTargetGroup;

  @Override
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults().spacing(1, 3).applyTo(composite);

    launchTargetGroup = new LaunchTargetComposite(composite, SWT.NONE);
    launchTargetGroup.addListener(SWT.Modify, new Listener() {

      @Override
      public void handleEvent(Event event) {
        notifyPanelChanged();
      }
    });

    // dart2js group
    Group dart2jsGroup = new Group(composite, SWT.NONE);
    dart2jsGroup.setText(Messages.BrowserMainTab_Dart2js);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(dart2jsGroup);
    GridLayoutFactory.swtDefaults().numColumns(3).applyTo(dart2jsGroup);
    ((GridLayout) dart2jsGroup.getLayout()).marginBottom = 5;

    runDart2jsButton = new Button(dart2jsGroup, SWT.CHECK);
    runDart2jsButton.setText("Compile before launch");
    GridDataFactory.swtDefaults().span(3, 1).applyTo(runDart2jsButton);

    Label dart2jsLabel = new Label(dart2jsGroup, SWT.NONE);
    dart2jsLabel.setText("Compiler flags:");
    GridDataFactory.swtDefaults().hint(launchTargetGroup.getLabelColumnWidth() + hIndent, -1).applyTo(
        dart2jsLabel);

    dart2jsFlagsText = new Text(dart2jsGroup, SWT.BORDER | SWT.SINGLE);
    GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(
        dart2jsFlagsText);

    Label label = new Label(dart2jsGroup, SWT.NONE);
    GridDataFactory.swtDefaults().hint(launchTargetGroup.getButtonWidthHint(), -1).applyTo(label);

    label = new Label(dart2jsGroup, SWT.NONE);
    label.setText("(e.g. --minify)");
    label.setFont(getItalicFont(label.getFont()));
    GridDataFactory.swtDefaults().indent(
        hIndent + launchTargetGroup.getLabelColumnWidth(),
        SWT.DEFAULT).span(3, 1).applyTo(label);

    setControl(composite);
  }

  @Override
  public void dispose() {
    Control control = getControl();

    if (control != null) {
      control.dispose();
      setControl(null);
    }

    if (italicFont != null) {
      italicFont.dispose();
    }
  }

  @Override
  public String getErrorMessage() {
    if (performSdkCheck() != null) {
      return performSdkCheck();
    }

    return launchTargetGroup.getErrorMessage();
  }

  /**
   * Answer the image to show in the configuration tab or <code>null</code> if none
   */
  @Override
  public Image getImage() {
    return SDBGDebugUIPlugin.getImage("obj16/globe_dark.png"); //$NON-NLS-1$
  }

  @Override
  public String getMessage() {
    return Messages.BrowserMainTab_Description;
  }

  /**
   * Answer the name to show in the configuration tab
   */
  @Override
  public String getName() {
    return Messages.BrowserMainTab_Name;
  }

  /**
   * Initialize the UI from the specified configuration
   */
  @Override
  public void initializeFrom(ILaunchConfiguration config) {

    SDBGLaunchConfigWrapper wrapper = new SDBGLaunchConfigWrapper(config);
    launchTargetGroup.setHtmlTextValue(wrapper.appendQueryParams(wrapper.getApplicationName()));
    launchTargetGroup.setUrlTextValue(wrapper.getUrl());

    launchTargetGroup.setSourceDirectoryTextValue(wrapper.getSourceDirectoryName());

    if (wrapper.getShouldLaunchFile()) {
      launchTargetGroup.setHtmlButtonSelection(true);
      updateEnablements(true);
    } else {
      launchTargetGroup.setHtmlButtonSelection(false);
      updateEnablements(false);
    }
    runDart2jsButton.setSelection(wrapper.getRunDart2js());
    dart2jsFlagsText.setText(wrapper.getDart2jsFlags());
  }

  private void notifyPanelChanged() {
    setDirty(true);
    updateEnablements(launchTargetGroup.getHtmlButtonSelection());
    updateLaunchConfigurationDialog();
  }

  /**
   * Store the value specified in the UI into the launch configuration
   */
  @Override
  public void performApply(ILaunchConfigurationWorkingCopy config) {

    SDBGLaunchConfigWrapper wrapper = new SDBGLaunchConfigWrapper(config);
    wrapper.setShouldLaunchFile(launchTargetGroup.getHtmlButtonSelection());

    String fileUrl = launchTargetGroup.getHtmlFileName();

    if (fileUrl.indexOf('?') == -1) {
      wrapper.setApplicationName(fileUrl);
      wrapper.setUrlQueryParams("");
    } else {
      int index = fileUrl.indexOf('?');

      wrapper.setApplicationName(fileUrl.substring(0, index));
      wrapper.setUrlQueryParams(fileUrl.substring(index + 1));
    }

    wrapper.setUrl(launchTargetGroup.getUrlString());
    wrapper.setSourceDirectoryName(launchTargetGroup.getSourceDirectory());

    wrapper.setRunDart2js(runDart2jsButton.getSelection());
    wrapper.setDart2jsFlags(dart2jsFlagsText.getText().trim());

  }

  private String performSdkCheck() {
//&&&    
//    if (!DartSdkManager.getManager().hasSdk()) {
//      return "Dart2js is not installed ("
//          + DartSdkManager.getManager().getSdk().getDart2JsExecutable() + ")";
//    } else {
    return null;
//    }
  }

  @Override
  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    SDBGLaunchConfigWrapper wrapper = new SDBGLaunchConfigWrapper(configuration);
    wrapper.setShouldLaunchFile(true);
    wrapper.setApplicationName(""); //$NON-NLS-1$
    wrapper.setRunDart2js(true);
  }

  private void updateEnablements(boolean isFile) {
    runDart2jsButton.setEnabled(isFile);
    dart2jsFlagsText.setEnabled(isFile);
  }
}
