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


public class IntegrationTestSuiteWorkspaceManagerTool {
	/*********************************************************************************
	 * Tests WorkspaceManagerTool.
	 *********************************************************************************/
	public static void testWorkspaceManagerTool() {
		return;
		/*
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
		*/
	}
}
/*
private static void testWorkspaceManagerBase() {
//	try { WorkspaceManagerTool.main(new String[] {"--help"}); } catch (ExitException ee) {}
	try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "status"}); } catch (ExitException ee) {}
	try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "update"}); } catch (ExitException ee) {}
//	try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "commit"}); } catch (ExitException ee) {}
//	try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "--commit-message=Commit message", "commit"}); } catch (ExitException ee) {}
//	try { WorkspaceManagerTool.main(new String[] {"--workspace-path=C:\\Projects\\workspace", "force-unlock"}); } catch (ExitException ee) {}
}
*/