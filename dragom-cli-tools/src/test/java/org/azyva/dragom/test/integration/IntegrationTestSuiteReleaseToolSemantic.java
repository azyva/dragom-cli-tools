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


public class IntegrationTestSuiteReleaseToolSemantic {
  /*********************************************************************************
   * Tests CreateStaticVersionTool.
   * <p>
   * Recursion tests.
   *********************************************************************************/
  public static void testReleaseToolSemantic() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt | Recurse tests");

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
      System.setProperty("org.azyva.dragom.ModuleExistenceCacheFile" , IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/module-existence.properties");
      System.setProperty("org.azyva.dragom.runtime-property.IND_ECHO_INFO", "true");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:D/develop-project1");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:D/develop-project1"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.SelectStaticVersionPlugin", "semantic");
      System.setProperty("org.azyva.dragom.runtime-property.RELEASE_ISOLATION_MODE", "REVERT_ARTIFACT_VERSION");

      // There is no equivalent version since commit attributes not recorded in test
      // repository.

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue update parent"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // There is no equivalent version since commit attributes not recorded in test
      // repository.

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.SelectDynamicVersionPlugin", "uniform");

      // Response "D/develop-project1" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project1\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // There is now an equivalent static version because of above, but we are not
      // using it.

      // Response "N" to "do you want to reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("N\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (expect to use next revision)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

      // Response "D/develop-project2" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project2\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Default response to "from which base version" (D/master)
      IntegrationTestSuite.inputStreamDouble.write("\n");

      // Response "Y" to "do you want to automatically reuse base version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Since base version is D/master and develop-project1 was not merged into master,
      // the reference to app-b-model is not consistent. But it does not matter.
      // Response "A" to "do you want to continue (even if version different)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "MAJOR" to "which version type"
      IntegrationTestSuite.inputStreamDouble.write("MAJOR\n");

      // Response "N" to "reuse version type"
      IntegrationTestSuite.inputStreamDouble.write("N\n");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "I" to "which version type"
      IntegrationTestSuite.inputStreamDouble.write("I\n");

      // Response "Y" to "reuse version type"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (manual MAJOR for app-b-model, manual MINOR for app-b)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

      // Response "D/develop-project3" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project3\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Default response to "from which base version" (D/master)
      IntegrationTestSuite.inputStreamDouble.write("\n");

      // Response "Y" to "do you want to automatically reuse base version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Since base version is D/master and develop-project1 was not merged into master,
      // the reference to app-b-model is not consistent. But it does not matter.
      // Response "A" to "do you want to continue (even if version different)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.Domain2.app-b.NEW_SEMANTIC_VERSION_TYPE", "MAJOR");
      System.setProperty("org.azyva.dragom.runtime-property.Domain2.app-b-model.NEW_SEMANTIC_VERSION_TYPE", "MINOR");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (automatic MINOR for app-b-model, automatic MAJOR for app-b)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();


      // We redo some tests but with the modules checked ou in user workspace
      // directories.

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

      // Response "D/develop-project4" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project4\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Default response to "from which base version" (D/master)
      IntegrationTestSuite.inputStreamDouble.write("\n");

      // Response "Y" to "do you want to automatically reuse base version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Since base version is D/master and develop-project1 was not merged into master,
      // the reference to app-b-model is not consistent. But it does not matter.
      // Response "A" to "do you want to continue (even if version different)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.clearProperty("org.azyva.dragom.runtime-property.Domain2.app-b.NEW_SEMANTIC_VERSION_TYPE");
      System.clearProperty("org.azyva.dragom.runtime-property.Domain2.app-b-model.NEW_SEMANTIC_VERSION_TYPE");

      // Response "MAJOR" to "which version type"
      IntegrationTestSuite.inputStreamDouble.write("MAJOR\n");

      // Response "N" to "reuse version type"
      IntegrationTestSuite.inputStreamDouble.write("N\n");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "I" to "which version type"
      IntegrationTestSuite.inputStreamDouble.write("I\n");

      // Response "Y" to "reuse version type"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (manual MAJOR for app-b-model, manual MINOR for app-b)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

      // Response "D/develop-project5" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project5\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Default response to "from which base version" (D/master)
      IntegrationTestSuite.inputStreamDouble.write("\n");

      // Response "Y" to "do you want to automatically reuse base version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Since base version is D/master and develop-project1 was not merged into master,
      // the reference to app-b-model is not consistent. But it does not matter.
      // Response "A" to "do you want to continue (even if version different)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
      try {
//??? Problem here. Non-fast-forward when fetch. The dragom.log file is bizarre also. Says that fetch is now disabled, but it fetched.
//    ??? there seems to be a problem with main workspace directory concept.
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.Domain2.app-b.NEW_SEMANTIC_VERSION_TYPE", "MAJOR");
      System.setProperty("org.azyva.dragom.runtime-property.Domain2.app-b-model.NEW_SEMANTIC_VERSION_TYPE", "MINOR");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (automatic MINOR for app-b-model, automatic MAJOR for app-b)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

// Test when unsync changes at different levels.