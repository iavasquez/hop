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

package org.apache.hop.pipeline;

import org.apache.hop.core.xml.XMLHandler;
import org.apache.hop.junit.rules.RestoreHopEngineEnvironment;
import org.junit.ClassRule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;

public class PipelineExecutionConfigurationTest {
  @ClassRule public static RestoreHopEngineEnvironment env = new RestoreHopEngineEnvironment();

  @Test
  public void testDefaultPassedBatchId() {
    PipelineExecutionConfiguration tec = new PipelineExecutionConfiguration();
    assertEquals( "default passedBatchId value must be null", null, tec.getPassedBatchId() );
  }

  @Test
  public void testCopy() {
    PipelineExecutionConfiguration tec = new PipelineExecutionConfiguration();

    tec.setPassedBatchId( null );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      PipelineExecutionConfiguration tecCopy = (PipelineExecutionConfiguration) tec.clone();
      assertEquals( "clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
    tec.setPassedBatchId( 0L );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      PipelineExecutionConfiguration tecCopy = (PipelineExecutionConfiguration) tec.clone();
      assertEquals( "clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
    tec.setPassedBatchId( 5L );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      PipelineExecutionConfiguration tecCopy = (PipelineExecutionConfiguration) tec.clone();
      assertEquals( "clone-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
  }

  @Test
  public void testCopyXml() throws Exception {
    PipelineExecutionConfiguration tec = new PipelineExecutionConfiguration();
    final Long passedBatchId0 = null;
    final long passedBatchId1 = 0L;
    final long passedBatchId2 = 5L;
    tec.setPassedBatchId( passedBatchId0 );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      String xml = tec.getXML();
      Document doc = XMLHandler.loadXMLString( xml );
      Node node = XMLHandler.getSubNode( doc, PipelineExecutionConfiguration.XML_TAG );
      PipelineExecutionConfiguration tecCopy = new PipelineExecutionConfiguration( node );
      assertEquals( "xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
    tec.setPassedBatchId( passedBatchId1 );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      String xml = tec.getXML();
      Document doc = XMLHandler.loadXMLString( xml );
      Node node = XMLHandler.getSubNode( doc, PipelineExecutionConfiguration.XML_TAG );
      PipelineExecutionConfiguration tecCopy = new PipelineExecutionConfiguration( node );
      assertEquals( "xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
    tec.setPassedBatchId( passedBatchId2 );
    //CHECKSTYLE IGNORE AvoidNestedBlocks FOR NEXT 3 LINES
    {
      String xml = tec.getXML();
      Document doc = XMLHandler.loadXMLString( xml );
      Node node = XMLHandler.getSubNode( doc, PipelineExecutionConfiguration.XML_TAG );
      PipelineExecutionConfiguration tecCopy = new PipelineExecutionConfiguration( node );
      assertEquals( "xml-copy", tec.getPassedBatchId(), tecCopy.getPassedBatchId() );
    }
  }
}