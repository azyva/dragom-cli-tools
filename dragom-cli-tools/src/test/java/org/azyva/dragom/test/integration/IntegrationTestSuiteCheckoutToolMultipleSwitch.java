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

public class IntegrationTestSuiteCheckoutToolMultipleSwitch {
  /*********************************************************************************
   *********************************************************************************
   * Tests CheckoutTool.
   * <p>
   * Tests with multiple ModuleVersion's, switch tests.
   *********************************************************************************
   *********************************************************************************/
  public static void testCheckoutToolMultipleSwitch() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt | Multiple switch tests");

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

      System.setProperty("org.azyva.dragom.init-property.GIT_REPOS_BASE_URL", "file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos");
      System.setProperty("org.azyva.dragom.init-property.URL_MODEL" , pathModel.toUri().toString());
      System.setProperty("org.azyva.dragom.init-property.MODULE_EXISTENCE_CACHE_FILE" , IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/module-existence.properties");
      System.setProperty("org.azyva.dragom.init-property.IND_ECHO_INFO", "true");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain2/app-b --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain2/app-b", "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "Y" to "do you want to switch".
      IntegrationTestSuite.inputStreamDouble.write("Y\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain2/app-b:D/develop-project1 --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain2/app-b:D/develop-project1", "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("Append to workspace/app-b-model/pom.xml\n");

      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-b-model/pom.xml"), "<!-- Dummy comment. -->\n");
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // Default response to "do you want to continue" (Y).
      IntegrationTestSuite.inputStreamDouble.write("\n");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain2/app-b:D/master --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain2/app-b:D/master", "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("[workspace/app-b-model] git add, git commit");
      try {
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-b-model"), "Dummy message.", null);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response NA to "do you want to switch".
      IntegrationTestSuite.inputStreamDouble.write("NA\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain2/app-b:D/master --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain2/app-b:D/master", "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader(
          "[workspace/app-b-model] git push\n" +
          "git clone test-git-repos/Domain2/app-b-model.git app-b-model.ext\n" +
          "Append to app-b-model.ext/pom.xml\n" +
          "git add, git commit, git push");

      try {
        IntegrationTestSuite.getGit().push(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-b-model"));
        IntegrationTestSuite.getGit().clone("file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos/Domain2/app-b-model.git", new Version("D/master"), IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext"));
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext/pom.xml"), "<!-- Dummy comment 2. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext"), "Dummy message.", null);
        IntegrationTestSuite.getGit().push(IntegrationTestSuite.pathTestWorkspace.resolve("app-b-model.ext"));
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Default response to "do you want to switch" (YA).
      IntegrationTestSuite.inputStreamDouble.write("\n");

      IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain2/app-b:D/master --reference-path-matcher=**");
      try {
        GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain2/app-b:D/master", "--reference-path-matcher=**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
