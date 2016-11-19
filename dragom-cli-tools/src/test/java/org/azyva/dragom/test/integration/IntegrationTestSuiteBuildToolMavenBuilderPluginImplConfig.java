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


public class IntegrationTestSuiteBuildToolMavenBuilderPluginImplConfig {
	/*********************************************************************************
	 * Tests BuildTool.
	 * <p>
	 * MavenBuilderPluginImpl config tests.
	 *********************************************************************************/
	public static void testBuildToolMavenBuilderPluginImplConfig() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;
		Path pathSettings;
		Path pathDragomProperties;

		try {
			IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Build BuildToolHelp.txt | MavenBuilderPluginImpl config tests");

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

				pathSettings = IntegrationTestSuite.pathTestWorkspace.resolve("settings.xml");
				inputStream = IntegrationTestSuite.class.getResourceAsStream("/settings.xml");
				Files.copy(inputStream, pathSettings, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();
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

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Build BuildToolHelp.txt --workspace=workspace --reference-path-matcher=**");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Build", "BuildToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ###############################################################################

			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_HOME", System.getenv("MAVEN_HOME"));
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_LOCAL_REPO", IntegrationTestSuite.pathTestWorkspace.toAbsolutePath().resolve("repository").toString());
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_SETTINGS", IntegrationTestSuite.pathTestWorkspace.toAbsolutePath().resolve("settings.xml").toString());
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_TARGETS", "package install");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_CLEAN_BEFORE_BUILD", "true");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTIES", "prop1,prop2,-prop1");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTY.prop1", "value1");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTY.prop2", "value1");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_PROFILES", "profile1,profile2");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_UPDATE_SNAPSHOTS", "true");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_FAIL_FAST", "true");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_POM_FILE", "pom.xml");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_OFFLINE", "false");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_SHOW_VERSION", "true");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_EXTRA_OPTIONS", "-X");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_IND_WRITE_LOG_TO_FILE", "true");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_LOG_FILE", "build-log");
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_RELATIVE_LOG_FILE_BASE", "MODULE");

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Build BuildToolHelp.txt --workspace=workspace --reference-path-matcher=** (with many runtime properties for the MavenBuilderPluginImpl)");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Build", "BuildToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=**"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ###############################################################################

			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_HOME");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_LOCAL_REPO");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_SETTINGS");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_TARGETS");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_CLEAN_BEFORE_BUILD");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTIES");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTY.prop1");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_PROPERTY.prop2");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_PROFILES");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_UPDATE_SNAPSHOTS");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_FAIL_FAST");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_POM_FILE");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_OFFLINE");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_SHOW_VERSION");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_EXTRA_OPTIONS");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_IND_WRITE_LOG_TO_FILE");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_LOG_FILE");
			System.clearProperty("org.azyva.dragom.runtime-property.MAVEN_RELATIVE_LOG_FILE_BASE");

			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_JDK_HOME.MAVEN_3.3.9", System.getenv("JAVA_HOME"));
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_LOCAL_REPO.MAVEN_3.3.9", IntegrationTestSuite.pathTestWorkspace.toAbsolutePath().resolve("repository").toString());
			System.setProperty("org.azyva.dragom.runtime-property.MAVEN_GLOBAL_SETTINGS.MAVEN_3.3.9", IntegrationTestSuite.pathTestWorkspace.toAbsolutePath().resolve("settings.xml").toString());

			pathDragomProperties = IntegrationTestSuite.pathTestWorkspace.resolve("workspace/app-a/dragom.properties");
			inputStream = IntegrationTestSuite.class.getResourceAsStream("/dragom.properties");
			Files.copy(inputStream, pathDragomProperties, StandardCopyOption.REPLACE_EXISTING);
			inputStream.close();

			IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Build BuildToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a (with dragom.properties file for the MavenBuilderPluginImpl)");
			try {
				GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Build", "BuildToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
