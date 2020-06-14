
package org.apache.hop.beam.transforms.kafka;

import org.apache.hop.core.annotations.PluginDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.hop.core.Const;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;

@PluginDialog(
        id = "BeamKafkaProduce",
        image = "beam-kafka-output.svg",
        pluginType = PluginDialog.PluginType.TRANSFORM
)
public class BeamProduceDialog extends BaseTransformDialog implements ITransformDialog {
  private static Class<?> PKG = BeamProduce.class; // for i18n purposes, needed by Translator2!!
  private final BeamProduceMeta input;

  int middle;
  int margin;

  private TextVar wBootstrapServers;
  private TextVar wTopic;
  private TextVar wKeyField;
  private TextVar wMessageField;

  public BeamProduceDialog( Shell parent, Object in, PipelineMeta pipelineMeta, String sname ) {
    super( parent, (BaseTransformMeta) in, pipelineMeta, sname );
    input = (BeamProduceMeta) in;
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "BeamProduceDialog.DialogTitle" ) );

    middle = props.getMiddlePct();
    margin = Const.MARGIN;

    // Stepname line
    wlTransformName = new Label( shell, SWT.RIGHT );
    wlTransformName.setText( BaseMessages.getString( PKG, "System.Label.TransformName" ) );
    props.setLook( wlTransformName );
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment( 0, 0 );
    fdlTransformName.top = new FormAttachment( 0, margin );
    fdlTransformName.right = new FormAttachment( middle, -margin );
    wlTransformName.setLayoutData( fdlTransformName );
    wTransformName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wTransformName.setText( transformName );
    props.setLook( wTransformName );
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment( middle, 0 );
    fdTransformName.top = new FormAttachment( wlTransformName, 0, SWT.CENTER );
    fdTransformName.right = new FormAttachment( 100, 0 );
    wTransformName.setLayoutData( fdTransformName );
    Control lastControl = wTransformName;

    Label wlBootstrapServers = new Label( shell, SWT.RIGHT );
    wlBootstrapServers.setText( BaseMessages.getString( PKG, "BeamProduceDialog.BootstrapServers" ) );
    props.setLook( wlBootstrapServers );
    FormData fdlBootstrapServers = new FormData();
    fdlBootstrapServers.left = new FormAttachment( 0, 0 );
    fdlBootstrapServers.top = new FormAttachment( lastControl, margin );
    fdlBootstrapServers.right = new FormAttachment( middle, -margin );
    wlBootstrapServers.setLayoutData( fdlBootstrapServers );
    wBootstrapServers = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wBootstrapServers );
    FormData fdBootstrapServers = new FormData();
    fdBootstrapServers.left = new FormAttachment( middle, 0 );
    fdBootstrapServers.top = new FormAttachment( wlBootstrapServers, 0, SWT.CENTER );
    fdBootstrapServers.right = new FormAttachment( 100, 0 );
    wBootstrapServers.setLayoutData( fdBootstrapServers );
    lastControl = wBootstrapServers;

    Label wlTopic = new Label( shell, SWT.RIGHT );
    wlTopic.setText( BaseMessages.getString( PKG, "BeamProduceDialog.Topic" ) );
    props.setLook( wlTopic );
    FormData fdlTopic = new FormData();
    fdlTopic.left = new FormAttachment( 0, 0 );
    fdlTopic.top = new FormAttachment( lastControl, margin );
    fdlTopic.right = new FormAttachment( middle, -margin );
    wlTopic.setLayoutData( fdlTopic );
    wTopic = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wTopic );
    FormData fdTopic = new FormData();
    fdTopic.left = new FormAttachment( middle, 0 );
    fdTopic.top = new FormAttachment( wlTopic, 0, SWT.CENTER );
    fdTopic.right = new FormAttachment( 100, 0 );
    wTopic.setLayoutData( fdTopic );
    lastControl = wTopic;

    Label wlKeyField = new Label( shell, SWT.RIGHT );
    wlKeyField.setText( BaseMessages.getString( PKG, "BeamProduceDialog.KeyField" ) );
    props.setLook( wlKeyField );
    FormData fdlKeyField = new FormData();
    fdlKeyField.left = new FormAttachment( 0, 0 );
    fdlKeyField.top = new FormAttachment( lastControl, margin );
    fdlKeyField.right = new FormAttachment( middle, -margin );
    wlKeyField.setLayoutData( fdlKeyField );
    wKeyField = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wKeyField );
    FormData fdKeyField = new FormData();
    fdKeyField.left = new FormAttachment( middle, 0 );
    fdKeyField.top = new FormAttachment( wlKeyField, 0, SWT.CENTER );
    fdKeyField.right = new FormAttachment( 100, 0 );
    wKeyField.setLayoutData( fdKeyField );
    lastControl = wKeyField;

    Label wlMessageField = new Label( shell, SWT.RIGHT );
    wlMessageField.setText( BaseMessages.getString( PKG, "BeamProduceDialog.MessageField" ) );
    props.setLook( wlMessageField );
    FormData fdlMessageField = new FormData();
    fdlMessageField.left = new FormAttachment( 0, 0 );
    fdlMessageField.top = new FormAttachment( lastControl, margin );
    fdlMessageField.right = new FormAttachment( middle, -margin );
    wlMessageField.setLayoutData( fdlMessageField );
    wMessageField = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wMessageField );
    FormData fdMessageField = new FormData();
    fdMessageField.left = new FormAttachment( middle, 0 );
    fdMessageField.top = new FormAttachment( wlMessageField, 0, SWT.CENTER );
    fdMessageField.right = new FormAttachment( 100, 0 );
    wMessageField.setLayoutData( fdMessageField );
    lastControl = wMessageField;

    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );

    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOk, wCancel }, margin, lastControl );


    wOk.addListener( SWT.Selection, e -> ok() );
    wCancel.addListener( SWT.Selection, e -> cancel() );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wTransformName.addSelectionListener( lsDef );
    wBootstrapServers.addSelectionListener( lsDef );
    wTopic.addSelectionListener( lsDef );
    wKeyField.addSelectionListener( lsDef );
    wMessageField.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addListener( SWT.Close, e->cancel());

    getData();
    setSize();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return transformName;
  }


  /**
   * Populate the widgets.
   */
  public void getData() {
    wTransformName.setText( transformName );
    wBootstrapServers.setText( Const.NVL( input.getBootstrapServers(), "" ) );
    wTopic.setText( Const.NVL( input.getTopic(), "" ) );
    wKeyField.setText( Const.NVL( input.getKeyField(), "" ) );
    wMessageField.setText( Const.NVL( input.getMessageField(), "" ) );

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wTransformName.getText() ) ) {
      return;
    }

    getInfo( input );

    dispose();
  }

  private void getInfo( BeamProduceMeta in ) {
    transformName = wTransformName.getText(); // return value

    in.setBootstrapServers( wBootstrapServers.getText() );
    in.setTopic( wTopic.getText() );
    in.setKeyField( wKeyField.getText() );
    in.setMessageField( wMessageField.getText() );

    input.setChanged();
  }
}