/*
 * Copyright 2015 AZYVA INC.
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


public class IntegrationTestSuiteReferenceGraphReportToolBase {
	/*********************************************************************************
	 * Tests ReferenceGraphReportTool.
	 * <p>
	 * Basic tests.
	 *********************************************************************************/
	public static void testReferenceGraphReportToolBase() {
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

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool");
			try {
				ReferenceGraphReportTool.main(new String[] {});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --help");
			try {
				ReferenceGraphReportTool.main(new String[] {"--help"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool dummy1 dummy2");
			try {
				ReferenceGraphReportTool.main(new String[] {"dummy1", "dummy2"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --module-versions --avoid-redundancy dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--module-versions", "--avoid-redundancy", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --graph --only-multiple-versions dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--graph", "--only-multiple-versions", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --graph --only-matched-modules dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--graph", "--only-matched-modules", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --graph --most-recent-version-in-reference-graph dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--graph", "--most-recent-version-in-reference-graph", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --graph --most-recent-static-version-in-scm dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--graph", "--most-recent-static-version-in-scm", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --graph --reference-paths dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--graph", "--reference-paths", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --module-versions --only-multiple-versions --only-matched-modules dummy");
			try {
				ReferenceGraphReportTool.main(new String[] {"--module-versions", "--only-multiple-versions", "--only-matched-modules", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/develop-project1");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/develop-project1"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ReferenceGraphReportTool --workspace=workspace --reference-path-matcher=** --graph reference-graph-report.txt");
			try {
				ReferenceGraphReportTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**", "--graph", IntegrationTestSuite.pathTestWorkspace.resolve("reference-graph-report.txt").toString()});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
