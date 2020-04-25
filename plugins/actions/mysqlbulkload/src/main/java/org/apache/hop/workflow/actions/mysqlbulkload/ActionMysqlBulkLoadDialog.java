/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * http://www.project-hop.org
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.apache.hop.workflow.actions.mysqlbulkload;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.annotations.PluginDialog;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopDatabaseException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.workflow.action.IActionDialog;
import org.apache.hop.ui.core.database.dialog.DatabaseExplorerDialog;
import org.apache.hop.ui.core.dialog.EnterSelectionDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog class for the MySqlBulkLoader.
 *
 * @author Samatar Hassan
 * @since Jan-2007
 */
@PluginDialog( 
		  id = "MYSQL_BULK_LOAD", 
		  image = "MysqlBulkLoad.svg", 
		  pluginType = PluginDialog.PluginType.ACTION,
		  documentationUrl = "https://www.project-hop.org/manual/latest/plugins/actions/"
)
public class ActionMysqlBulkLoadDialog extends ActionDialog implements IActionDialog {
  private static Class<?> PKG = ActionMysqlBulkLoad.class; // for i18n purposes, needed by Translator!!

  private static final String[] FILETYPES = new String[] {
    BaseMessages.getString( PKG, "JobMysqlBulkLoad.Filetype.Text" ),
    BaseMessages.getString( PKG, "JobMysqlBulkLoad.Filetype.All" ) };

  private Label wlName;
  private Text wName;
  private FormData fdlName, fdName;

  private MetaSelectionLine<DatabaseMeta> wConnection;

  // Schema name
  private Label wlSchemaname;
  private TextVar wSchemaname;
  private FormData fdlSchemaname, fdSchemaname;

  private Label wlTablename;
  private TextVar wTablename;
  private FormData fdlTablename, fdTablename;

  private Button wOk, wCancel;
  private Listener lsOk, lsCancel;
  private ActionMysqlBulkLoad action;
  private Shell shell;
  private SelectionAdapter lsDef;
  private boolean changed;

  // File
  private Label wlFilename;
  private Button wbFilename;
  private TextVar wFilename;
  private FormData fdlFilename, fdbFilename, fdFilename;

  // LocalInfile
  private Label wlLocalInfile;
  private Button wLocalInfile;
  private FormData fdlLocalInfile, fdLocalInfile;

  // Separator
  private Label wlSeparator;
  private TextVar wSeparator;
  private FormData fdlSeparator, fdSeparator;

  // Enclosed
  private Label wlEnclosed;
  private TextVar wEnclosed;
  private FormData fdlEnclosed, fdEnclosed;

  // Escaped
  private Label wlEscaped;
  private TextVar wEscaped;
  private FormData fdlEscaped, fdEscaped;

  // Line terminated
  private Label wlLineterminated;
  private TextVar wLineterminated;
  private FormData fdlLineterminated, fdLineterminated;

  // Line starting
  private Label wlLinestarted;
  private TextVar wLinestarted;
  private FormData fdlLinestarted, fdLinestarted;

  // List Columns
  private Label wlListattribut;
  private TextVar wListattribut;
  private FormData fdlListattribut, fdListattribut;

  // Ignore First lines
  private Label wlIgnorelines;
  private TextVar wIgnorelines;
  private FormData fdlIgnorelines, fdIgnorelines;

  // Replace
  private Label wlReplacedata;
  private Button wReplacedata;
  private FormData fdlReplacedata, fdReplacedata;

  // Priority
  private Label wlProrityValue;
  private CCombo wProrityValue;
  private FormData fdlProrityValue, fdProrityValue;

  private Button wbTable;
  private Button wbListattribut;

  // Add File to result

  private Group wFileResult;
  private FormData fdFileResult;

  private Label wlAddFileToResult;
  private Button wAddFileToResult;
  private FormData fdlAddFileToResult, fdAddFileToResult;

  public ActionMysqlBulkLoadDialog( Shell parent, IAction action, WorkflowMeta workflowMeta ) {
    super( parent, action, workflowMeta );
    this.action = (ActionMysqlBulkLoad) action;
    if ( this.action.getName() == null ) {
      this.action.setName( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Name.Default" ) );
    }
  }

  public IAction open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getWorkflowsDialogStyle() );
    props.setLook( shell );
    WorkflowDialog.setShellImage( shell, action );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        action.setChanged();
      }
    };
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Filename line
    wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Name.Label" ) );
    props.setLook( wlName );
    fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, 0 );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    // Connection line
    wConnection = addConnectionLine( shell, wName, action.getDatabase(), lsMod );

    // Schema name line
    wlSchemaname = new Label( shell, SWT.RIGHT );
    wlSchemaname.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Schemaname.Label" ) );
    props.setLook( wlSchemaname );
    fdlSchemaname = new FormData();
    fdlSchemaname.left = new FormAttachment( 0, 0 );
    fdlSchemaname.right = new FormAttachment( middle, 0 );
    fdlSchemaname.top = new FormAttachment( wConnection, margin );
    wlSchemaname.setLayoutData( fdlSchemaname );

    wSchemaname = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wSchemaname );
    wSchemaname.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Schemaname.Tooltip" ) );
    wSchemaname.addModifyListener( lsMod );
    fdSchemaname = new FormData();
    fdSchemaname.left = new FormAttachment( middle, 0 );
    fdSchemaname.top = new FormAttachment( wConnection, margin );
    fdSchemaname.right = new FormAttachment( 100, 0 );
    wSchemaname.setLayoutData( fdSchemaname );

    // Table name line
    wlTablename = new Label( shell, SWT.RIGHT );
    wlTablename.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Tablename.Label" ) );
    props.setLook( wlTablename );
    fdlTablename = new FormData();
    fdlTablename.left = new FormAttachment( 0, 0 );
    fdlTablename.right = new FormAttachment( middle, 0 );
    fdlTablename.top = new FormAttachment( wSchemaname, margin );
    wlTablename.setLayoutData( fdlTablename );

    wbTable = new Button( shell, SWT.PUSH | SWT.CENTER );
    props.setLook( wbTable );
    wbTable.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    FormData fdbTable = new FormData();
    fdbTable.right = new FormAttachment( 100, 0 );
    fdbTable.top = new FormAttachment( wSchemaname, margin / 2 );
    wbTable.setLayoutData( fdbTable );
    wbTable.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        getTableName();
      }
    } );

    wTablename = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wTablename );
    wTablename.addModifyListener( lsMod );
    fdTablename = new FormData();
    fdTablename.left = new FormAttachment( middle, 0 );
    fdTablename.top = new FormAttachment( wSchemaname, margin );
    fdTablename.right = new FormAttachment( wbTable, -margin );
    wTablename.setLayoutData( fdTablename );

    // Filename line
    wlFilename = new Label( shell, SWT.RIGHT );
    wlFilename.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Filename.Label" ) );
    props.setLook( wlFilename );
    fdlFilename = new FormData();
    fdlFilename.left = new FormAttachment( 0, 0 );
    fdlFilename.top = new FormAttachment( wTablename, margin );
    fdlFilename.right = new FormAttachment( middle, -margin );
    wlFilename.setLayoutData( fdlFilename );

    wbFilename = new Button( shell, SWT.PUSH | SWT.CENTER );
    props.setLook( wbFilename );
    wbFilename.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbFilename = new FormData();
    fdbFilename.right = new FormAttachment( 100, 0 );
    fdbFilename.top = new FormAttachment( wTablename, 0 );
    wbFilename.setLayoutData( fdbFilename );

    wFilename = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFilename );
    wFilename.addModifyListener( lsMod );
    fdFilename = new FormData();
    fdFilename.left = new FormAttachment( middle, 0 );
    fdFilename.top = new FormAttachment( wTablename, margin );
    fdFilename.right = new FormAttachment( wbFilename, -margin );
    wFilename.setLayoutData( fdFilename );

    // Whenever something changes, set the tooltip to the expanded version:
    wFilename.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wFilename.setToolTipText( workflowMeta.environmentSubstitute( wFilename.getText() ) );
      }
    } );

    wbFilename.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        FileDialog dialog = new FileDialog( shell, SWT.OPEN );
        dialog.setFilterExtensions( new String[] { "*.txt", "*.csv", "*" } );
        if ( wFilename.getText() != null ) {
          dialog.setFileName( workflowMeta.environmentSubstitute( wFilename.getText() ) );
        }
        dialog.setFilterNames( FILETYPES );
        if ( dialog.open() != null ) {
          wFilename.setText( dialog.getFilterPath() + Const.FILE_SEPARATOR + dialog.getFileName() );
        }
      }
    } );

    // Local
    wlLocalInfile = new Label( shell, SWT.RIGHT );
    wlLocalInfile.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.LocalInfile.Label" ) );
    props.setLook( wlLocalInfile );
    fdlLocalInfile = new FormData();
    fdlLocalInfile.left = new FormAttachment( 0, 0 );
    fdlLocalInfile.top = new FormAttachment( wFilename, margin );
    fdlLocalInfile.right = new FormAttachment( middle, -margin );
    wlLocalInfile.setLayoutData( fdlLocalInfile );
    wLocalInfile = new Button( shell, SWT.CHECK );
    props.setLook( wLocalInfile );
    wLocalInfile.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.LocalInfile.Tooltip" ) );
    fdLocalInfile = new FormData();
    fdLocalInfile.left = new FormAttachment( middle, 0 );
    fdLocalInfile.top = new FormAttachment( wFilename, margin );
    fdLocalInfile.right = new FormAttachment( 100, 0 );
    wLocalInfile.setLayoutData( fdLocalInfile );
    wLocalInfile.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        action.setChanged();
      }
    } );

    // Priority
    wlProrityValue = new Label( shell, SWT.RIGHT );
    wlProrityValue.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.ProrityValue.Label" ) );
    props.setLook( wlProrityValue );
    fdlProrityValue = new FormData();
    fdlProrityValue.left = new FormAttachment( 0, 0 );
    fdlProrityValue.right = new FormAttachment( middle, 0 );
    fdlProrityValue.top = new FormAttachment( wLocalInfile, margin );
    wlProrityValue.setLayoutData( fdlProrityValue );
    wProrityValue = new CCombo( shell, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
    wProrityValue.add( BaseMessages.getString( PKG, "JobMysqlBulkLoad.NorProrityValue.Label" ) );
    wProrityValue.add( BaseMessages.getString( PKG, "JobMysqlBulkLoad.LowProrityValue.Label" ) );
    wProrityValue.add( BaseMessages.getString( PKG, "JobMysqlBulkLoad.ConProrityValue.Label" ) );
    wProrityValue.select( 0 ); // +1: starts at -1

    props.setLook( wProrityValue );
    fdProrityValue = new FormData();
    fdProrityValue.left = new FormAttachment( middle, 0 );
    fdProrityValue.top = new FormAttachment( wLocalInfile, margin );
    fdProrityValue.right = new FormAttachment( 100, 0 );
    wProrityValue.setLayoutData( fdProrityValue );

    fdProrityValue = new FormData();
    fdProrityValue.left = new FormAttachment( middle, 0 );
    fdProrityValue.top = new FormAttachment( wLocalInfile, margin );
    fdProrityValue.right = new FormAttachment( 100, 0 );
    wProrityValue.setLayoutData( fdProrityValue );

    // Separator
    wlSeparator = new Label( shell, SWT.RIGHT );
    wlSeparator.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Separator.Label" ) );
    props.setLook( wlSeparator );
    fdlSeparator = new FormData();
    fdlSeparator.left = new FormAttachment( 0, 0 );
    fdlSeparator.right = new FormAttachment( middle, 0 );
    fdlSeparator.top = new FormAttachment( wProrityValue, margin );
    wlSeparator.setLayoutData( fdlSeparator );

    wSeparator = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wSeparator );
    wSeparator.addModifyListener( lsMod );
    fdSeparator = new FormData();
    fdSeparator.left = new FormAttachment( middle, 0 );
    fdSeparator.top = new FormAttachment( wProrityValue, margin );
    fdSeparator.right = new FormAttachment( 100, 0 );
    wSeparator.setLayoutData( fdSeparator );

    // enclosed
    wlEnclosed = new Label( shell, SWT.RIGHT );
    wlEnclosed.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Enclosed.Label" ) );
    props.setLook( wlEnclosed );
    fdlEnclosed = new FormData();
    fdlEnclosed.left = new FormAttachment( 0, 0 );
    fdlEnclosed.right = new FormAttachment( middle, 0 );
    fdlEnclosed.top = new FormAttachment( wSeparator, margin );
    wlEnclosed.setLayoutData( fdlEnclosed );

    wEnclosed = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wEnclosed );
    wEnclosed.addModifyListener( lsMod );
    fdEnclosed = new FormData();
    fdEnclosed.left = new FormAttachment( middle, 0 );
    fdEnclosed.top = new FormAttachment( wSeparator, margin );
    fdEnclosed.right = new FormAttachment( 100, 0 );
    wEnclosed.setLayoutData( fdEnclosed );

    // escaped
    wlEscaped = new Label( shell, SWT.RIGHT );
    wlEscaped.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Escaped.Label" ) );
    props.setLook( wlEscaped );
    fdlEscaped = new FormData();
    fdlEscaped.left = new FormAttachment( 0, 0 );
    fdlEscaped.right = new FormAttachment( middle, 0 );
    fdlEscaped.top = new FormAttachment( wEnclosed, margin );
    wlEscaped.setLayoutData( fdlEscaped );

    wEscaped = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wEscaped );
    wEscaped.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Escaped.Tooltip" ) );
    wEscaped.addModifyListener( lsMod );
    fdEscaped = new FormData();
    fdEscaped.left = new FormAttachment( middle, 0 );
    fdEscaped.top = new FormAttachment( wEnclosed, margin );
    fdEscaped.right = new FormAttachment( 100, 0 );
    wEscaped.setLayoutData( fdEscaped );

    // Line started
    wlLinestarted = new Label( shell, SWT.RIGHT );
    wlLinestarted.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Linestarted.Label" ) );
    props.setLook( wlLinestarted );
    fdlLinestarted = new FormData();
    fdlLinestarted.left = new FormAttachment( 0, 0 );
    fdlLinestarted.right = new FormAttachment( middle, 0 );
    fdlLinestarted.top = new FormAttachment( wEscaped, margin );
    wlLinestarted.setLayoutData( fdlLinestarted );

    wLinestarted = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wLinestarted );
    wLinestarted.addModifyListener( lsMod );
    fdLinestarted = new FormData();
    fdLinestarted.left = new FormAttachment( middle, 0 );
    fdLinestarted.top = new FormAttachment( wEscaped, margin );
    fdLinestarted.right = new FormAttachment( 100, 0 );
    wLinestarted.setLayoutData( fdLinestarted );

    // Line terminated
    wlLineterminated = new Label( shell, SWT.RIGHT );
    wlLineterminated.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Lineterminated.Label" ) );
    props.setLook( wlLineterminated );
    fdlLineterminated = new FormData();
    fdlLineterminated.left = new FormAttachment( 0, 0 );
    fdlLineterminated.right = new FormAttachment( middle, 0 );
    fdlLineterminated.top = new FormAttachment( wLinestarted, margin );
    wlLineterminated.setLayoutData( fdlLineterminated );

    wLineterminated = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wLineterminated );
    wLineterminated.addModifyListener( lsMod );
    fdLineterminated = new FormData();
    fdLineterminated.left = new FormAttachment( middle, 0 );
    fdLineterminated.top = new FormAttachment( wLinestarted, margin );
    fdLineterminated.right = new FormAttachment( 100, 0 );
    wLineterminated.setLayoutData( fdLineterminated );

    // List of columns to set for
    wlListattribut = new Label( shell, SWT.RIGHT );
    wlListattribut.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Listattribut.Label" ) );
    props.setLook( wlListattribut );
    fdlListattribut = new FormData();
    fdlListattribut.left = new FormAttachment( 0, 0 );
    fdlListattribut.right = new FormAttachment( middle, 0 );
    fdlListattribut.top = new FormAttachment( wLineterminated, margin );
    wlListattribut.setLayoutData( fdlListattribut );

    wbListattribut = new Button( shell, SWT.PUSH | SWT.CENTER );
    props.setLook( wbListattribut );
    wbListattribut.setText( BaseMessages.getString( PKG, "System.Button.Edit" ) );
    FormData fdbListattribut = new FormData();
    fdbListattribut.right = new FormAttachment( 100, 0 );
    fdbListattribut.top = new FormAttachment( wLineterminated, margin );
    wbListattribut.setLayoutData( fdbListattribut );
    wbListattribut.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        getListColumns();
      }
    } );

    wListattribut = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wListattribut );
    wListattribut.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Listattribut.Tooltip" ) );
    wListattribut.addModifyListener( lsMod );
    fdListattribut = new FormData();
    fdListattribut.left = new FormAttachment( middle, 0 );
    fdListattribut.top = new FormAttachment( wLineterminated, margin );
    fdListattribut.right = new FormAttachment( wbListattribut, -margin );
    wListattribut.setLayoutData( fdListattribut );

    // Replace data
    wlReplacedata = new Label( shell, SWT.RIGHT );
    wlReplacedata.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Replacedata.Label" ) );
    props.setLook( wlReplacedata );
    fdlReplacedata = new FormData();
    fdlReplacedata.left = new FormAttachment( 0, 0 );
    fdlReplacedata.top = new FormAttachment( wListattribut, margin );
    fdlReplacedata.right = new FormAttachment( middle, -margin );
    wlReplacedata.setLayoutData( fdlReplacedata );
    wReplacedata = new Button( shell, SWT.CHECK );
    props.setLook( wReplacedata );
    wReplacedata.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Replacedata.Tooltip" ) );
    fdReplacedata = new FormData();
    fdReplacedata.left = new FormAttachment( middle, 0 );
    fdReplacedata.top = new FormAttachment( wListattribut, margin );
    fdReplacedata.right = new FormAttachment( 100, 0 );
    wReplacedata.setLayoutData( fdReplacedata );
    wReplacedata.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        action.setChanged();
      }
    } );

    // Nbr of lines to ignore
    wlIgnorelines = new Label( shell, SWT.RIGHT );
    wlIgnorelines.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.Ignorelines.Label" ) );
    props.setLook( wlIgnorelines );
    fdlIgnorelines = new FormData();
    fdlIgnorelines.left = new FormAttachment( 0, 0 );
    fdlIgnorelines.right = new FormAttachment( middle, 0 );
    fdlIgnorelines.top = new FormAttachment( wReplacedata, margin );
    wlIgnorelines.setLayoutData( fdlIgnorelines );

    wIgnorelines = new TextVar( workflowMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wIgnorelines );
    wIgnorelines.addModifyListener( lsMod );
    fdIgnorelines = new FormData();
    fdIgnorelines.left = new FormAttachment( middle, 0 );
    fdIgnorelines.top = new FormAttachment( wReplacedata, margin );
    fdIgnorelines.right = new FormAttachment( 100, 0 );
    wIgnorelines.setLayoutData( fdIgnorelines );

    // fileresult grouping?
    // ////////////////////////
    // START OF FileResult GROUP///
    // /
    wFileResult = new Group( shell, SWT.SHADOW_NONE );
    props.setLook( wFileResult );
    wFileResult.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.FileResult.Group.Label" ) );

    FormLayout groupLayout = new FormLayout();
    groupLayout.marginWidth = 10;
    groupLayout.marginHeight = 10;

    wFileResult.setLayout( groupLayout );

    // Add file to result
    wlAddFileToResult = new Label( wFileResult, SWT.RIGHT );
    wlAddFileToResult.setText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.AddFileToResult.Label" ) );
    props.setLook( wlAddFileToResult );
    fdlAddFileToResult = new FormData();
    fdlAddFileToResult.left = new FormAttachment( 0, 0 );
    fdlAddFileToResult.top = new FormAttachment( wIgnorelines, margin );
    fdlAddFileToResult.right = new FormAttachment( middle, -margin );
    wlAddFileToResult.setLayoutData( fdlAddFileToResult );
    wAddFileToResult = new Button( wFileResult, SWT.CHECK );
    props.setLook( wAddFileToResult );
    wAddFileToResult.setToolTipText( BaseMessages.getString( PKG, "JobMysqlBulkLoad.AddFileToResult.Tooltip" ) );
    fdAddFileToResult = new FormData();
    fdAddFileToResult.left = new FormAttachment( middle, 0 );
    fdAddFileToResult.top = new FormAttachment( wIgnorelines, margin );
    fdAddFileToResult.right = new FormAttachment( 100, 0 );
    wAddFileToResult.setLayoutData( fdAddFileToResult );
    wAddFileToResult.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        action.setChanged();
      }
    } );

    fdFileResult = new FormData();
    fdFileResult.left = new FormAttachment( 0, margin );
    fdFileResult.top = new FormAttachment( wIgnorelines, margin );
    fdFileResult.right = new FormAttachment( 100, -margin );
    wFileResult.setLayoutData( fdFileResult );
    // ///////////////////////////////////////////////////////////
    // / END OF FilesRsult GROUP
    // ///////////////////////////////////////////////////////////

    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    FormData fd = new FormData();
    fd.right = new FormAttachment( 50, -10 );
    fd.bottom = new FormAttachment( 100, 0 );
    fd.width = 100;
    wOk.setLayoutData( fd );

    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    fd = new FormData();
    fd.left = new FormAttachment( 50, 10 );
    fd.bottom = new FormAttachment( 100, 0 );
    fd.width = 100;
    wCancel.setLayoutData( fd );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOk = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOk.addListener( SWT.Selection, lsOk );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wName.addSelectionListener( lsDef );
    wTablename.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    BaseTransformDialog.setSize( shell );

    shell.open();
    props.setDialogSize( shell, "JobMysqlBulkLoadDialogSize" );
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return action;
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wName.setText( Const.nullToEmpty( action.getName() ) );
    if ( action.getSchemaname() != null ) {
      wSchemaname.setText( action.getSchemaname() );
    }
    if ( action.getTablename() != null ) {
      wTablename.setText( action.getTablename() );
    }
    if ( action.getFilename() != null ) {
      wFilename.setText( action.getFilename() );
    }
    if ( action.getSeparator() != null ) {
      wSeparator.setText( action.getSeparator() );
    }

    if ( action.getEnclosed() != null ) {
      wEnclosed.setText( action.getEnclosed() );
    }

    if ( action.getEscaped() != null ) {
      wEscaped.setText( action.getEscaped() );
    }
    if ( action.getLinestarted() != null ) {
      wLinestarted.setText( action.getLinestarted() );
    }
    if ( action.getLineterminated() != null ) {
      wLineterminated.setText( action.getLineterminated() );
    }

    wReplacedata.setSelection( action.isReplacedata() );

    wLocalInfile.setSelection( action.isLocalInfile() );

    if ( action.getIgnorelines() != null ) {

      wIgnorelines.setText( action.getIgnorelines() );
    } else {
      wIgnorelines.setText( "0" );
    }

    if ( action.getListattribut() != null ) {
      wListattribut.setText( action.getListattribut() );
    }

    if ( action.prorityvalue >= 0 ) {
      wProrityValue.select( action.prorityvalue );
    } else {
      wProrityValue.select( 0 ); // NORMAL priority
    }

    if ( action.getDatabase() != null ) {
      wConnection.setText( action.getDatabase().getName() );
    }

    wAddFileToResult.setSelection( action.isAddFileToResult() );

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    action.setChanged( changed );
    action = null;
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( BaseMessages.getString( PKG, "System.TransformActionNameMissing.Title" ) );
      mb.setMessage( BaseMessages.getString( PKG, "System.ActionNameMissing.Msg" ) );
      mb.open();
      return;
    }
    action.setName( wName.getText() );
    action.setDatabase( workflowMeta.findDatabase( wConnection.getText() ) );
    action.setSchemaname( wSchemaname.getText() );
    action.setTablename( wTablename.getText() );
    action.setFilename( wFilename.getText() );
    action.setSeparator( wSeparator.getText() );
    action.setEnclosed( wEnclosed.getText() );
    action.setEscaped( wEscaped.getText() );
    action.setLineterminated( wLineterminated.getText() );
    action.setLinestarted( wLinestarted.getText() );
    action.setReplacedata( wReplacedata.getSelection() );
    action.setIgnorelines( wIgnorelines.getText() );
    action.setListattribut( wListattribut.getText() );
    action.prorityvalue = wProrityValue.getSelectionIndex();
    action.setLocalInfile( wLocalInfile.getSelection() );

    action.setAddFileToResult( wAddFileToResult.getSelection() );

    dispose();
  }

  private void getTableName() {
    String databaseName = wConnection.getText();
    if ( StringUtils.isNotEmpty( databaseName ) ) {
      DatabaseMeta databaseMeta = workflowMeta.findDatabase( databaseName );
      if ( databaseMeta != null ) {
        DatabaseExplorerDialog std = new DatabaseExplorerDialog( shell, SWT.NONE, databaseMeta, workflowMeta.getDatabases() );
        std.setSelectedSchemaAndTable( wSchemaname.getText(), wTablename.getText() );
        if ( std.open() ) {
          wTablename.setText( Const.NVL( std.getTableName(), "" ) );
        }
      } else {
        MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
        mb.setMessage( BaseMessages.getString( PKG, "JobMysqlBulkLoad.ConnectionError2.DialogMessage" ) );
        mb.setText( BaseMessages.getString( PKG, "System.Dialog.Error.Title" ) );
        mb.open();
      }
    }
  }

  /**
   * Get a list of columns, comma separated, allow the user to select from it.
   */
  private void getListColumns() {
    if ( !Utils.isEmpty( wTablename.getText() ) ) {
      DatabaseMeta databaseMeta = workflowMeta.findDatabase( wConnection.getText() );
      if ( databaseMeta != null ) {
        Database database = new Database( loggingObject, databaseMeta );
        database.shareVariablesWith( workflowMeta );
        try {
          database.connect();
          String schemaTable =
            databaseMeta.getQuotedSchemaTableCombination( wSchemaname.getText(), wTablename.getText() );
          IRowMeta row = database.getTableFields( schemaTable );
          String[] available = row.getFieldNames();

          String[] source = wListattribut.getText().split( "," );
          for ( int i = 0; i < source.length; i++ ) {
            source[ i ] = Const.trim( source[ i ] );
          }
          int[] idxSource = Const.indexsOfStrings( source, available );
          EnterSelectionDialog dialog = new EnterSelectionDialog( shell, available,
            BaseMessages.getString( PKG, "JobMysqlBulkLoad.SelectColumns.Title" ),
            BaseMessages.getString( PKG, "JobMysqlBulkLoad.SelectColumns.Message" ) );
          dialog.setMulti( true );
          dialog.setAvoidQuickSearch();
          dialog.setSelectedNrs( idxSource );
          if ( dialog.open() != null ) {
            String columns = "";
            int[] idx = dialog.getSelectionIndeces();
            for ( int i = 0; i < idx.length; i++ ) {
              if ( i > 0 ) {
                columns += ", ";
              }
              columns += available[ idx[ i ] ];
            }
            wListattribut.setText( columns );
          }
        } catch ( HopDatabaseException e ) {
          new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.Error.Title" ), BaseMessages
            .getString( PKG, "JobMysqlBulkLoad.ConnectionError2.DialogMessage" ), e );
        } finally {
          database.disconnect();
        }
      }
    }
  }
}