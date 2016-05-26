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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Permission;

import org.apache.commons.io.FileUtils;
import org.azyva.dragom.tool.DragomToolInvoker;
import org.azyva.dragom.tool.ExecContextPropertyManagerTool;
import org.azyva.dragom.tool.RootManagerTool;
import org.azyva.dragom.tool.WorkspaceManagerTool;

public class IntegrationTestSuite {
	private static Path pathTestWorkspace;

	public static void main(String[] args) {
		System.setSecurityManager(new NoExitSecurityManager());

		EclipseSynchronizeErrOut.fix();

		if (args.length == 0) {
			IntegrationTestSuite.pathTestWorkspace = Paths.get(System.getProperty("user.dir"));
			System.out.println("Test workspace directory not specified. Using current directory " + IntegrationTestSuite.pathTestWorkspace + '.');
		} else {
			IntegrationTestSuite.pathTestWorkspace = Paths.get(args[0]);
			System.out.println("Using specified test workspace directory " + IntegrationTestSuite.pathTestWorkspace + '.');
		}

		if (IntegrationTestSuite.pathTestWorkspace.toFile().exists()) {
			try {
				FileUtils.deleteDirectory(IntegrationTestSuite.pathTestWorkspace.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		IntegrationTestSuite.pathTestWorkspace.toFile().mkdirs();

		IntegrationTestSuite.testDragomToolInvoker();
		IntegrationTestSuite.testExecContextPropertyManagerTool();

//		IntegrationTestSuite.testCheckout();
//		IntegrationTestSuite.testWorkspaceManagerBase();
//		IntegrationTestSuite.testRootManagerBase();
//		IntegrationTestSuite.testNewDynamicVersionUniform();
//		IntegrationTestSuite.testNewStaticVersionUniform();
//		IntegrationTestSuite.testNewStaticVersionSemantic();
//		IntegrationTestSuite.testPhaseDevelopment();
	}


	private static void testDragomToolInvoker() {
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

	private static void testExecContextPropertyManagerTool() {
		InputStream inputStream;
		Path pathUserProperties;
		Path pathToolProperties;
		Path pathModel;

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool");
		try {
			ExecContextPropertyManagerTool.main(new String[] {});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --help");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--help"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool get-properties (with UrlModel not set)");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --user-properties=dummy-user.properties get-properties (with dummy UrlModel)");
		try {
			inputStream = IntegrationTestSuite.class.getResourceAsStream("/dummy-user.properties");
			pathUserProperties = IntegrationTestSuite.pathTestWorkspace.resolve("dummy-user.properties");
			Files.copy(inputStream, pathUserProperties, StandardCopyOption.REPLACE_EXISTING);
			inputStream.close();

			ExecContextPropertyManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "get-properties"});
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

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties (with valid UrlModel and workspace)");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace set-property NAME");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace set-property NAME VALUE extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME", "VALUE", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace set-property NAME VALUE");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-property", "NAME", "VALUE"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties (with IndIgnoreCachedExecContext and IndIgnoreCachedModel)");
		try {
			System.setProperty("org.azyva.dragom.IndIgnoreCachedExecContext" , "true");
			System.setProperty("org.azyva.dragom.IndIgnoreCachedModel" , "true");
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties NA extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NA", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties NA");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NA"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-properties NB");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-properties", "NB"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-property");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-property NAME extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NAME", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-property NB");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NB"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace get-property NAME");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-property", "NAME"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-property");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-property NAME extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property", "NAME", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-property NA");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-property", "NA"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-properties NA extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NA", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-properties NB");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NB"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace remove-properties NA");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "remove-properties", "NA"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace set-properties-from-tool-properties extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-properties-from-tool-properties", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace set-properties-from-tool-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "set-properties-from-tool-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace -DNAME=VALUE set-properties-from-tool-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "set-properties-from-tool-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace -DNAME=VALUE -DNAME2=VALUE2 set-properties-from-tool-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "-DNAME2=VALUE2", "set-properties-from-tool-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace -DNAME=VALUE --tool-properties=simple-tool.properties set-properties-from-tool-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "-DNAME=VALUE", "--tool-properties=" + pathToolProperties, "set-properties-from-tool-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NAME extra");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NAME", "extra"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 1);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NB");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NB"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --user-properties=simple-user.properties --workspace=workspace get-init-property NAME4");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--user-properties=" + pathUserProperties, "--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "get-init-property", "NAME4"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace --no-confirm get-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--no-confirm", "get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

		// ################################################################################

		IntegrationTestSuite.printTestHeader("ExecContextPropertyManagerTool --workspace=workspace --no-confirm-context=A --no-confirm-context=B get-properties");
		try {
			ExecContextPropertyManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "--no-confirm-context=A", "--no-confirm-context=B", "get-properties"});
		} catch (Exception e) {
			IntegrationTestSuite.validateExitException(e, 0);
		}
		IntegrationTestSuite.printTestFooter();

	}

	// no-confirm in setupExecContext
	// no-confirm context

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