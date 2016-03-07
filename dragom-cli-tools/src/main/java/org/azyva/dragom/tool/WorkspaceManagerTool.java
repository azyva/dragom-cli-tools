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

package org.azyva.dragom.tool;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.ExecContext;
import org.azyva.dragom.execcontext.WorkspaceExecContext;
import org.azyva.dragom.execcontext.plugin.UserInteractionCallbackPlugin;
import org.azyva.dragom.execcontext.plugin.WorkspaceDir;
import org.azyva.dragom.execcontext.plugin.WorkspaceDirSystemModule;
import org.azyva.dragom.execcontext.plugin.WorkspaceDirUserModuleVersion;
import org.azyva.dragom.execcontext.plugin.WorkspacePlugin;
import org.azyva.dragom.execcontext.plugin.WorkspacePlugin.GetWorkspaceDirModeEnum;
import org.azyva.dragom.execcontext.plugin.WorkspacePlugin.WorkspaceDirAccessMode;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.model.Model;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.model.Version;
import org.azyva.dragom.model.VersionType;
import org.azyva.dragom.model.plugin.BuilderPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin.BaseVersion;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

/**
 * Tool for managing the workspace.
 *
 * See the help information displayed by the WorkspaceManagerTool.help method.
 *
 * @author David Raymond
 */
public class WorkspaceManagerTool {
	/**
	 * Logger for the class.
	 */
	//private static final Logger logger = LoggerFactory.getLogger(WorkspaceManagerTool.class);

	/**
	 * Name of the ResourceBundle of the class.
	 */
	public static final String RESOURCE_BUNDLE = "org/azyva/tool/WorkspaceManagerToolResourceBundle";

	/**
	 * Indicates that the class has been initialized.
	 */
	private static boolean indInit;

	/**
	 * Options for parsing the command line.
	 */
	private static Options options;

	/**
	 * ResourceBundle specific to this class.
	 */
	private static ResourceBundle resourceBundle;

	CommandLine commandLine;

	ExecContext execContext;

	WorkspacePlugin workspacePlugin;

	UserInteractionCallbackPlugin userInteractionCallbackPlugin;

	Model model;

	/**
	 * Stores data about a ModuleVersion and its corresponding directory within the
	 * workspace.
	 */
	private static class WorkspaceDirPath implements Comparable<WorkspaceDirPath> {
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;
		ModuleVersion moduleVersion;
		Path pathWorkspaceDir;

		@Override
		public int compareTo(WorkspaceDirPath workspaceDirPathOther) {
			return this.pathWorkspaceDir.compareTo(workspaceDirPathOther.pathWorkspaceDir);
		}

		@Override
		public boolean equals(Object other) {
			return this.pathWorkspaceDir.equals(((WorkspaceDirPath)other).pathWorkspaceDir);
		}
	}

	/**
	 * Method main.
	 *
	 * @param args Arguments.
	 */
	public static void main(String[] args) {
		Parser parser;
		CommandLine commandLine;
		String command;

		WorkspaceManagerTool.init();

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(WorkspaceManagerTool.options, args);
			} catch (org.apache.commons.cli.ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage()));
			}

			if (CliUtil.isHelpOption(commandLine)) {
				WorkspaceManagerTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length < 1) {
				throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
			}

			command = args[0];

			if (command.equals("force-unlock")) {
				ExecContextHolder.forceUnset(CliUtil.setupExecContext(commandLine, false));
			} else {
				WorkspaceManagerTool workspaceManagerTool;

				workspaceManagerTool = new WorkspaceManagerTool();

				workspaceManagerTool.commandLine = commandLine;
				workspaceManagerTool.execContext = CliUtil.setupExecContext(commandLine, true);
				workspaceManagerTool.workspacePlugin = workspaceManagerTool.execContext.getExecContextPlugin(WorkspacePlugin.class);
				workspaceManagerTool.userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
				workspaceManagerTool.model = ExecContextHolder.get().getModel();

				if (command.equals("status")) {
					workspaceManagerTool.statusCommand();
				} else if (command.equals("update")) {
					workspaceManagerTool.updateCommand();
				} else if (command.equals("commit")) {
					workspaceManagerTool.commitCommand();
				} else if (command.equals("clean-all")) {
					workspaceManagerTool.cleanAllCommand();
				} else if (command.equals("clean-system")) {
					workspaceManagerTool.cleanSystemCommand();
				} else if (command.equals("remove-module-version")) {
					workspaceManagerTool.removeModuleVersionCommand();
				} else if (command.equals("build-clean-all")) {
					workspaceManagerTool.buildCleanAllCommand();
				} else if (command.equals("build-clean-module-version")) {
					workspaceManagerTool.buildCleanModuleVersionCommand();
				} else if (command.equals("build-clean-dir")) {
					workspaceManagerTool.buildCleanDirCommand();
				} else if (command.equals("fix")) {
					workspaceManagerTool.fixCommand();
				} else {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_COMMAND), command));
				}
			}
		} catch (RuntimeExceptionUserError reue) {
			System.err.println(reue.getMessage());
			System.exit(1);
		} finally {
			ExecContextHolder.endToolAndUnset();
		}
	}

	/**
	 * Initializes the class.
	 */
	private synchronized static void init() {
		if (!WorkspaceManagerTool.indInit) {
			Option option;

			WorkspaceManagerTool.options = new Options();

			option = new Option(null, null);
			option.setLongOpt("commit-message");
			option.setArgs(1);
			WorkspaceManagerTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("commit-message");
			option.setArgs(1);
			WorkspaceManagerTool.options.addOption(option);

			CliUtil.addStandardOptions(WorkspaceManagerTool.options);

			WorkspaceManagerTool.resourceBundle = ResourceBundle.getBundle(WorkspaceManagerTool.RESOURCE_BUNDLE);

			WorkspaceManagerTool.indInit = true;
		}
	}

	private void statusCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		sortedSetWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: sortedSetWorkspaceDirPath) {
			StringBuilder stringBuilder;
			Module module;
			ScmPlugin scmPlugin;
			Version version;
			BaseVersion baseVersion;

			// We use a StringBuilder so that all the information is within a single text
			// block (UserInteractionCallbackPlugin.provideInfo can introduce empty lines)
			stringBuilder = new StringBuilder();

			stringBuilder.append("Workspace directory: ").append(workspaceDirPath.pathWorkspaceDir).append('\n');
			stringBuilder.append("Module version: ").append(workspaceDirPath.moduleVersion).append('\n');

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

			stringBuilder.append("SCM type: ").append(scmPlugin.getScmType()).append('\n');
			stringBuilder.append("SCM URL: ").append(scmPlugin.getScmUrl(workspaceDirPath.pathWorkspaceDir)).append('\n');

			version = scmPlugin.getVersion(workspaceDirPath.pathWorkspaceDir);

			stringBuilder.append("Version: ").append(version).append('\n');

			baseVersion = scmPlugin.getBaseVersion(version);
			stringBuilder.append("Base version: ").append(baseVersion == null ? null : baseVersion.versionBase).append('\n');;

			if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
				stringBuilder.append("Has local unsynchronized changes: ").append(!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.LOCAL_CHANGES_ONLY)).append('\n');
				stringBuilder.append("Has remote unsynchronized changes: ").append(!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.REMOTE_CHANGES_ONLY)).append('\n');
			}

			this.userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
		}
	}

	private void updateCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		sortedSetWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: sortedSetWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

			if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
				// Theoretically we should reserve access to the workspace directory. But we do
				// not bother since the tool does not perform deep processing and is not likely to
				// get into a conflicting situation.

				if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.REMOTE_CHANGES_ONLY)) {
					this.userInteractionCallbackPlugin.provideInfo("Updating workspace directory " + workspaceDirPath.pathWorkspaceDir + " containing module version " + workspaceDirPath.moduleVersion + '.');

					if (scmPlugin.update(workspaceDirPath.pathWorkspaceDir)) {
						this.userInteractionCallbackPlugin.provideInfo("Conflicts were encountered while updating workspace directory " + workspaceDirPath.pathWorkspaceDir + " containing module version " + workspaceDirPath.moduleVersion + '.');
					}
				} else {
					this.userInteractionCallbackPlugin.provideInfo("Workspace directory " + workspaceDirPath.pathWorkspaceDir + " containing module version " + workspaceDirPath.moduleVersion + " up to date with remote repository.");
				}
			} else {
				this.userInteractionCallbackPlugin.provideInfo("Workspace directory " + workspaceDirPath.pathWorkspaceDir + " contains module version " + workspaceDirPath.moduleVersion + " which is static. Static versions are assumed to always be up to date with remote repository.");
			}
		}
	}

	private void commitCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;
		String message;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		message = this.commandLine.getOptionValue("commit-message");

		sortedSetWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: sortedSetWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

			if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
				// Theoretically we should reserve access to the workspace directory. But we do
				// not bother since the tool does not perform deep processing and is not likely to
				// get into a conflicting situation.

				if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.LOCAL_CHANGES_ONLY)) {
					if (message == null) {
						this.userInteractionCallbackPlugin.provideInfo("At least module version " + workspaceDirPath.moduleVersion + " in workspace directory " + workspaceDirPath.pathWorkspaceDir + " has changes to be committed.");
						message = this.userInteractionCallbackPlugin.getInfo("Please specify Commit message for this module and all the others that will need to be committed: ");
					}

					this.userInteractionCallbackPlugin.provideInfo("Committing changes in workspace directory " + workspaceDirPath.pathWorkspaceDir + " containing module version " + workspaceDirPath.moduleVersion + '.');

					scmPlugin.commit(workspaceDirPath.pathWorkspaceDir, message, null);
				} else {
					this.userInteractionCallbackPlugin.provideInfo("No changes to commit in workspace directory " + workspaceDirPath.pathWorkspaceDir + " containing module version " + workspaceDirPath.moduleVersion + '.');
				}
			} else {
				this.userInteractionCallbackPlugin.provideInfo("Workspace directory " + workspaceDirPath.pathWorkspaceDir + " contains module version " + workspaceDirPath.moduleVersion + " which is static. Static versions cannot be committed.");
			}
		}
	}

	private void cleanAllCommand() {
		Set<WorkspaceDir> setWorkspaceDir;
		Set<WorkspaceDirPath> setWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		// First take care of the system workspace directories.

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(WorkspaceDirSystemModule.class);

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Path pathWorkspaceDir;

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try {
				FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
		}

		// Then the user workspace directories.

		setWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: setWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

			if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.LOCAL_CHANGES_ONLY)) {
				this.userInteractionCallbackPlugin.provideInfo("Module version " + workspaceDirPath.moduleVersion + " in workspace directory " + workspaceDirPath.pathWorkspaceDir + " contains unsynchronized local changes and will be deleted.");
			}

			if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
				continue;
			}

			try {
				FileUtils.deleteDirectory(workspaceDirPath.pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);
			this.workspacePlugin.deleteWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion);
		}
	}

	private void cleanSystemCommand() {
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(WorkspaceDirSystemModule.class);

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Path pathWorkspaceDir;

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try {
				FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
		}
	}

	private void removeModuleVersionCommand() {
		ModuleVersion moduleVersion;
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		// Here moduleVersion may not be complete: it may not contain the Version. If that
		// is so and the WorkspacePlugin implementation allows for multiple different
		// Version's of the same Module, setWorkspaceDir below will contain all these
		// occurrences. But in general it is expected that it contains only one
		// ModuleVersion.
		try {
			moduleVersion = ModuleVersion.parse(this.commandLine.getArgs()[1]);
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		workspaceDirUserModuleVersion = new WorkspaceDirUserModuleVersion(moduleVersion);

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(workspaceDirUserModuleVersion);

		if (setWorkspaceDir.isEmpty()) {
			this.userInteractionCallbackPlugin.provideInfo("No user workspace directory corresponds to module version " + moduleVersion + ". No module version removed.");
		}

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Module module;
			ScmPlugin scmPlugin;
			Path pathWorkspaceDir;

			module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);
			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);

			if (!scmPlugin.isSync(pathWorkspaceDir, ScmPlugin.IsSyncFlagEnum.LOCAL_CHANGES_ONLY)) {
				this.userInteractionCallbackPlugin.provideInfo("Module version " + ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion() + " in workspace directory " + pathWorkspaceDir + " contains unsynchronized local changes and will be deleted.");
			}

			if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
				continue;
			}

			try {
				FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);
			this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
		}
	}

	private void buildCleanAllCommand() {
		Set<WorkspaceDirPath> setWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		setWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: setWorkspaceDirPath) {
			Module module;
			BuilderPlugin builderPlugin;

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter("Initiating the clean process for module version " + workspaceDirPath.moduleVersion + " in workspace directory " + workspaceDirPath.pathWorkspaceDir + '.')) {
				builderPlugin.clean(workspaceDirPath.pathWorkspaceDir, writerLog);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			} finally {
				this.workspacePlugin.releaseWorkspaceDir(workspaceDirPath.pathWorkspaceDir);
			}
		}
	}

	private void buildCleanModuleVersionCommand() {
		ModuleVersion moduleVersion;
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		// Here moduleVersion may not be complete: it may not contain the Version. If that
		// is so and the WorkspacePlugin implementation allows for multiple different
		// Version's of the same Module, setWorkspaceDir below will contain all these
		// occurrences. But in general it is expected that it contains only one
		// ModuleVersion.
		try {
			moduleVersion = ModuleVersion.parse(this.commandLine.getArgs()[1]);
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		workspaceDirUserModuleVersion = new WorkspaceDirUserModuleVersion(moduleVersion);

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(workspaceDirUserModuleVersion);

		if (setWorkspaceDir.isEmpty()) {
			this.userInteractionCallbackPlugin.provideInfo("No user workspace directory corresponds to module version " + moduleVersion + ". No module version cleaned.");
		}

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Module module;
			BuilderPlugin builderPlugin;
			Path pathWorkspaceDir;

			module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
			builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirModeEnum.GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter("Initiating the clean process for module version " + ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion() + " in workspace directory " + pathWorkspaceDir + '.')) {
				builderPlugin.clean(pathWorkspaceDir, writerLog);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			} finally {

				this.workspacePlugin.releaseWorkspaceDir(pathWorkspaceDir);
			}
		}
	}

	private void buildCleanDirCommand() {
		Path pathWorkspaceDir;
		WorkspaceDir workspaceDir;
		Module module;
		BuilderPlugin builderPlugin;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT));
		}

		pathWorkspaceDir = ((WorkspaceExecContext)this.execContext).getPathWorkspaceDir().resolve(this.commandLine.getArgs()[1]);

		if (!this.workspacePlugin.isPathWorkspaceDirExists(pathWorkspaceDir)) {
			this.userInteractionCallbackPlugin.provideInfo("The user workspace directory " + pathWorkspaceDir + " does not exist or is not known to the workspace. No module version cleaned.");
			return;
		}

		workspaceDir = this.workspacePlugin.getWorkspaceDirFromPath(pathWorkspaceDir);

		if (!(workspaceDir instanceof WorkspaceDirUserModuleVersion)) {
			this.userInteractionCallbackPlugin.provideInfo("The workspace directory " + pathWorkspaceDir + " is not a user workspace directory. Only user workspace directories can be cleaned. No module version cleaned.");
			return;
		}

		module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
		builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

		try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter("Initiating the clean process for module version " + ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion() + " in workspace directory " + pathWorkspaceDir + '.')) {
			builderPlugin.clean(pathWorkspaceDir, writerLog);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			this.workspacePlugin.releaseWorkspaceDir(pathWorkspaceDir);
		}
	}

	private void fixCommand() {
		// TODO: Not necessarily easy since a directory can contain pretty much anything not expected by Dragom.
		// Maybe have a tool to attempt to recognize a workspace directory.
		throw new RuntimeException("Not implemented yet.");
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(WorkspaceManagerTool.class.getResourceAsStream("WorkspaceManagerToolHelp.txt"), System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private static SortedSet<WorkspaceDirPath> getSortedSetWorkspaceDirPath() {
		WorkspacePlugin workspacePlugin;
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;

		workspacePlugin = ExecContextHolder.get().getExecContextPlugin(WorkspacePlugin.class);
		sortedSetWorkspaceDirPath = new TreeSet<WorkspaceDirPath>();

		for (WorkspaceDir workspaceDir: workspacePlugin.getSetWorkspaceDir(WorkspaceDirUserModuleVersion.class)) {
			WorkspaceManagerTool.WorkspaceDirPath workspaceDirPath;

			workspaceDirPath = new WorkspaceManagerTool.WorkspaceDirPath();

			workspaceDirPath.workspaceDirUserModuleVersion = (WorkspaceDirUserModuleVersion)workspaceDir;
			workspaceDirPath.moduleVersion = ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion();

			// We cannot maintain access to the workspace directory since depending on the
			// action to be performed by the caller different access may be required. The
			// strategy is therefore to not reserve access (WorkspaceDirAccessMode.PEEK)
			// and let the caller reserve access if and when necessary.
			workspaceDirPath.pathWorkspaceDir = workspacePlugin.getWorkspaceDir(workspaceDir, GetWorkspaceDirModeEnum.GET_EXISTING, WorkspaceDirAccessMode.PEEK);

			sortedSetWorkspaceDirPath.add(workspaceDirPath);
		}

		return sortedSetWorkspaceDirPath;
	}
}
