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

package org.azyva.dragom.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.azyva.dragom.tool.DragomToolInvoker;
import org.azyva.dragom.tool.ExecContextManagerTool;
import org.azyva.dragom.tool.GenericRootModuleVersionJobInvokerTool;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;

public class IntegrationTestSuite {
	private static Path pathTestWorkspace;

	public static void main(String[] args) {
		InputStream inputStreamLoggingProperties;
		Path pathLoggingProperties;
		Set<String> setTestCategory;
		boolean indAllTests;

		System.setSecurityManager(new NoExitSecurityManager());

		EclipseSynchronizeErrOut.fix();

		if (args.length == 0) {
			IntegrationTestSuite.pathTestWorkspace = Paths.get(System.getProperty("user.dir")).resolve("test-workspace");
			System.out.println("Test workspace directory not specified. Using \"test-workspace\" subdirectory of current directory " + IntegrationTestSuite.pathTestWorkspace + '.');
		} else {
			IntegrationTestSuite.pathTestWorkspace = Paths.get(args[0]);
			System.out.println("Using specified test workspace directory " + IntegrationTestSuite.pathTestWorkspace + '.');

			args = Arrays.copyOfRange(args, 1, args.length);
		}

		setTestCategory = new HashSet<String>(Arrays.asList(args));
		indAllTests = setTestCategory.contains("all");

		if (indAllTests || setTestCategory.contains("DragomToolInvoker")) {
			IntegrationTestSuite.testDragomToolInvoker();
		}

		if (indAllTests || setTestCategory.contains("ExecContextManagerTool")) {
			IntegrationTestSuite.testExecContextManagerTool();
		}

		if (indAllTests || setTestCategory.contains("RootManagerTool")) {
			IntegrationTestSuite.testRootManagerTool();
		}

		if (indAllTests || setTestCategory.contains("CheckoutToolBase")) {
			IntegrationTestSuite.testCheckoutToolBase();
		}

		if (indAllTests || setTestCategory.contains("WorkspaceManagerTool")) {
			IntegrationTestSuite.testWorkspaceManagerTool();
		}
	}

	/*********************************************************************************
	 *********************************************************************************
	 * Tests DragomToolInvoker.
	 *********************************************************************************
	 *********************************************************************************/
	private static void testDragomToolInvoker() {
		IntegrationTestSuite.printTestCategoryHeader("DragomToolInvoker");

		IntegrationTestSuite.resetTestWorkspace();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker");
		try {
			DragomToolInvoker.main(new String[] {});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker --help");
		try {
			DragomToolInvoker.main(new String[] {"--help"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker dummy");
		try {
			DragomToolInvoker.main(new String[] {"dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker exec-context-property-manager");
		try {
			DragomToolInvoker.main(new String[] {"exec-context-property-manager"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker exec-context-property-manager --help");
		try {
			DragomToolInvoker.main(new String[] {"exec-context-property-manager", "--help"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help build");
		try {
			DragomToolInvoker.main(new String[] {"help", "build"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help checkout");
		try {
			DragomToolInvoker.main(new String[] {"help", "checkout"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help change-reference-to-module-version");
		try {
			DragomToolInvoker.main(new String[] {"help", "change-reference-to-module-version"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help create-static-version");
		try {
			DragomToolInvoker.main(new String[] {"help", "create-static-version"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help exec-context-property-manager");
		try {
			DragomToolInvoker.main(new String[] {"help", "exec-context-property-manager"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help merge-main");
		try {
			DragomToolInvoker.main(new String[] {"help", "merge-main"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help merge-reference-graph");
		try {
			DragomToolInvoker.main(new String[] {"help", "merge-reference-graph"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help reference-graph-report");
		try {
			DragomToolInvoker.main(new String[] {"help", "reference-graph-report"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help root-manager");
		try {
			DragomToolInvoker.main(new String[] {"help", "root-manager"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help switch-to-dynamic-version");
		try {
			DragomToolInvoker.main(new String[] {"help", "switch-to-dynamic-version"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("DragomToolInvoker help workspace-manager");
		try {
			DragomToolInvoker.main(new String[] {"help", "workspace-manager"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();
	}

	/*********************************************************************************
	 *********************************************************************************
	 * Tests ExecContextManagerTool.
	 *********************************************************************************
	 *********************************************************************************/
	private static void testExecContextManagerTool() {
		InputStream inputStream;
		Path pathUserProperties;
		Path pathToolProperties;
		Path pathModel;

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
	}

	/*********************************************************************************
	 *********************************************************************************
	 * Tests RootManagerTool.
	 *********************************************************************************
	 *********************************************************************************/
	private static void testRootManagerTool() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;

		IntegrationTestSuite.printTestCategoryHeader("RootManagerTool");

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

		System.setProperty("model-property.GIT_REPOS_BASE_URL", "file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos");
		System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool");
		try {
			RootManagerTool.main(new String[] {});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --help");
		try {
			RootManagerTool.main(new String[] {"--help"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a", "dummy"});
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

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add Domain1/app-a:D/master");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "Domain1/app-a:D/master"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace --ind-allow-duplicate-modules add Domain1/app-a");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--ind-allow-duplicate-modules", "add", "Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove Domain1/app:D/master");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove", "Domain1/app-a:D/master"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
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

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher ** dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher ** dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher **");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher *");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "*"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher **");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "**"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace add-reference-path-matcher Domain1/app-a");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "add-reference-path-matcher", "/Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher *");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "*"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-reference-path-matcher **");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-reference-path-matcher", "**"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all-reference-path-matchers dummy");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all-reference-path-matchers", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace remove-all-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-all-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("RootManagerTool --workspace=workspace list-reference-path-matchers");
		try {
			RootManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "list-reference-path-matchers"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();
	}

	/*********************************************************************************
	 *********************************************************************************
	 * Tests CheckoutTool.
	 * <p>
	 * Basic tests.
	 *********************************************************************************
	 *********************************************************************************/
	private static void testCheckoutToolBase() {
		Path pathModel;
		InputStream inputStream;
		ZipInputStream zipInputStream;
		ZipEntry zipEntry;

		IntegrationTestSuite.printTestCategoryHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt");

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

		System.setProperty("model-property.GIT_REPOS_BASE_URL", "file:///" + IntegrationTestSuite.pathTestWorkspace.toAbsolutePath() + "/test-git-repos");
		System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());

		// CheckoutTool does not have regular arguments so the test of not passing any
		// argument and expecting a corresponding message is not pertinent.

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt dummy");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "dummy"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --help");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--help"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace")});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain1/app-a");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain1/app-a --reference-path-matcher=/Domain1/app-a");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain1/app-a", "--reference-path-matcher=/Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
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

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace")});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --root-module-version=Domain1/app-a");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--root-module-version=Domain1/app-b"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("GenericRootModuleVersionJobInvokerTool org.azyva.dragom.job.Checkout CheckoutToolHelp.txt --workspace=workspace --reference-path-matcher=/Domain1/app-a");
		try {
			GenericRootModuleVersionJobInvokerTool.main(new String[] {"org.azyva.dragom.job.Checkout", "CheckoutToolHelp.txt", "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--reference-path-matcher=/Domain1/app-a"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

	}

	/*********************************************************************************
	 *********************************************************************************
	 * Tests WorkspaceManagerTool.
	 *********************************************************************************
	 *********************************************************************************/
	private static void testWorkspaceManagerTool() {
		IntegrationTestSuite.printTestCategoryHeader("WorkspaceManagerTool");

		IntegrationTestSuite.resetTestWorkspace();

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

	}








	private static void testWorkspaceManagerBase() {
//		try { WorkspaceManagerTool.main(new String[] {"--help"}); } catch (ExitException ee) {}
		try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "status"}); } catch (ExitException ee) {}
		try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "update"}); } catch (ExitException ee) {}
//		try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "commit"}); } catch (ExitException ee) {}
//		try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--commit-message=Commit message", "commit"}); } catch (ExitException ee) {}
//		try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "force-unlock"}); } catch (ExitException ee) {}
	}

	private static void testRootManagerBase() {
		try { RootManagerTool.main(new String[] {"--help"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain2/app-b:D/master"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a:D/master"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--allow-duplicate-modules", "add", "Domain1/app-a"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-all"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list-reference-path-matchers"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "**"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "**->*"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/Domain1/app-a"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/Domain1/app-a->**"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/Domain1/app-a:D/master"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/(Domain?/app.*):(D/.*)"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1->**"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1:app-a"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1:app-a:master"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1:app-a:master-SNAPSHOT"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "com.acme.domain1:app-a:master-SNAPSHOT"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/Domain1/app-a:D/master->**->com.acme.domain1:app-a:master-SNAPSHOT->*"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list-reference-path-matchers"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-reference-path-matcher", "**"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list-reference-path-matchers"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-reference-path-matcher", "/Domain1/app-a:D/master->**->com.acme.domain1:app-a:master-SNAPSHOT->*"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list-reference-path-matchers"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-reference-path-matcher", "/(Domain?/app.*):(D/.*)"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "list-reference-path-matchers"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-reference-path-matcher", "/dummy"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "remove-all-reference-path-matchers"}); } catch (ExitException ee) {}
	}

	private static void testCheckout() {
//		try { TaskInvokerTool.main(new String[] {"checkout", "checkout", "CheckoutToolHelp.txt", "--help"}); } catch (ExitException ee) {}
//		try { TaskInvokerTool.main(new String[] {"checkout", "checkout", "CheckoutToolHelp.txt", "--workspace-path=C:\\Projects\\workspace", "--root-module-version=Domain1/app-a:S/v-3.2.1", "--reference-path-matcher=**"}); } catch (ExitException ee) {}
//		try { TaskInvokerTool.main(new String[] {"checkout", "checkout", "CheckoutToolHelp.txt", "--workspace-path=C:\\Projects\\workspace", "--root-module-version=Domain1/app-a", "--reference-path-matcher=**"}); } catch (ExitException ee) {}
	}

	private static void testNewDynamicVersionUniform() {
		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "uniform");
//		try { SwitchToDynamicVersionTool.main(new String[] {"--help"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--root-module-version=Domain1/app-a", "--reference-path-matcher=**->/Domain1/app-a"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--root-module-version=Domain1/app-a", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}
//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a"}); } catch (ExitException ee) {}
//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain2/app-b"}); } catch (ExitException ee) {}
//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add-reference-path-matcher", "/Domain1/app-a->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/:D/develop"}); } catch (ExitException ee) {}
	}

	private static void testNewStaticVersionUniform() {
		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "uniform");
//		try { CreateStaticVersionTool.main(new String[] {"--help"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--root-module-version=Domain1/app-a", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a:D/develop"}); } catch (ExitException ee) {}
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a:D/develop"}); } catch (ExitException ee) {}

//		System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_STATIC_VERSION", "S/v-2017-05-15");
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a:D/develop"}); } catch (ExitException ee) {}

//		System.setProperty("org.azyva.dragom.runtime-property.NEW_STATIC_VERSION_PLUGIN_ID", "uniform");

//		System.setProperty("org.azyva.dragom.runtime-property.SPECIFIC_STATIC_VERSION_PREFIX", "S/v-2017-05-15");
//		System.setProperty("org.azyva.dragom.runtime-property.REVISION_DECIMAL_POSITION_COUNT", "5");
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a:D/develop"}); } catch (ExitException ee) {}

//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}

//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a:D/develop"}); } catch (ExitException ee) {}
	}

	private static void testNewStaticVersionSemantic() {
		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a:D/develop"}); } catch (ExitException ee) {}

		System.setProperty("org.azyva.dragom.runtime-property.NEW_STATIC_VERSION_PLUGIN_ID", "semantic");
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a:D/develop"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}

	}

	private static void testPhaseDevelopment() {
//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a:S/v-3.2.1"}); } catch (ExitException ee) {}
//		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "uniform");
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}
//
//		System.setProperty("org.azyva.dragom.runtime-property.NEW_STATIC_VERSION_PLUGIN_ID", "phase");
//		System.setProperty("org.azyva.dragom.runtime-property.CURRENT_PHASE", "iteration01");
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}
//
//		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "phase");
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}


//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain1/app-a:S/v-3.2.1"}); } catch (ExitException ee) {}
//		try { RootManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "add", "Domain2/app-b:S/v-2000-07-01.01"}); } catch (ExitException ee) {}
//		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "uniform");
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}

//		System.setProperty("org.azyva.dragom.runtime-property.NEW_STATIC_VERSION_PLUGIN_ID", "phase");
//		System.setProperty("org.azyva.dragom.runtime-property.CURRENT_PHASE", "iteration01");
//		try { CreateStaticVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}

//		System.setProperty("org.azyva.dragom.runtime-property.NEW_DYNAMIC_VERSION_PLUGIN_ID", "phase");
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=/Domain1/app-a"}); } catch (ExitException ee) {}
//		try { SwitchToDynamicVersionTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--reference-path-matcher=**->/Domain1/app-a-model-intf"}); } catch (ExitException ee) {}
	}

	private static void testNewStaticVersionPhase() {
	}

	private static void printTestCategoryHeader(String header) {
		System.out.println("########################################");
		System.out.println("Starting test category:");
		System.out.println(header);
		System.out.println("########################################");
	}

	private static void resetTestWorkspace() {
		InputStream inputStreamLoggingProperties;
		Path pathLoggingProperties;
		String loggingProperties;

		System.out.println("Resetting test workspace directory " + IntegrationTestSuite.pathTestWorkspace + '.');

		try {
			if (IntegrationTestSuite.pathTestWorkspace.toFile().exists()) {
				Path pathModel;
				InputStream inputStream;

				pathModel = IntegrationTestSuite.pathTestWorkspace.resolve("simple-model.xml");
				inputStream = IntegrationTestSuite.class.getResourceAsStream("/simple-model.xml");
				Files.copy(inputStream, pathModel, StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();

				System.setProperty("org.azyva.dragom.UrlModel" , pathModel.toUri().toString());

				ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "release"});

				FileUtils.deleteDirectory(IntegrationTestSuite.pathTestWorkspace.toFile());

				System.getProperties().remove("org.azyva.dragom.UrlModel");
			}

			IntegrationTestSuite.pathTestWorkspace.toFile().mkdirs();

			inputStreamLoggingProperties = IntegrationTestSuite.class.getResourceAsStream("/logging.properties");
			pathLoggingProperties = IntegrationTestSuite.pathTestWorkspace.resolve("logging.properties");
			Files.copy(inputStreamLoggingProperties, pathLoggingProperties, StandardCopyOption.REPLACE_EXISTING);
			inputStreamLoggingProperties.close();
			loggingProperties = FileUtils.readFileToString(pathLoggingProperties.toFile());
			loggingProperties = loggingProperties.replaceAll("%test-workspace%", IntegrationTestSuite.pathTestWorkspace.toString());
			FileUtils.write(pathLoggingProperties.toFile(), loggingProperties);
			inputStreamLoggingProperties = new FileInputStream(pathLoggingProperties.toFile());
			LogManager.getLogManager().readConfiguration(inputStreamLoggingProperties);
			inputStreamLoggingProperties.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private static void printTestHeader(String header) {
		System.out.println("##########");
		System.out.println("Starting test of:");
		System.out.println(header);
		System.out.println("Output of tool follows.");
		System.out.println("##########");
	}

	private static void printTestFooter() {
		System.out.println("##########");
		System.out.println("Test completed.");
		System.out.println("##########\n");
	}

	private static void validateExitException(Exception e, int status) {
		ExitException exitException;

		if (!(e instanceof ExitException)) {
			System.err.println(">>>>> TEST FAILURE: ExitException expected. Exception thrown:");
			e.printStackTrace();
			return;
		}

		exitException = (ExitException)e;

		if (exitException.status != status) {
			System.err.println(">>>>> TEST FAILURE: Tool exited with status " + exitException.status + " but " + status + " was expected.");
		}
	}
}

class ExitException extends SecurityException {
	static final long serialVersionUID = 0;
	public final int status;
	public ExitException(int status) {
        this.status = status;
    }
}

class NoExitSecurityManager extends SecurityManager {
	@Override
	public void checkPermission(Permission perm) {
	}

	@Override
	public void checkPermission(Permission perm, Object context) {
	}

	@Override
	public void checkExit(int status) {
		super.checkExit(status);
		throw new ExitException(status);
	}
}

class EclipseSynchronizeErrOut {
	private static OutputStream outputStreamLast;

	private static class FixedOutputStream extends OutputStream {
		private final OutputStream outputStreamOrg;

		public FixedOutputStream(OutputStream outputStreamOrg) {
			this.outputStreamOrg = outputStreamOrg;
		}

		@Override
		public void write(int aByte) throws IOException {
			if (EclipseSynchronizeErrOut.outputStreamLast != this) {
				this.swap();
			}

			this.outputStreamOrg.write(aByte);
		}

		@Override
		public void write(byte[] arrayByte) throws IOException {
			if (EclipseSynchronizeErrOut.outputStreamLast != this) {
				this.swap();
			}

			this.outputStreamOrg.write(arrayByte);
		}

		@Override
		public void write(byte[] arrayByte, int offset, int length) throws IOException {
			if (EclipseSynchronizeErrOut.outputStreamLast != this) {
				this.swap();
			}

			this.outputStreamOrg.write(arrayByte, offset, length);
		}

		private void swap() throws IOException {
			if (EclipseSynchronizeErrOut.outputStreamLast != null) {
				EclipseSynchronizeErrOut.outputStreamLast.flush();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}

			EclipseSynchronizeErrOut.outputStreamLast = this;
		}

		@Override
		public void close() throws IOException {
			this.outputStreamOrg.close();
		}

		@Override public void flush() throws IOException {
			this.outputStreamOrg.flush();
		}
	}

	public static void fix() {
		System.setErr(new PrintStream(new FixedOutputStream(System.err)));
		System.setOut(new PrintStream(new FixedOutputStream(System.out)));
	}
}