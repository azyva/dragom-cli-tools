/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
 *
 * This file is part of Dragom.
 *
 * Dragom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Dragom.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.azyva.dragom.test.integration;

import org.azyva.dragom.tool.DragomToolInvoker;

public class IntegrationTestSuiteDragomToolInvoker {
  /*********************************************************************************
   * Tests DragomToolInvoker.
   *********************************************************************************/
  public static void testDragomToolInvoker() {
    try {
      IntegrationTestSuite.printTestCategoryHeader("DragomToolInvoker");

      IntegrationTestSuite.resetTestWorkspace();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker");
      try {
        DragomToolInvoker.main(new String[] {});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker --help");
      try {
        DragomToolInvoker.main(new String[] {"--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker dummy");
      try {
        DragomToolInvoker.main(new String[] {"dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker exec-context-manager");
      try {
        DragomToolInvoker.main(new String[] {"exec-context-manager"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker exec-context-manager --help");
      try {
        DragomToolInvoker.main(new String[] {"exec-context-manager", "--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help build");
      try {
        DragomToolInvoker.main(new String[] {"help", "build"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help build-remote");
      try {
        DragomToolInvoker.main(new String[] {"help", "build-remote"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help checkout");
      try {
        DragomToolInvoker.main(new String[] {"help", "checkout"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help change-reference-to-module-version");
      try {
        DragomToolInvoker.main(new String[] {"help", "change-reference-to-module-version"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help release");
      try {
        DragomToolInvoker.main(new String[] {"help", "release"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help credential-manager");
      try {
        DragomToolInvoker.main(new String[] {"help", "credential-manager"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help exec-context-manager");
      try {
        DragomToolInvoker.main(new String[] {"help", "exec-context-manager"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help merge-main");
      try {
        DragomToolInvoker.main(new String[] {"help", "merge-main"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help merge-reference-graph");
      try {
        DragomToolInvoker.main(new String[] {"help", "merge-reference-graph"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help reference-graph-report");
      try {
        DragomToolInvoker.main(new String[] {"help", "reference-graph-report"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help root-manager");
      try {
        DragomToolInvoker.main(new String[] {"help", "root-manager"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help setup-jenkins-jobs");
      try {
        DragomToolInvoker.main(new String[] {"help", "setup-jenkins-jobs"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help switch-to-dynamic-version");
      try {
        DragomToolInvoker.main(new String[] {"help", "switch-to-dynamic-version"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("DragomToolInvoker help workspace-manager");
      try {
        DragomToolInvoker.main(new String[] {"help", "workspace-manager"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}