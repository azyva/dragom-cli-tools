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


public class IntegrationTestSuiteReleaseToolBase {
  /*********************************************************************************
   * Tests CreateStaticVersionTool.
   * <p>
   * Basic tests.
   *********************************************************************************/
  public static void testReleaseToolBase() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt | Basic tests");

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

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --help");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
      } catch (Exception e) {
        // RuntimeException indicating a cycle detected expected.
        IntegrationTestSuite.validateExitException(e, 1);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.IND_ALLOW_USER_SPECIFIED_PLUGIN_ID.org.azyva.dragom.model.plugin.SelectStaticVersionPlugin", "true");

      // Response "uniform" to "specify plugin ID".
      IntegrationTestSuite.testInputStream.write("uniform\n");

      // Response "Y" to "do you want to reuse plugin ID"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to reuse existing static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a (allow user to specify uniform plugin ID; reuse existing equivalent static version)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.SelectStaticVersionPlugin", "uniform");

      // Response "N" to "do you want to reuse existing static version"
      IntegrationTestSuite.testInputStream.write("N\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "S/v-4.0" to "specify prefix"
      IntegrationTestSuite.testInputStream.write("S/v-4.0\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.testInputStream.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a (uniform plugin ID specified explicitly; use non-existing prefix)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "N" to "do you want to reuse existing static version"
      IntegrationTestSuite.testInputStream.write("N\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "S/v-3.2" to "specify prefix"
      IntegrationTestSuite.testInputStream.write("S/v-3.2\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.testInputStream.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a (use existing prefix)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


/*
cases where equivalent static, and version change commit attributes are not specified (such as for develop-project1).
unsync changes, remote and local

test all ways to specify runtime properties to automate behavior (specific, etc.)
specific prefix and other specific stuff.
build unsuccessful
user workspace directory
*/
Tests with project code