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

import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;


public class IntegrationTestSuiteWorkspaceManagerToolBase {
	/*********************************************************************************
	 * Tests WorkspaceManagerTool.
	 * <p>
	 * Basic tests.
	 *********************************************************************************/
	public static void testWorkspaceManagerToolBase() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;

		try {
			IntegrationTestSuite.printTestCategoryHeader("WorkspaceManagerTool | Basic tests");

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

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool");
			try {
				WorkspaceManagerTool.main(new String[] {});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --help");
			try {
				WorkspaceManagerTool.main(new String[] {"--help"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace status");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "status"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace update dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "update", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace update");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "update"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace commit");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "commit"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace --commit-message=\"dummy\" commit");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--commit-message=dummy", "commit"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-system dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-system", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-system");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-system"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-non-root-reachable dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-non-root-reachable", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-non-root-reachable");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-non-root-reachable"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-non-root-reachable");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-non-root-reachable"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-module-version");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-module-version"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-module-version dummy dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-module-version", "dummy", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-module-version Domain1/app-a");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-module-version", "Domain1/app-a"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-dir");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-dir"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-dir dummy dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-dir", "dummy", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-dir dir");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-dir", "dir"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-all dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-all", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-all");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-all"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-module-version");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-module-version"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-module-version dummy dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-module-version", "Domain1/app-a", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-module-version Domain1/app-a");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-module-version", "Domain1/app-a"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-dir");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-dir"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-dir dummy dummy");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-dir", "dummy", "dummy"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace build-clean-dir dir");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "build-clean-dir", "dir"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace fix");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "fix"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
