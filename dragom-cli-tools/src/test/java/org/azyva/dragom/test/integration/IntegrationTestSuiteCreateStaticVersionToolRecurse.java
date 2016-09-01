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


public class IntegrationTestSuiteCreateStaticVersionToolRecurse {
	/*********************************************************************************
	 * Tests CreateStaticVersionTool.
	 * <p>
	 * Recursion tests.
	 *********************************************************************************/
	public static void testCreateStaticVersionToolRecurse() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;

		try {
			IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.CreateStaticVersion CreateStaticVersionToolHelp.txt | Recurse tests");

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

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:D/develop-project1");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:D/develop-project1"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_PLUGIN_ID.org.azyva.dragom.model.plugin.NewStaticVersionPlugin", "uniform");

			// There is no equivalent version since commit attributes not recorded in test
			// repository.

			// Response "S/v-2001-01-01" to "specify prefix"
			IntegrationTestSuite.testInputStream.write("S/v-2001-01-01\n");

			// Response "Y" to "do you want to reuse prefix"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to continue creating static version"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to revert"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to continue update parent"
			IntegrationTestSuite.testInputStream.write("Y\n");

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.CreateStaticVersion CreateStaticVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.CreateStaticVersion", "CreateStaticVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b->/Domain2/app-b-model"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			// There is no equivalent version since commit attributes not recorded in test
			// repository.

			// Response "S/v-2001-01-01" to "specify prefix"
			IntegrationTestSuite.testInputStream.write("S/v-2001-01-01\n");

			// Response "Y" to "do you want to reuse prefix"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to continue creating static version"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to revert"
			IntegrationTestSuite.testInputStream.write("Y\n");

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.CreateStaticVersion CreateStaticVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.CreateStaticVersion", "CreateStaticVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain2/app-b:D/develop-project1");
			try {
				RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain2/app-b:D/develop-project1"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################
??? Need to review this one. When getting develop-project1 again, it has been modified above and now is not multiple level deep (only app-b is on branch).
			// There is now an equivalent static version because of above.

			// Response "N" to "do you want to reuse existing static version"
			IntegrationTestSuite.testInputStream.write("N\n");

			// Response "Y" to "do you want to automatically apply that response always reuse existing static version"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "S/v-2001-01-01" to "specify prefix"
			IntegrationTestSuite.testInputStream.write("S/v-2001-01-01\n");

			// Response "Y" to "do you want to reuse prefix"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to continue creating static version"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to revert"
			IntegrationTestSuite.testInputStream.write("Y\n");

			// Response "Y" to "do you want to continue update parent"
			IntegrationTestSuite.testInputStream.write("Y\n");

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.CreateStaticVersion CreateStaticVersionToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain2/app-b");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.CreateStaticVersion", "CreateStaticVersionToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain2/app-b"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

??? repeat in UWD.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// Some modules evolve, other remain at same version (no increment, reuse existing equivalent)