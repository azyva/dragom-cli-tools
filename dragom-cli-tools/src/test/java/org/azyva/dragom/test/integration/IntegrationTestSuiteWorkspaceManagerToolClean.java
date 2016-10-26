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

import org.azyva.dragom.tool.GenericRootModuleVersionJobInvokerTool;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;


public class IntegrationTestSuiteWorkspaceManagerToolClean {
	/*********************************************************************************
	 * Tests WorkspaceManagerTool.
	 * <p>
	 * Clean tests.
	 *********************************************************************************/
	public static void testWorkspaceManagerToolClean() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;

		try {
			IntegrationTestSuite.printTestCategoryHeader("WorkspaceManagerTool | Clean tests");

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

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/develop-project1");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/develop-project1"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "B" (abort) to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("B\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "Y" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "N" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("N\n");

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
			try {
				IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			// Response "A" to "do you want to delete with unsync changes".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-all");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-all"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
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

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-non-root-reachable");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-non-root-reachable"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Append to workspace/app-a/pom.xml");
			try {
				IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/pom.xml"), "<!-- Dummy comment. -->\n");
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
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

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			// Response "A" to "do you want to delete with unsync changes".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace clean-non-root-reachable");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "clean-non-root-reachable"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-module-version Domain1/app-a:D/develop-project1");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-module-version", "Domain1/app-a:D/develop-project1"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Append to workspace/app-a-model-intf/pom.xml");
			try {
				IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a-model-intf/pom.xml"), "<!-- Dummy comment. -->\n");
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete with unsync changes".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-module-version Domain1/app-a-model-intf");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-module-version", "Domain1/app-a-model-intf"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-dir app-a");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-dir", "app-a"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Append to workspace/app-a-model-intf/pom.xml");
			try {
				IntegrationTestSuite.appendToFile(IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a-model-intf/pom.xml"), "<!-- Dummy comment. -->\n");
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// Response "A" to "do you want to delete with unsync changes".
			IntegrationTestSuite.testInputStream.write("A\n");

			IntegrationTestSuite.printTestHeader("WorkspaceManagerTool --workspace=workspace remove-dir app-a-model-intf");
			try {
				WorkspaceManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-dir", "app-a-model-intf"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
