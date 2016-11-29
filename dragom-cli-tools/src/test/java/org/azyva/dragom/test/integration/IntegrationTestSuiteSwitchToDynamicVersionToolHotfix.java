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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.azyva.dragom.tool.GenericRootModuleVersionJobInvokerTool;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;


public class IntegrationTestSuiteSwitchToDynamicVersionToolHotfix {
  /*********************************************************************************
   * Tests SwitchToDynamicVersionTool.
   * <p>
   * Hotfix tests.
   *********************************************************************************/
  public static void testSwitchToDynamicVersionToolHotfix() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt | Hotfix tests");

      IntegrationTestSuite.resetTestWorkspace();

      try {
        pathModel = IntegrationTestSuite.pathTestWorkspace.resolve("basic-model.xml");
        inputStream = IntegrationTestSuite.class.getResourceAsStream("/basic-model.xml");
        Files.copy(inputStream, pathModel, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();

        inputStream = IntegrationTestSuite.class.getResourceAsStream("/test-git-repos.zip");
        zipInputStream = new ZipInputStream(inputStream);

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
          Path path;

          path = IntegrationTestSuite.pathTestWorkspace.resolve(zipEntry.getName());

          if (zipEntry.isDirectory()) {
            path.toFile().mkdirs();
          } else {
            OutputStream outputStream;
            final int chunk = 1024;
            byte[] arrayByteBuffer;
            long size;
            int sizeRead;

            outputStream = new FileOutputStream(path.toFile());
            arrayByteBuffer = new byte[chunk];
            size = zipEntry.getSize();

            while (size > 0) {
              sizeRead = (int)Math.min(chunk,  size);
              sizeRead = zipInputStream.read(arrayByteBuffer, 0, sizeRead);
              outputStream.write(arrayByteBuffer, 0, sizeRead);
              size -= sizeRead;
            }

            outputStream.close();
          }
        }

        zipInputStream.close();
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }

      System.setProperty("org.azyva.dragom.model-property.GIT_REPOS_BASE_URL", "file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos");
      System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());
      System.setProperty("org.azyva.dragom.runtime-property.IND_ECHO_INFO", "true");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "N" to "do you want to continue non-static versions in reference path"
      IntegrationTestSuite.testInputStream.write("N\n");

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.NewDynamicVersionPlugin", "hotfix");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Framework/framework");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Framework/framework"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 1);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:S/v-2000-07-01.01");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:S/v-2000-07-01.01"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "D/hotfix_hotfix1" to "to which version do you want to switch"
      IntegrationTestSuite.testInputStream.write("D/hotfix_hotfix1\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "A" to "do you want to continue create version"
      IntegrationTestSuite.testInputStream.write("A\n");

      // Response "A" to "do you want to continue update parent"
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Framework/framework");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Framework/framework"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:S/v-2000-07-01.01");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:S/v-2000-07-01.01"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "D/hotfix_hotfix2" to "to which version do you want to switch"
      IntegrationTestSuite.testInputStream.write("D/hotfix_hotfix2\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "A" to "do you want to continue create version"
      IntegrationTestSuite.testInputStream.write("A\n");

      // Response "A" to "do you want to continue update parent"
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Framework/framework (user workspace directories)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Framework/framework"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "A" to "do you want to delete".
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:S/v-2000-07-01.01");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:S/v-2000-07-01.01"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "D/hotfix_hotfix3" to "to which version do you want to switch"
      IntegrationTestSuite.testInputStream.write("D/hotfix_hotfix3\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "A" to "do you want to continue create version"
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "A" to "do you want to continue non-static versions in reference path"
      IntegrationTestSuite.testInputStream.write("A\n");

      // Response "D/hotfix_hotfix3" to "to which version do you want to switch"
      IntegrationTestSuite.testInputStream.write("D/hotfix_hotfix3\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "A" to "do you want to continue create version"
      IntegrationTestSuite.testInputStream.write("A\n");

      // Response "A" to "do you want to continue use current hotfix version"
      IntegrationTestSuite.testInputStream.write("A\n");

      // Response "A" to "do you want to continue update parent"
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Framework/framework (user workspace directories)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Framework/framework"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:S/v-2000-07-01.01");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:S/v-2000-07-01.01"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "D/hotfix_hotfix3" to "to which version do you want to switch"
      IntegrationTestSuite.testInputStream.write("D/hotfix_hotfix1\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "A" to "do you want to continue switch version"
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (existing hotfix version)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
