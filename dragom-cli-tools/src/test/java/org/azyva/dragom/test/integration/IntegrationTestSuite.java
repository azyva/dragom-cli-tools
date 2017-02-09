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

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.azyva.dragom.git.Git;
import org.azyva.dragom.git.impl.DefaultGitImpl;
import org.azyva.dragom.tool.ExecContextManagerTool;

/**
 * Suite of integration tests.
 *
 * <p>These are not unit tests and are not developped with JUnit or another
 * testing framework.
 *
 * @author David Raymond
 */
public class IntegrationTestSuite {
  /**
   * Path to the test workspace.
   */
  public static Path pathTestWorkspace;

  /**
   * InputStream double which allows simulating user input.
   */
  public static InputStreamDouble inputStreamDouble;

  /**
   * Git interface.
   */
  public static Git git;

  /**
   * Holds the last Exception raised.
   */
  public static Exception exception;

  /**
   * Main method.
   *
   * @param args Arguments.
   */
  public static void main(String[] args) {
    Set<String> setTestCategory;
    boolean indAllTests;

    System.setSecurityManager(new NoExitSecurityManager());

    EclipseSynchronizeErrOut.fix();

    IntegrationTestSuite.inputStreamDouble = new InputStreamDouble();
    System.setIn(IntegrationTestSuite.inputStreamDouble);

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
      IntegrationTestSuiteDragomToolInvoker.testDragomToolInvoker();
    }

    if (indAllTests || setTestCategory.contains("ExecContextManagerTool")) {
      IntegrationTestSuiteExecContextManagerTool.testExecContextManagerTool();
    }

    if (indAllTests || setTestCategory.contains("RootManagerTool")) {
      IntegrationTestSuiteRootManagerTool.testRootManagerTool();
    }

    if (indAllTests || setTestCategory.contains("CredentialManagerTool")) {
      IntegrationTestSuiteCredentialManagerTool.testCredentialManagerTool();
    }

    if (indAllTests || setTestCategory.contains("GenericRootModuleVersionJobInvokerTool")) {
      IntegrationTestSuiteGenericRootModuleVersionJobInvokerTool.testGenericRootModuleVersionJobInvokerTool();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolBase")) {
      IntegrationTestSuiteCheckoutToolBase.testCheckoutToolBase();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolConflict")) {
      IntegrationTestSuiteCheckoutToolConflict.testCheckoutToolConflict();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolSwitch")) {
      IntegrationTestSuiteCheckoutToolSwitch.testCheckoutToolSwitch();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolMultipleBase")) {
      IntegrationTestSuiteCheckoutToolMultipleBase.testCheckoutToolMultipleBase();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolMultipleConflict")) {
      IntegrationTestSuiteCheckoutToolMultipleConflict.testCheckoutToolMultipleConflict();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolMultipleSwitch")) {
      IntegrationTestSuiteCheckoutToolMultipleSwitch.testCheckoutToolMultipleSwitch();
    }

    if (indAllTests || setTestCategory.contains("CheckoutToolMultipleVersions")) {
      IntegrationTestSuiteCheckoutToolMultipleVersions.testCheckoutToolMultipleVersions();
    }

    if (indAllTests || setTestCategory.contains("WorkspaceManagerToolBase")) {
      IntegrationTestSuiteWorkspaceManagerToolBase.testWorkspaceManagerToolBase();
    }

    if (indAllTests || setTestCategory.contains("WorkspaceManagerToolStatusUpdateCommit")) {
      IntegrationTestSuiteWorkspaceManagerToolStatusUpdateCommit.testWorkspaceManagerToolStatusUpdateCommit();
    }

    if (indAllTests || setTestCategory.contains("WorkspaceManagerToolClean")) {
      IntegrationTestSuiteWorkspaceManagerToolClean.testWorkspaceManagerToolClean();
    }

    if (indAllTests || setTestCategory.contains("WorkspaceManagerToolBuildClean")) {
      IntegrationTestSuiteWorkspaceManagerToolBuildClean.testWorkspaceManagerToolBuildClean();
    }

    if (indAllTests || setTestCategory.contains("BuildToolBase")) {
      IntegrationTestSuiteBuildToolBase.testBuildToolBase();
    }

    if (indAllTests || setTestCategory.contains("BuildToolUserSystemMode")) {
      IntegrationTestSuiteBuildToolUserSystemMode.testBuildToolUserSystemMode();
    }

    if (indAllTests || setTestCategory.contains("BuildToolMavenBuilderPluginImplConfig")) {
      IntegrationTestSuiteBuildToolMavenBuilderPluginImplConfig.testBuildToolMavenBuilderPluginImplConfig();
    }

    if (indAllTests || setTestCategory.contains("ReferenceGraphReportToolBase")) {
      IntegrationTestSuiteReferenceGraphReportToolBase.testReferenceGraphReportToolBase();
    }

    if (indAllTests || setTestCategory.contains("ReferenceGraphReportToolReport")) {
      IntegrationTestSuiteReferenceGraphReportToolReport.testReferenceGraphReportToolReport();
    }

    if (indAllTests || setTestCategory.contains("SwitchToDynamicVersionToolBase")) {
      IntegrationTestSuiteSwitchToDynamicVersionToolBase.testSwitchToDynamicVersionToolBase();
    }

    if (indAllTests || setTestCategory.contains("SwitchToDynamicVersionToolRecurse")) {
      IntegrationTestSuiteSwitchToDynamicVersionToolRecurse.testSwitchToDynamicVersionToolRecurse();
    }

    if (indAllTests || setTestCategory.contains("SwitchToDynamicVersionToolReferenceChange")) {
      IntegrationTestSuiteSwitchToDynamicVersionToolReferenceChange.testSwitchToDynamicVersionToolReferenceChange();
    }

    if (indAllTests || setTestCategory.contains("SwitchToDynamicVersionToolHotfix")) {
      IntegrationTestSuiteSwitchToDynamicVersionToolHotfix.testSwitchToDynamicVersionToolHotfix();
    }

    if (indAllTests || setTestCategory.contains("SwitchToDynamicVersionToolPhase")) {
      IntegrationTestSuiteSwitchToDynamicVersionToolPhase.testSwitchToDynamicVersionToolPhase();
    }

    if (indAllTests || setTestCategory.contains("ReleaseToolBase")) {
      IntegrationTestSuiteReleaseToolBase.testReleaseToolBase();
    }

    if (indAllTests || setTestCategory.contains("ReleaseVersionToolRecurse")) {
      IntegrationTestSuiteReleaseToolRecurse.testReleaseToolRecurse();
    }

    if (indAllTests || setTestCategory.contains("ReleaseVersionToolSemantic")) {
      IntegrationTestSuiteReleaseToolSemantic.testReleaseToolSemantic();
      //??? incomplete. ANd there seems to be a bug with main workspace directory concept.
    }
/*
TODO:
    if (indAllTests || setTestCategory.contains("ReleaseVersionToolPhase")) {
      IntegrationTestSuiteCreateStaticVersionToolPhase.testCreateStaticVersionToolPhase();
    }
*/

    if (indAllTests || setTestCategory.contains("ReleaseToolMainModuleVersion")) {
//TODO:
      IntegrationTestSuiteReleaseToolMainModuleVersion.testReleaseToolMainModuleVersion();
    }

    if (indAllTests || setTestCategory.contains("ReleaseToolMainModuleVersion")) {
//TODO:
      IntegrationTestSuiteReleaseToolMainModuleVersion.testReleaseToolMainModuleVersion();
    }

    if (indAllTests || setTestCategory.contains("MergeMainToolBase")) {
      IntegrationTestSuiteMergeMainToolBase.testMergeMainToolBase();
    }

    if (indAllTests || setTestCategory.contains("SetupJenkinsJobsToolBase")) {
      IntegrationTestSuiteSetupJenkinsJobsToolBase.testSetupJenkinsJobsToolBase();
    }

    if (indAllTests || setTestCategory.contains("MutableModelSimpleConfig")) {
      IntegrationTestSuiteMutableModelSimpleConfig.testMutableModelSimpleConfig();
    }

//    build-remote
//    change-reference-to-module-version
//    merge-main
//    merge-reference-graph
  }

  /**
   * Prints a header for a test category.
   *
   * @param header Header.
   */
  public static void printTestCategoryHeader(String header) {
    System.out.println("########################################");
    System.out.println("Starting test category:");
    System.out.println(header);
    System.out.println("########################################");
  }

  /**
   * Resets the test workspace.
   */
  public static void resetTestWorkspace() {
    InputStream inputStreamLoggingProperties;
    Path pathLoggingProperties;
    String loggingProperties;

    System.out.println("Resetting test workspace directory " + IntegrationTestSuite.pathTestWorkspace + '.');

    try {
      LogManager.getLogManager().reset();

      if (IntegrationTestSuite.pathTestWorkspace.toFile().exists()) {
        Path pathModel;
        InputStream inputStream;

        pathModel = IntegrationTestSuite.pathTestWorkspace.resolve("simple-model.xml");
        inputStream = IntegrationTestSuite.class.getResourceAsStream("/simple-model.xml");
        Files.copy(inputStream, pathModel, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();

        System.setProperty("org.azyva.dragom.init-property.URL_MODEL" , pathModel.toUri().toString());

        try {
          ExecContextManagerTool.main(new String[] {"--workspace=" + IntegrationTestSuite.pathTestWorkspace.resolve("workspace"), "release"});
        } catch (ExitException ee) {
          if (ee.status != 0) {
            throw ee;
          }
        }

        FileUtils.deleteDirectory(IntegrationTestSuite.pathTestWorkspace.toFile());

        System.getProperties().remove("org.azyva.dragom.init-property.URL_MODEL");
      }

      IntegrationTestSuite.pathTestWorkspace.toFile().mkdirs();

      inputStreamLoggingProperties = IntegrationTestSuite.class.getResourceAsStream("/logging.properties");
      pathLoggingProperties = IntegrationTestSuite.pathTestWorkspace.resolve("logging.properties");
      Files.copy(inputStreamLoggingProperties, pathLoggingProperties, StandardCopyOption.REPLACE_EXISTING);
      inputStreamLoggingProperties.close();
      loggingProperties = FileUtils.readFileToString(pathLoggingProperties.toFile());
      loggingProperties = loggingProperties.replace("%test-workspace%", IntegrationTestSuite.pathTestWorkspace.toString());
      FileUtils.write(pathLoggingProperties.toFile(), loggingProperties);
      inputStreamLoggingProperties = new FileInputStream(pathLoggingProperties.toFile());
      LogManager.getLogManager().readConfiguration(inputStreamLoggingProperties);
      inputStreamLoggingProperties.close();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Prints a header for a single test.
   *
   * <p>Since this is called before running a test, it is also considered a test
   * initialization method. It therefore sets
   * {@link IntegrationTestSuite#exception} to null.
   *
   * @param header Header.
   */
  public static void printTestHeader(String header) {
    System.out.println("###############################################################################");
    System.out.println("Starting test of:");
    System.out.println(header);
    System.out.println("Output of tool follows.");
    System.out.println("###############################################################################");

    IntegrationTestSuite.exception = null;
  }

  /**
   * Print a footer for a single test.
   */
  public static void printTestFooter() {
    System.out.println("###############################################################################");
    System.out.println("Test completed.");
    System.out.println("###############################################################################\n");
  }

  /**
   * Validates that an exception is an ExitException (normal termination of a
   * tool) and that the status (return) code is as specified.
   *
   * @param exception Exception.
   * @param status Expected status code.
   */
  public static void validateExitException(Exception exception, int status) {
    ExitException exitException;

    if (exception == null) {
      throw new RuntimeException(">>>>> TEST FAILURE: ExitException with status " + status +  " expected .");
    }

    if (!(exception instanceof ExitException)) {
      throw new RuntimeException(">>>>> TEST FAILURE: ExitException expected. Exception thrown:", exception);
    }

    exitException = (ExitException)exception;

    if (exitException.status != status) {
      throw new RuntimeException(">>>>> TEST FAILURE: Tool exited with status " + exitException.status + " but " + status + " was expected.");
    }
  }

  /**
   * Appends content to a text file.
   *
   * @param pathFile Path to the file.
   * @param content Content.
   */
  public static void appendToFile(Path pathFile, String content) {
    Writer writerFile;

    try {
      writerFile = new FileWriter(pathFile.toFile(), true);
      writerFile.append(content);
      writerFile.close();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * @return Git interface.
   */
  public static Git getGit() {
    if (IntegrationTestSuite.git == null) {
      IntegrationTestSuite.git = new DefaultGitImpl();
    }

    return IntegrationTestSuite.git;
  }
}
