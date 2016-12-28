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

import org.azyva.dragom.tool.ReferenceGraphReportTool;
import org.azyva.dragom.tool.RootManagerTool;


public class IntegrationTestSuiteReferenceGraphReportToolReport {
  /*********************************************************************************
   * Tests ReferenceGraphReportTool.
   * <p>
   * Report tests.
   *********************************************************************************/
  public static void testReferenceGraphReportToolReport() {
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

      System.setProperty("org.azyva.dragom.model-property.GIT_REPOS_BASE_URL", "file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos");
      System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());
      System.setProperty("org.azyva.dragom.ModuleExistenceCacheFile" , IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/module-existence.properties");
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

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph reference-graph-report.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT reference-graph-report.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=XML reference-graph-report.xml");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=XML", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report.xml").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=JSON reference-graph-report.json");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=JSON", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report.json").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --reference-paths reference-graph-report-modules.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-modules.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=XML --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --reference-paths reference-graph-report-modules.xml");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=XML", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-modules.xml").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=JSON --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --reference-paths reference-graph-report-modules.json");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=JSON", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-modules.json").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --only-multiple-versions --reference-paths reference-graph-report-modules-only-multiple-versions.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--only-multiple-versions", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-modules-only-multiple-versions.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --only-matched-modules --reference-paths reference-graph-report-modules-only-matched-modules.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--only-matched-modules", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-modules-only-matched-modules.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:D/develop-project1");
      try {
        RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:D/develop-project1"});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT --module-versions --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --reference-paths reference-graph-report-multiple-modules.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-multiple-modules.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=TEXT --module-versions --avoid-redundancy --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --only-multiple-versions --reference-paths reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.txt");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=TEXT", "--avoid-redundancy", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--only-multiple-versions", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.txt").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=XML --module-versions --avoid-redundancy --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --only-multiple-versions --reference-paths reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.xml");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=XML", "--avoid-redundancy", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--only-multiple-versions", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.xml").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

      // ###############################################################################

      IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph --output-format=JSON --module-versions --avoid-redundancy --most-recent-version-in-reference-graph --most-recent-static-version-in-scm --only-multiple-versions --reference-paths reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.json");
      try {
        ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", "--output-format=JSON", "--avoid-redundancy", "--module-versions", "--most-recent-version-in-reference-graph", "--most-recent-static-version-in-scm", "--only-multiple-versions", "--reference-paths", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report-multiple-avoid-redundancy-modules-only-multiple-versions.json").toString()});
      } catch (Exception e) {
        IntegrationTestSuite.validateExitException(e, 0);
      }
      IntegrationTestSuite.printTestFooter();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
