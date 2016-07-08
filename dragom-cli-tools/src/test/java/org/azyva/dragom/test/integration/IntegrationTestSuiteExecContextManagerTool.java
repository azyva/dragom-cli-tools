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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.azyva.dragom.tool.ExecContextManagerTool;

public class IntegrationTestSuiteExecContextManagerTool {
	/*********************************************************************************
	 * Tests ExecContextManagerTool.
	 *********************************************************************************/
	public static void testExecContextManagerTool() {
		InputStream inputStream;
		Path pathUserProperties;
		Path pathToolProperties;
		Path pathModel;

		try {
			IntegrationTestSuite.printTestCategoryHeader("ExecContextManagerTool");

			IntegrationTestSuite.resetTestWorkspace();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool");
			try {
				ExecContextManagerTool.main(new String[] {});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --help");
			try {
				ExecContextManagerTool.main(new String[] {"--help"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool release (with UrlModel not set)");
			try {
				ExecContextManagerTool.main(new String[] {"release"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --user-properties=dummy-user.properties release (with dummy UrlModel)");
			try {
				inputStream = IntegrationTestSuite.class.getResourceAsStream("/dummy-user.properties");
				pathUserProperties = IntegrationTestSuite.pathTestWorkspace.resolve("dummy-user.properties");
				Files.copy(inputStream, pathUserProperties, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();

				ExecContextManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "release"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();


			// ################################################################################

			try {
				inputStream = IntegrationTestSuite.class.getResourceAsStream("/simple-user.properties");
				pathUserProperties = IntegrationTestSuite.pathTestWorkspace.resolve("simple-user.properties");
				Files.copy(inputStream, pathUserProperties, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();

				inputStream = IntegrationTestSuite.class.getResourceAsStream("/simple-tool.properties");
				pathToolProperties = IntegrationTestSuite.pathTestWorkspace.resolve("simple-tool.properties");
				Files.copy(inputStream, pathToolProperties, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();

				pathModel = IntegrationTestSuite.pathTestWorkspace.resolve("simple-model.xml");
				inputStream = IntegrationTestSuite.class.getResourceAsStream("/simple-model.xml");
				Files.copy(inputStream, pathModel, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace force-unlock");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "force-unlock"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace set-property NAME");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace set-property NAME VALUE extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME", "VALUE", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace set-property NAME VALUE");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME", "VALUE"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties (with IndIgnoreCachedExecContext and IndIgnoreCachedModel)");
			try {
				System.setProperty("org.azyva.dragom.IndIgnoreCachedExecContext" , "true");
				System.setProperty("org.azyva.dragom.IndIgnoreCachedModel" , "true");
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
				System.getProperties().remove("org.azyva.dragom.IndIgnoreCachedExecContext");
				System.getProperties().remove("org.azyva.dragom.IndIgnoreCachedModel");
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties NA extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NA", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties NA");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NA"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-properties NB");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NB"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-property");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-property NAME extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NAME", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-property NB");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NB"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace get-property NAME");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NAME"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-property");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-property NAME extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property", "NAME", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-property NA");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property", "NA"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-properties NA extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NA", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-properties NB");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NB"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace remove-properties NA");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NA"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace set-properties-from-tool-properties extra");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-properties-from-tool-properties", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace set-properties-from-tool-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-properties-from-tool-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace -DNAME=VALUE set-properties-from-tool-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "set-properties-from-tool-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace -DNAME=VALUE -DNAME2=VALUE2 set-properties-from-tool-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "-DNAME2=VALUE2", "set-properties-from-tool-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace -DNAME=VALUE --tool-properties=simple-tool.properties set-properties-from-tool-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "--tool-properties=" + pathToolProperties, "set-properties-from-tool-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property");
			try {
				ExecContextManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NAME extra");
			try {
				ExecContextManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NAME", "extra"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 1);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NB");
			try {
				ExecContextManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NB"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NAME4");
			try {
				ExecContextManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NAME4"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace --no-confirm get-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--no-confirm", "get-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("ExecContextManagerTool --workspace=workspace --no-confirm-context=A --no-confirm-context=B get-properties");
			try {
				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--no-confirm-context=A", "--no-confirm-context=B", "get-properties"});
			} catch (Exception e) {
				IntegrationTestSuite.validateExitException(e, 0);
			}
			IntegrationTestSuite.printTestFooter();

			System.getProperties().remove("org.azyva.dragom.UrlModel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
