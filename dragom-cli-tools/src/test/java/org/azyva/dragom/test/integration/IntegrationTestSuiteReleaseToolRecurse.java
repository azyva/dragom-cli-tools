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

import org.azyva.dragom.model.Version;
import org.azyva.dragom.tool.GenericRootModuleVersionJobInvokerTool;
import org.azyva.dragom.tool.RootManagerTool;


public class IntegrationTestSuiteReleaseToolRecurse {
  /*********************************************************************************
   * Tests CreateStaticVersionTool.
   * <p>
   * Recursion tests.
   *********************************************************************************/
  public static void testReleaseToolRecurse() {
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

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.SelectStaticVersionPlugin", "uniform");

      // There is no equivalent version since commit attributes not recorded in test
      // repository.

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to revert"
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

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to revert"
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

      System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.NewDynamicVersionPlugin", "uniform");

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

      // There is now an equivalent static version because of above.

      // Response "Y" to "do you want to reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (reuse existing static versions)");
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

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue update parent"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (not reusing existing static versions)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();


      // We redo all tests but with the modules checked ou in user workspace
      // directories.

      // ###############################################################################

      // We introduce dummy changes so that make the existing static versions not
      // equivalent.

      IntegrationTestSuite.printTestHeader(
          "git clone test-git-repos/Domain2/app-b.git app-b.ext (branch develop-project1)\n" +
          "Append to app-b.ext/pom.xml\n" +
          "git add, git commit, git push");
      try {
        IntegrationTestSuite.getGit().clone("file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos/Domain2/app-b.git", new Version("D/develop-project1"), IntegrationTestSuite.pathTestWorkspace.resolve("app-b.ext"));
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("app-b.ext/pom.xml"), "<!-- Dummy comment. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("app-b.ext"), "Dummy message.", null, true);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader(
          "git clone test-git-repos/Domain2/app-b-model.git app-b-model.ext (branch develop-project1)\n" +
          "Append to app-b-model.ext/pom.xml\n" +
          "git add, git commit, git push");
      try {
        IntegrationTestSuite.getGit().clone("file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos/Domain2/app-b-model.git", new Version("D/develop-project1"), IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext"));
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext/pom.xml"), "<!-- Dummy comment. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext"), "Dummy message.", null, true);
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

      // We are not testing SwitchToDynamicVersion, but we are using this tool to put
      // back Domain2/app-b-model in development within the graph since it was released
      // above.

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

      // There is no equivalent version since commit attributes not recorded in test
      // repository.

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue update parent"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model (user workspace directories)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Release", "ReleaseToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // There is no equivalent version since commit attributes not recorded in test
      // repository.

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (user workspace directories)");
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

      // Response "D/develop-project1" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project1\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model (user workspace directories)");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.SwitchToDynamicVersion", "SwitchToDynamicVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // There is now an equivalent static version because of above.

      // Response "Y" to "do you want to reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to automatically apply that response always reuse existing static version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (reuse existing static versions; user workspace directories)");
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

      // Response "D/develop-project1" to "to which version do you want to switch"
      IntegrationTestSuite.inputStreamDouble.write("D/develop-project1\n");

      // Response "Y" to "do you want to automatically reuse dynamic version"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue (switching)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "A" to "do you want to continue (updating parent)"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.SwitchToDynamicVersion SwitchToDynamicVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model (user workspace directories)");
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

      // Response "S/v-2001-01-01" to "specify prefix"
      IntegrationTestSuite.inputStreamDouble.write("S/v-2001-01-01\n");

      // Response "Y" to "do you want to reuse prefix"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "A" to "do you want to continue creating static version"
      IntegrationTestSuite.inputStreamDouble.write("A\n");

      // Response "Y" to "do you want to revert"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      // Response "Y" to "do you want to continue update parent"
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Release ReleaseToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b (not reusing existing static versions; user workspace directories)");
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

// Some modules evolve, other remain at same version (no increment, reuse existing equivalent)
// Test when unsync changes at different levels.