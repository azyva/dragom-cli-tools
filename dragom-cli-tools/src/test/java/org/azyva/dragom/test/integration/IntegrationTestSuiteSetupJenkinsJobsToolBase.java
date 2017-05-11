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

import org.azyva.dragom.test.integration.setupjenkinsjobs.JenkinsClientTestDouble;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.SetupJenkinsJobsTool;


public class IntegrationTestSuiteSetupJenkinsJobsToolBase {
  /*********************************************************************************
   * Tests SetupJenkinsJobsTool.
   * <p>
   * Basic tests.
   *********************************************************************************/
  public static void testSetupJenkinsJobsToolBase() {
    Path pathModel;
    InputStream inputStream;
    ZipInputStream zipInputStream;
    ZipEntry zipEntry;

    try {
      IntegrationTestSuite.printTestCategoryHeader("ReferenceGraphReportTool | Basic tests");

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
      System.setProperty("org.azyva.dragom.init-property.IND_PASSWORD_INPUT_NORMAL", "true");
      System.setProperty("org.azyva.dragom.init-property.MASTER_KEY_FILE", IntegrationTestSuite.pathTestWorkspace.resolve("master-key").toString());
      System.setProperty("org.azyva.dragom.DefaultServiceImpl.org.azyva.dragom.jenkins.JenkinsClient", "org.azyva.dragom.test.integration.setupjenkinsjobs.JenkinsClientTestDouble");


      // ###############################################################################

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --help");
      try {
        SetupJenkinsJobsTool.main(new String[] {"--help"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool dummy");
      try {
        SetupJenkinsJobsTool.main(new String[] {"dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.exception = e;
      }
      IntegrationTestSuite.validateExitException(IntegrationTestSuite.exception, 1);
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:D/develop-project1");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:D/develop-project1"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();


      // First set of tests without creating a project folder for the jobs.

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_USER", "correct-user");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      JenkinsClientTestDouble.createInitialFolder("build/ci/projects");

      // Response "incorrect-password" to "enter password"
      IntegrationTestSuite.inputStreamDouble.write("incorrect-password\n");

      // Response "correct-password" to "enter password"
      IntegrationTestSuite.inputStreamDouble.write("correct-password\n");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --reference-path-matcher=/Domain2/app-b:D/develop-project1");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b:D/develop-project1"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --reference-path-matcher=**->/:(D/.*)");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**->/:(D/.*)"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --reference-path-matcher=**->/:(S/.*)");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**->/:(S/.*)"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --items-created-file-mode=REPLACE --reference-path-matcher=/dummy");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--items-created-file-mode", "REPLACE", "--reference-path-matcher=/dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();


      // Second set of tests without creating a project folder for the jobs.

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test/");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --reference-path-matcher=**->/:(D/.*)");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**->/:(D/.*)"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test/");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      JenkinsClientTestDouble.createInitialJob("build/ci/projects/dragom-test/sticky-job");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --items-created-file-mode=REPLACE_DELETE_FOLDER_ONLY_IF_EMPTY --reference-path-matcher=/dummy");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--items-created-file-mode", "REPLACE_DELETE_FOLDER_ONLY_IF_EMPTY", "--reference-path-matcher=/dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test/");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --items-created-file-mode=REPLACE_NO_DELETE_FOLDER --reference-path-matcher=/dummy");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--items-created-file-mode", "REPLACE_NO_DELETE_FOLDER", "--reference-path-matcher=/dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      System.setProperty("org.azyva.dragom.init-property.JENKINS_PROJECT", "dragom-test/");
      System.setProperty("org.azyva.dragom.init-property.JENKINS_IND_INCLUDE_VERSION", "true");

      IntegrationTestSuite.printTestHeader("SetupJenkinsJobsTool --workspace=workspace --items-created-file-mode=REPLACE --reference-path-matcher=/dummy");

      System.out.println("Jenkins contents before:");
      JenkinsClientTestDouble.printContents();

      try {
        SetupJenkinsJobsTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--items-created-file-mode", "REPLACE", "--reference-path-matcher=/dummy"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }

      System.out.println("Jenkins contents after:");
      JenkinsClientTestDouble.printContents();

      IntegrationTestSuite.printTestFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
