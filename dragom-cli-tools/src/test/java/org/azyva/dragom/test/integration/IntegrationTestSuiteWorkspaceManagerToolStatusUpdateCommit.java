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

import org.azyva.dragom.git.Git;
import org.azyva.dragom.model.Version;
import org.azyva.dragom.tool.GenericRootModuleVersionJobInvokerTool;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;


public class IntegrationTestSuiteWorkspaceManagerToolStatusUpdateCommit {
  /*********************************************************************************
   * Tests WorkspaceManagerTool.
   * <p>
   * Status, update and commit tests.
   *********************************************************************************/
  public static void testWorkspaceManagerToolStatusUpdateCommit() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("WorkspaceManagerTool | Status, update and commmit tests");

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

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/develop-project1");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/develop-project1"});
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

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("[workspace/app-a] git add, git commit (no push)");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a"), "Dummy message.", null, false);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("[workspace/app-a] git push");
      try {
        IntegrationTestSuite.getGit().push(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a"));
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader(
          "git clone --branch develop-project1 test-git-repos/Domain1/app-a.git app-a.ext\n" +
          "Append to app-a.ext/pom.xml\n" +
          "git add, git commit, git push");
      try {
        IntegrationTestSuite.getGit().clone("file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos/Domain1/app-a.git", new Version("D/develop-project1"), IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext"));
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext/pom.xml"), "<!-- Dummy comment. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext"), "Dummy message.", null, true);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace update");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "update"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("git reset --hard HEAD [workspace/app-a]");
      try {
        IntegrationTestSuite.getGit().executeGitCommand(new String[] {"reset", "--hard", "HEAD"}, false, Git.AllowExitCode.NONE, IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a"), null);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace update");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "update"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "Commit message." to "specify commit message".
      IntegrationTestSuite.testInputStream.write("Commit message.\n");

      // Response "Y" to "do you want to reuse".
      IntegrationTestSuite.testInputStream.write("Y\n");

      // Response "N" to "do you want to commit".
      IntegrationTestSuite.testInputStream.write("N\n");

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Default response to "do you want to commit" (Y).
      IntegrationTestSuite.testInputStream.write("\n");

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace --commit-message=\"Commit message.\" commit");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--commit-message=Commit message.", "commit"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader(
          "Append to workspace/app-a/pom.xml\n" +
          "git add, git commit (no push)");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment 2. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a"), "Dummy message.", null, false);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.runtime-property.GIT_IND_PUSH_ALL" , "true");

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status (with GIT_IND_PUSH_ALL=true)");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      System.clearProperty("org.azyva.dragom.runtime-property.GIT_IND_PUSH_ALL");

      // ###############################################################################

      IntegrationTestSuite.printTestHeader(
          "[app-a.ext] git pull\n" +
          "Append to app-a.ext/pom.xml\n" +
          "git add, git commit, git push");

      try {
        IntegrationTestSuite.getGit().pull(IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext"));
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext/pom.xml"), "<!-- Dummy comment 3. -->\n");
        IntegrationTestSuite.getGit().addCommit(IntegrationTestSuite.pathTestWorkspace.resolve("app-a.ext"), "Dummy message.", null, true);
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
      try {
        IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment 4. -->\n");
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      // Response "A" to "do you want to continue".
      IntegrationTestSuite.testInputStream.write("A\n");

      IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit");
      try {
        WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
