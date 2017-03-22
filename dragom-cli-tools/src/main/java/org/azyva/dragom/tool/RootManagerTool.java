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

package org.azyva.dragom.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.ExecContext;
import org.azyva.dragom.execcontext.plugin.UserInteractionCallbackPlugin;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.model.ArtifactGroupIdVersion;
import org.azyva.dragom.model.Model;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.model.Version;
import org.azyva.dragom.model.plugin.ArtifactVersionMapperPlugin;
import org.azyva.dragom.reference.ReferencePathMatcher;
import org.azyva.dragom.reference.ReferencePathMatcherByElement;
import org.azyva.dragom.reference.ReferencePathMatcherOr;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

/**
 * Tool wrapper for the RootManager class.
 *
 * See the help information displayed by the RootManagerTool.help method.
 *
 * @author David Raymond
 */
public class RootManagerTool {
  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_LIST_OF_ROOT_MODULE_VERSIONS_EMPTY = "LIST_OF_ROOT_MODULE_VERSIONS_EMPTY";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_ARTIFACT_VERSION_CANNOT_MAP_TO_VERSION = "ARTIFACT_VERSION_CANNOT_MAP_TO_VERSION";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS = "MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS = "MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS = "MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_MODULE_VERSION_NOT_IN_LIST_OF_ROOTS = "MODULE_VERSION_NOT_IN_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_MODULE_VERSION_REMOVED_FROM_LIST_OF_ROOTS = "MODULE_VERSION_REMOVED_FROM_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_ALL_MODULE_VERSIONS_REMOVED_FROM_LIST_OF_ROOTS = "ALL_MODULE_VERSIONS_REMOVED_FROM_LIST_OF_ROOTS";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_LIST_REFERENCE_PATH_MATCHERS_EMPTY = "LIST_REFERENCE_PATH_MATCHERS_EMPTY";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ALREADY_IN_LIST = "REFERENCE_PATH_MATCHER_ALREADY_IN_LIST";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ADDED_TO_LIST = "REFERENCE_PATH_MATCHER_ADDED_TO_LIST";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_NOT_IN_LIST = "REFERENCE_PATH_MATCHER_NOT_IN_LIST";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST = "REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST";

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_ALL_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST = "ALL_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST";

  /**
   * ResourceBundle specific to this class.
   */
  private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(RootManagerTool.class.getName() + "ResourceBundle");

  /**
   * Indicates that the class has been initialized.
   */
  private static boolean indInit;

  /**
   * Options for parsing the command line.
   */
  private static Options options;

  /**
   * Method main.
   *
   * @param args Arguments.
   */
  public static void main(String[] args) {
    DefaultParser defaultParser;
    CommandLine commandLine;
    String command;
    int exitStatus;

    RootManagerTool.init();

    try {
      defaultParser = new DefaultParser();

      try {
        commandLine = defaultParser.parse(RootManagerTool.options, args);
      } catch (org.apache.commons.cli.ParseException pe) {
        throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
      }

      if (CliUtil.hasHelpOption(commandLine)) {
        RootManagerTool.help();
      } else {
        args = commandLine.getArgs();

        if (args.length < 1) {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
        }

        CliUtil.setupExecContext(commandLine, true);

        command = args[0];

        if (command.equals("list")) {
          RootManagerTool.listCommand(commandLine);
        } else if (command.equals("add")) {
          RootManagerTool.addCommand(commandLine);
        } else if (command.equals("add-artifact")) {
          RootManagerTool.addArtifactCommand(commandLine);
        } else if (command.equals("add-artifact-from-file")) {
            RootManagerTool.addArtifactFromFileCommand(commandLine);
        } else if (command.equals("remove")) {
          RootManagerTool.removeCommand(commandLine);
        } else if (command.equals("remove-all")) {
          RootManagerTool.removeAllCommand(commandLine);
        } else if (command.equals("list-reference-path-matchers")) {
          RootManagerTool.listReferencePathMatchersCommand(commandLine);
        } else if (command.equals("add-reference-path-matcher")) {
          RootManagerTool.addReferencePathMatcherCommand(commandLine);
        } else if (command.equals("remove-reference-path-matcher")) {
          RootManagerTool.removeReferencePathMatcherCommand(commandLine);
        } else if (command.equals("remove-all-reference-path-matchers")) {
          RootManagerTool.removeAllReferencePathMatchersCommand(commandLine);
        } else {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_COMMAND), command, CliUtil.getHelpCommandLineOption()));
        }
      }

      // Need to call before ExecContextHolder.endToolAndUnset.
      exitStatus = Util.getExitStatusAndShowReason();
    } catch (RuntimeExceptionUserError reue) {
      System.err.println(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_USER_ERROR_PREFIX) + reue.getMessage());
      exitStatus = 1;
    } catch (RuntimeException re) {
      re.printStackTrace();
      exitStatus = 1;
    } finally {
      ExecContextHolder.endToolAndUnset();
    }

    System.exit(exitStatus);
  }

  /**
   * Initializes the class.
   */
  private synchronized static void init() {
    if (!RootManagerTool.indInit) {
      Option option;

      CliUtil.initJavaUtilLogging();

      RootManagerTool.options = new Options();

      option = new Option(null, null);
      option.setLongOpt("ind-allow-duplicate-modules");
      RootManagerTool.options.addOption(option);

      CliUtil.addStandardOptions(RootManagerTool.options);

      RootManagerTool.indInit = true;
    }
  }

  /**
   * Displays help information.
   */
  private static void help() {
    try {
      IOUtils.copy(CliUtil.getLocalizedResourceAsStream(RootManagerTool.class, "RootManagerToolHelp.txt"), System.out);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Implements the "list" command.
   *
   * @param commandLine CommandLine.
   */
  private static void listCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    List<ModuleVersion> listModuleVersion;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    listModuleVersion = RootManager.getListModuleVersion();

    if (listModuleVersion.isEmpty()) {
      userInteractionCallbackPlugin.provideInfo(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_LIST_OF_ROOT_MODULE_VERSIONS_EMPTY));
    } else {
      for (ModuleVersion moduleVersion: listModuleVersion) {
        userInteractionCallbackPlugin.provideInfo(moduleVersion.toString());
      }
    }
  }

  /**
   * Implements the "add" command.
   *
   * @param commandLine CommandLine.
   */
  private static void addCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    boolean indAllowDuplicateModule;
    ModuleVersion moduleVersion;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length < 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    indAllowDuplicateModule = commandLine.hasOption("ind-allow-duplicate-modules");

    for (int i = 1; i < args.length; i++) {
      try {
        moduleVersion = ModuleVersion.parse(args[i]);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(pe.getMessage());
      }

      if (RootManager.containsModuleVersion(moduleVersion)) {
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS), moduleVersion));
      } else {

        if (indAllowDuplicateModule) {
          RootManager.addModuleVersion(moduleVersion, true);
          userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
        } else {
          ModuleVersion moduleVersionOrg;

          moduleVersionOrg = RootManager.getModuleVersion(moduleVersion.getNodePath());

          if (moduleVersionOrg != null) {
            RootManager.replaceModuleVersion(moduleVersionOrg, moduleVersion);
            userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS), moduleVersionOrg, moduleVersion));
          } else {
            RootManager.addModuleVersion(moduleVersion, false);
            userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
          }
        }
      }
    }
  }

  /**
   * Implements the "add-artifact" command.
   *
   * @param commandLine CommandLine.
   */
  private static void addArtifactCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ArtifactGroupIdVersion artifactGroupIdVersion;
    ExecContext execContext;
    Model model;
    Module module;
    ArtifactVersionMapperPlugin artifactVersionMapperPlugin;
    Version version;
    ModuleVersion moduleVersion;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length < 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    for (int i = 1; i < args.length; i++) {
      // First, convert the ArtifactGroupIdVersion to a ModuleVersion.

      try {
        artifactGroupIdVersion = ArtifactGroupIdVersion.parse(args[i]);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(pe.getMessage());
      }

      execContext = ExecContextHolder.get();
      model = execContext.getModel();
      module = model.findModuleByArtifactGroupId(artifactGroupIdVersion.getArtifactGroupId());

      if (module == null) {
        // We expect the handling of the tool exit status to be done by
        // model.findModuleByArtifactGroupId called above. If we get here with a null
        // module, it means we are expected to silently continue and ignore artifact.
        continue;
      }

      if (!module.isNodePluginExists(ArtifactVersionMapperPlugin.class, null)) {
        throw new RuntimeExceptionUserError(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ARTIFACT_VERSION_CANNOT_MAP_TO_VERSION), artifactGroupIdVersion.getArtifactVersion(), module.getNodePath()));
      }

      artifactVersionMapperPlugin = module.getNodePlugin(ArtifactVersionMapperPlugin.class, null);

      version = artifactVersionMapperPlugin.mapArtifactVersionToVersion(artifactGroupIdVersion.getArtifactVersion());

      moduleVersion = new ModuleVersion(module.getNodePath(), version);

      // Second, do the same as for the add command.

      if (RootManager.containsModuleVersion(moduleVersion)) {
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS), moduleVersion));
      } else {
        boolean indAllowDuplicateModule;

        indAllowDuplicateModule = commandLine.hasOption("ind-allow-duplicate-modules");

        if (indAllowDuplicateModule) {
          RootManager.addModuleVersion(moduleVersion, true);
          userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
        } else {
          ModuleVersion moduleVersionOrg;

          moduleVersionOrg = RootManager.getModuleVersion(moduleVersion.getNodePath());

          if (moduleVersionOrg != null) {
            RootManager.replaceModuleVersion(moduleVersionOrg, moduleVersion);
            userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS), moduleVersionOrg, moduleVersion));
          } else {
            RootManager.addModuleVersion(moduleVersion, false);
            userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
          }
        }
      }
    }
  }

  /**
   * Implements the "add-artifact-from-file" command.
   *
   * @param commandLine CommandLine.
   */
  private static void addArtifactFromFileCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    BufferedReader bufferedReaderArtifacts;
    String artifact;
    ArtifactGroupIdVersion artifactGroupIdVersion;
    ExecContext execContext;
    Model model;
    Module module;
    ArtifactVersionMapperPlugin artifactVersionMapperPlugin;
    Version version;
    ModuleVersion moduleVersion;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length != 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    bufferedReaderArtifacts = null;

    try {
      bufferedReaderArtifacts = new BufferedReader(new FileReader(args[1]));

      while ((artifact = bufferedReaderArtifacts.readLine()) != null) {
        // First, convert the ArtifactGroupIdVersion to a ModuleVersion.

        try {
          artifactGroupIdVersion = ArtifactGroupIdVersion.parse(artifact);
        } catch (ParseException pe) {
          throw new RuntimeExceptionUserError(pe.getMessage());
        }

        execContext = ExecContextHolder.get();
        model = execContext.getModel();
        module = model.findModuleByArtifactGroupId(artifactGroupIdVersion.getArtifactGroupId());

        if (module == null) {
          // We expect the handling of the tool exit status to be done by
          // model.findModuleByArtifactGroupId called above. If we get here with a null
          // module, it means we are expected to silently continue and ignore artifact.
          continue;
        }

        if (!module.isNodePluginExists(ArtifactVersionMapperPlugin.class, null)) {
          throw new RuntimeExceptionUserError(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ARTIFACT_VERSION_CANNOT_MAP_TO_VERSION), artifactGroupIdVersion.getArtifactVersion(), module.getNodePath()));
        }

        artifactVersionMapperPlugin = module.getNodePlugin(ArtifactVersionMapperPlugin.class, null);

        version = artifactVersionMapperPlugin.mapArtifactVersionToVersion(artifactGroupIdVersion.getArtifactVersion());

        moduleVersion = new ModuleVersion(module.getNodePath(), version);

        // Second, do the same as for the add command.

        if (RootManager.containsModuleVersion(moduleVersion)) {
          userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS), moduleVersion));
        } else {
          boolean indAllowDuplicateModule;

          indAllowDuplicateModule = commandLine.hasOption("ind-allow-duplicate-modules");

          if (indAllowDuplicateModule) {
            RootManager.addModuleVersion(moduleVersion, true);
            userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
          } else {
            ModuleVersion moduleVersionOrg;

            moduleVersionOrg = RootManager.getModuleVersion(moduleVersion.getNodePath());

            if (moduleVersionOrg != null) {
              RootManager.replaceModuleVersion(moduleVersionOrg, moduleVersion);
              userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS), moduleVersionOrg, moduleVersion));
            } else {
              RootManager.addModuleVersion(moduleVersion, false);
              userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
            }
          }
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } finally {
      if (bufferedReaderArtifacts != null) {
        try {
          bufferedReaderArtifacts.close();
        } catch (IOException ioe) {}
      }
    }
  }

  /**
   * Implements the "remove" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removeCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ModuleVersion moduleVersion;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length < 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    for (int i = 1; i < args.length; i++) {
      try {
        moduleVersion = ModuleVersion.parse(args[i]);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(pe.getMessage());
      }

      if (!RootManager.containsModuleVersion(moduleVersion)) {
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_NOT_IN_LIST_OF_ROOTS), moduleVersion));
      } else {
        RootManager.removeModuleVersion(moduleVersion);
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REMOVED_FROM_LIST_OF_ROOTS), moduleVersion));
      }
    }
  }

  /**
   * Implements the "remove-all" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removeAllCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    RootManager.removeAllModuleVersion();
    userInteractionCallbackPlugin.provideInfo(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ALL_MODULE_VERSIONS_REMOVED_FROM_LIST_OF_ROOTS));
  }

  /**
   * Implements the "list-reference-path-matchers" command.
   *
   * @param commandLine CommandLine.
   */
  private static void listReferencePathMatchersCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ReferencePathMatcherOr referencePathMatcherOr;
    List<ReferencePathMatcher> listReferencePathMatcher;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
    listReferencePathMatcher = referencePathMatcherOr.getListReferencePathMatcher();

    if (listReferencePathMatcher.isEmpty()) {
      userInteractionCallbackPlugin.provideInfo(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_LIST_REFERENCE_PATH_MATCHERS_EMPTY));
    } else {
      for (ReferencePathMatcher referencePathMatcher: listReferencePathMatcher) {
        userInteractionCallbackPlugin.provideInfo(referencePathMatcher.toString());
      }
    }
  }

  /**
   * Implements the "add-reference-path-matcher" command.
   *
   * @param commandLine CommandLine.
   */
  private static void addReferencePathMatcherCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ReferencePathMatcherOr referencePathMatcherOr;
    ReferencePathMatcherByElement referencePathMatcherByElement;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length < 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    for (int i = 1; i < args.length; i++) {
      referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
      try {
        referencePathMatcherByElement = ReferencePathMatcherByElement.parse(args[i], ExecContextHolder.get().getModel());
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(pe.getMessage());
      }

      if (referencePathMatcherOr.getListReferencePathMatcher().contains(referencePathMatcherByElement)) {
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ALREADY_IN_LIST), referencePathMatcherByElement));
       } else {
        referencePathMatcherOr.addReferencePathMatcher(referencePathMatcherByElement);
        RootManager.saveReferencePathMatcherOr();
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ADDED_TO_LIST), referencePathMatcherByElement));
      }
    }
  }

  /**
   * Implements the "remove-reference-path-matcher" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removeReferencePathMatcherCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ReferencePathMatcherOr referencePathMatcherOr;
    ReferencePathMatcherByElement referencePathMatcherByElement;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length < 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    for (int i = 1; i < args.length; i++) {
      referencePathMatcherOr = RootManager.getReferencePathMatcherOr();

      try {
        referencePathMatcherByElement = ReferencePathMatcherByElement.parse(args[i], ExecContextHolder.get().getModel());
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(pe.getMessage());
      }

      if (!referencePathMatcherOr.getListReferencePathMatcher().contains(referencePathMatcherByElement)) {
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_NOT_IN_LIST), referencePathMatcherByElement));
      } else {
        referencePathMatcherOr.getListReferencePathMatcher().remove(referencePathMatcherByElement);
        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST), referencePathMatcherByElement));
        RootManager.saveReferencePathMatcherOr();
      }
    }
  }

  /**
   * Implements the "remove-all-reference-path-matchers" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removeAllReferencePathMatchersCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    String[] args;
    ReferencePathMatcherOr referencePathMatcherOr;

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

    args = commandLine.getArgs();

    if (args.length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
    referencePathMatcherOr.getListReferencePathMatcher().clear();

    RootManager.saveReferencePathMatcherOr();
    userInteractionCallbackPlugin.provideInfo(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ALL_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST));
  }
}
