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

import org.azyva.dragom.tool.CredentialManagerTool;

public class IntegrationTestSuiteCredentialManagerTool {
  /*********************************************************************************
   * Tests RootManagerTool.
   *********************************************************************************/
  public static void testCredentialManagerTool() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("CredentialManagerTool");

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
      System.setProperty("org.azyva.dragom.runtime-property.IND_PASSWORD_INPUT_NORMAL", "true");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool");
      try {
        CredentialManagerTool.main(new String[] {});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --help");
      try {
        CredentialManagerTool.main(new String[] {"--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace enum-resource-realm-mappings dummy");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "enum-resource-realm-mappings", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace enum-resource-realm-mappings");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "enum-resource-realm-mappings"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace enum-passwords");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "enum-passwords"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://jsmith@acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://jsmith@acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://jsmith@acme.com/git jsmith");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://jsmith@acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://jsmith@acme.com/git dummy");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://jsmith@acme.com/git", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "password" to "input password"
      IntegrationTestSuite.inputStreamDouble.write("password\n");

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-password http://jsmith@acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-password", "http://jsmith@acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://jsmith@acme.com/git (with password available)");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://jsmith@acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://jsmith@acme.com/git jsmith (with password available)");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://jsmith@acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "password" to "input password"
      IntegrationTestSuite.inputStreamDouble.write("password\n");

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-password http://jsmith@acme.com/git jsmith");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-password", "http://jsmith@acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-password http://jsmith@acme.com/git dummy");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-password", "http://jsmith@acme.com/git", "dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://acme.com/git jsmith");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://acme.com/git bob");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://acme.com/git", "bob"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "password" to "input password"
      IntegrationTestSuite.inputStreamDouble.write("password\n");

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-password http://acme.com/git jsmith");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-password", "http://acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "jsmith" to "which user"
      IntegrationTestSuite.inputStreamDouble.write("jsmith\n");

      // Response "password" to "input password"
      IntegrationTestSuite.inputStreamDouble.write("password\n");

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-password http://acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-password", "http://acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-password http://acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-password", "http://acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace enum-passwords");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "enum-passwords"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace enum-default-users");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "enum-default-users"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-default-user http://acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-default-user", "http://acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace get-default-user http://bob@acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-default-user", "http://bob@acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-default-user http://acme.com/git bob");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-default-user", "http://acme.com/git", "bob"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-default-user http://bob@acme.com/git bob");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-default-user", "http://bob@acme.com/git", "bob"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace set-default-user http://bob@acme.com/git jsmith");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-default-user", "http://bob@acme.com/git", "jsmith"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace remove-default-user http://bob@acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-default-user", "http://bob@acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("CredentialManagerTool --workspace=workspace remove-default-user http://acme.com/git");
      try {
        CredentialManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-default-user", "http://acme.com/git"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

// how to test isCredentialsExist if ever worth it?
// credential validation is not tested either.