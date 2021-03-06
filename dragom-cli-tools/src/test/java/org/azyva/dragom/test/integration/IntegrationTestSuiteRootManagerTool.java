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

import org.azyva.dragom.tool.RootManagerTool;

public class IntegrationTestSuiteRootManagerTool {
  /*********************************************************************************
   * Tests RootManagerTool.
   *********************************************************************************/
  public static void testRootManagerTool() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("RootManagerTool");

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

      IntegrationTestSuite.printTestHeader("RootManagerTool");
      try {
        RootManagerTool.main(new String[] {});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --help");
      try {
        RootManagerTool.main(new String[] {"--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
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

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace --ind-allow-duplicate-modules add Domain1/app-a:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--ind-allow-duplicate-modules", "add", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app:D/master dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a:D/master", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app:D/master");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a:D/master"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
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

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher ** dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher ** dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher **");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher *");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "*"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher Domain1/app-a");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "/Domain1/app-a"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher *");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "*"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher **");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all-reference-path-matchers dummy");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all-reference-path-matchers", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

// Test the new add-artifact and others based on artifacts.