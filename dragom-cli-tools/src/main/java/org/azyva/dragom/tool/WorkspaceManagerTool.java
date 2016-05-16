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
import org.azyva.dragom.execcontext.plugin.WorkspacePlugin.GetWorkspaceDirMode;
import org.azyva.dragom.execcontext.plugin.WorkspacePlugin.WorkspaceDirAccessMode;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.BuildReferenceGraph;
import org.azyva.dragom.model.Model;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.model.Version;
import org.azyva.dragom.model.VersionType;
import org.azyva.dragom.model.plugin.BuilderPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin.BaseVersion;
import org.azyva.dragom.reference.ReferenceGraph;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;
import org.azyva.dragom.util.YesAlwaysNoUserResponse;

/**
 * Tool for managing the workspace.
 *
 * See the help information displayed by the WorkspaceManagerTool.help method.
 *
 * @author David Raymond
 */
public class WorkspaceManagerTool {
	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_WORKSPACE_DIRECTORY = "STATUS_WORKSPACE_DIRECTORY";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_MODULE_VERSION = "STATUS_MODULE_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_SCM_TYPE = "STATUS_SCM_TYPE";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_SCM_URL = "STATUS_SCM_URL";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_VERSION = "STATUS_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_BASE_VERSION = "STATUS_BASE_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_HAS_UNSYNC_LOCAL_CHANGES = "STATUS_HAS_UNSYNC_LOCAL_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_STATUS_HAS_UNSYNC_REMOTE_CHANGES = "STATUS_HAS_UNSYNC_REMOTE_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_UPDATE_UPDATING = "UPDATE_UPDATING";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_UPDATE_CONFLICTS_WHILE_UPDATING = "UPDATE_CONFLICTS_WHILE_UPDATING";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_UPDATE_NO_UNSYNC_REMOTE_CHANGES = "UPDATE_NO_UNSYNC_REMOTE_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_UPDATE_NO_UPDATE_STATIC_VERSION = "UPDATE_NO_UPDATE_STATIC_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_HAS_LOCAL_UNSYNC_CHANGES = "COMMIT_HAS_LOCAL_UNSYNC_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_SPECIFY_MESSAGE = "COMMIT_SPECIFY_MESSAGE";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_REUSE_COMMIT_MESSAGE = "COMMIT_REUSE_COMMIT_MESSAGE";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_COMMITTING = "COMMIT_COMMITTING";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_NO_UNSYNC_LOCAL_CHANGES = "COMMIT_NO_UNSYNC_LOCAL_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_COMMIT_NO_COMMIT_STATIC_VERSION = "COMMIT_NO_COMMIT_STATIC_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES = "DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY = "DELETE_WORKSPACE_DIRECTORY";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_NO_WORKSPACE_DIRECTORY_FOR_MODULE_VERSION = "NO_WORKSPACE_DIRECTORY_FOR_MODULE_VERSION";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_UNKNOWN = "WORKSPACE_DIRECTORY_UNKNOWN";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_NOT_USER = "WORKSPACE_DIRECTORY_NOT_USER";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_CLEAN = "CLEAN";

	/**
	 * ResourceBundle specific to this class.
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(WorkspaceManagerTool.class.getName() + "ResourceBundle");

	/**
	 * Indicates that the class has been initialized.
	 */
	private static boolean indInit;

	/**
	 * Options for parsing the command line.
	 */
	private static Options options;

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
		/**
		 * WorkspaceDirUserModuleVesion.
		 */
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;

		/**
		 * ModuleVersion.
		 * <p>
		 * This is redundant with workspaceDirUserModuleVersion.getModuleVersion(), but is
		 * convenient since often used.
		 */
		ModuleVersion moduleVersion;

		/**
		 * Path to the workspace directory.
		 */
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
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				WorkspaceManagerTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length < 1) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
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
				} else if (command.equals("clean-non-root-reachable")) {
					workspaceManagerTool.cleanNonRootReachableCommand();
				} else if (command.equals("remove-module-version")) {
					workspaceManagerTool.removeModuleVersionCommand();
				} else if (command.equals("remove-dir")) {
					workspaceManagerTool.removeDirCommand();
				} else if (command.equals("build-clean-all")) {
					workspaceManagerTool.buildCleanAllCommand();
				} else if (command.equals("build-clean-module-version")) {
					workspaceManagerTool.buildCleanModuleVersionCommand();
				} else if (command.equals("build-clean-dir")) {
					workspaceManagerTool.buildCleanDirCommand();
				} else if (command.equals("fix")) {
					workspaceManagerTool.fixCommand();
				} else {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_COMMAND), command, CliUtil.getHelpCommandLineOption()));
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

			WorkspaceManagerTool.indInit = true;
		}
	}

	/**
	 * Implements the "status" command.
	 */
	private void statusCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
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

			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_WORKSPACE_DIRECTORY), workspaceDirPath.pathWorkspaceDir)).append('\n');
			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_MODULE_VERSION), workspaceDirPath.moduleVersion)).append('\n');

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_SCM_TYPE), scmPlugin.getScmType())).append('\n');
			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_SCM_URL), scmPlugin.getScmUrl(workspaceDirPath.pathWorkspaceDir))).append('\n');

			version = scmPlugin.getVersion(workspaceDirPath.pathWorkspaceDir);

			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_VERSION), version)).append('\n');

			baseVersion = scmPlugin.getBaseVersion(version);
			stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_BASE_VERSION), baseVersion == null ? null : baseVersion.versionBase)).append('\n');

			if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
				stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_HAS_UNSYNC_LOCAL_CHANGES), !scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY))).append('\n');
				stringBuilder.append(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_STATUS_HAS_UNSYNC_REMOTE_CHANGES), !scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.REMOTE_CHANGES_ONLY))).append('\n');
			}

			this.userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
		}
	}

	/**
	 * Implements the "update" command.
	 */
	private void updateCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		sortedSetWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: sortedSetWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try {
				module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());

				scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

				if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
					// Theoretically we should reserve access to the workspace directory. But we do
					// not bother since the tool does not perform deep processing and is not likely to
					// get into a conflicting situation.

					if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.REMOTE_CHANGES_ONLY)) {
						this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_UPDATE_UPDATING), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

						if (scmPlugin.update(workspaceDirPath.pathWorkspaceDir)) {
							this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_UPDATE_CONFLICTS_WHILE_UPDATING), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));
						}
					} else {
						this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_UPDATE_NO_UNSYNC_REMOTE_CHANGES), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));
					}
				} else {
					this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_UPDATE_NO_UPDATE_STATIC_VERSION), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));
				}
			} finally {
				this.workspacePlugin.releaseWorkspaceDir(workspaceDirPath.pathWorkspaceDir);
			}
		}
	}

	/**
	 * Implements the "commit" command.
	 */
	private void commitCommand() {
		SortedSet<WorkspaceDirPath> sortedSetWorkspaceDirPath;
		String message;
		boolean indReuseCommitMessage;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		message = this.commandLine.getOptionValue("commit-message");

		// If commit message specified on command line, reuse it for all Modules.
		indReuseCommitMessage = (message != null);

		sortedSetWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: sortedSetWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try {
				module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
				scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

				if (workspaceDirPath.moduleVersion.getVersion().getVersionType() == VersionType.DYNAMIC) {
					// Theoretically we should reserve access to the workspace directory. But we do
					// not bother since the tool does not perform deep processing and is not likely to
					// get into a conflicting situation.

					if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY)) {
						this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_HAS_LOCAL_UNSYNC_CHANGES), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

						if (!indReuseCommitMessage) {
							message = this.userInteractionCallbackPlugin.getInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_SPECIFY_MESSAGE), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

							Util.getInfoYesNoUserResponse(this.userInteractionCallbackPlugin, WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_REUSE_COMMIT_MESSAGE), YesAlwaysNoUserResponse.YES);
						}

						this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_COMMITTING), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

						if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_COMMIT)) {
							continue;
						}

						scmPlugin.commit(workspaceDirPath.pathWorkspaceDir, message, null);
					} else {
						this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_NO_UNSYNC_LOCAL_CHANGES), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));
					}
				} else {
					this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_COMMIT_NO_COMMIT_STATIC_VERSION), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));
				}
			} finally {
				this.workspacePlugin.releaseWorkspaceDir(workspaceDirPath.pathWorkspaceDir);
			}
		}
	}

	/**
	 * Implements the "clean-all" command.
	 */
	private void cleanAllCommand() {
		Set<WorkspaceDir> setWorkspaceDir;
		Set<WorkspaceDirPath> setWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		// First take care of the system workspace directories.

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(WorkspaceDirSystemModule.class);

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Path pathWorkspaceDir;

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

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

			if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY)) {
				this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

				if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
					continue;
				}
			} else {
				this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

				if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY)) {
					continue;
				}
			}

			try {
				FileUtils.deleteDirectory(workspaceDirPath.pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);
			this.workspacePlugin.deleteWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion);
		}
	}

	/**
	 * Implements the "clean-system" command.
	 */
	private void cleanSystemCommand() {
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		setWorkspaceDir = this.workspacePlugin.getSetWorkspaceDir(WorkspaceDirSystemModule.class);

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Path pathWorkspaceDir;

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try {
				FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
		}
	}

	/**
	 * Implements the "clean-non-root-reachable" command.
	 */
	private void cleanNonRootReachableCommand() {
		BuildReferenceGraph buildReferenceGraph;
		ReferenceGraph referenceGraph;
		Set<WorkspaceDirPath> setWorkspaceDirPath;

		// Here this.commandLine is not expected to contain the root-module-version
		// option, as supported by CliUtil.getListModuleVersionRoot. Therefore a List of
		// root ModuleVersion must be defined within the ExecContext.
		buildReferenceGraph = new BuildReferenceGraph(null, CliUtil.getListModuleVersionRoot(this.commandLine));

		// Also, this.commandLine is not expected to contain any reference-path-matcher
		// options, as supported by CliUtil.getReferencePathMatcher.
		buildReferenceGraph.setReferencePathMatcher(CliUtil.getReferencePathMatcher(this.commandLine));

		// The idea for the above expectations is that the clean-non-root-reachable
		// command is specifically intended to be applied in the context of the root
		// ModuleVersion's defined within the ExecContext.

		buildReferenceGraph.performJob();

		referenceGraph = buildReferenceGraph.getReferenceGraph();

		this.workspacePlugin = ExecContextHolder.get().getExecContextPlugin(WorkspacePlugin.class);
		setWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: setWorkspaceDirPath) {
			Module module;
			ScmPlugin scmPlugin;

			if (!referenceGraph.moduleVersionExists(workspaceDirPath.moduleVersion)) {
				module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
				scmPlugin = module.getNodePlugin(ScmPlugin.class, null);

				if (!scmPlugin.isSync(workspaceDirPath.pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY)) {
					this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

					if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
						continue;
					}
				} else {
					this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion));

					if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY)) {
						continue;
					}
				}

				try {
					FileUtils.deleteDirectory(workspaceDirPath.pathWorkspaceDir.toFile());
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}

				this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);
				this.workspacePlugin.deleteWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion);
			}
		}
	}

	/**
	 * Implements the "remove-module-version" command.
	 */
	private void removeModuleVersionCommand() {
		ModuleVersion moduleVersion;
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
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
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_NO_WORKSPACE_DIRECTORY_FOR_MODULE_VERSION), moduleVersion));
		}

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Module module;
			ScmPlugin scmPlugin;
			Path pathWorkspaceDir;

			module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
			scmPlugin = module.getNodePlugin(ScmPlugin.class, null);
			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);

			if (!scmPlugin.isSync(pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY)) {
				this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()));

				if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
					continue;
				}
			} else {
				this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()));

				if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY)) {
					continue;
				}
			}

			try {
				FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);
			this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
		}
	}

	/**
	 * Implements the "remove-dir" command.
	 */
	private void removeDirCommand() {
		Path pathWorkspaceDir;
		WorkspaceDir workspaceDir;
		Module module;
		ScmPlugin scmPlugin;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		pathWorkspaceDir = ((WorkspaceExecContext)this.execContext).getPathWorkspaceDir().resolve(this.commandLine.getArgs()[1]);

		if (!this.workspacePlugin.isPathWorkspaceDirExists(pathWorkspaceDir)) {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_UNKNOWN), pathWorkspaceDir));
			return;
		}

		workspaceDir = this.workspacePlugin.getWorkspaceDirFromPath(pathWorkspaceDir);

		if (!(workspaceDir instanceof WorkspaceDirUserModuleVersion)) {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_NOT_USER), pathWorkspaceDir));
			return;
		}

		module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
		scmPlugin = module.getNodePlugin(ScmPlugin.class, null);
		pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);


		if (!scmPlugin.isSync(pathWorkspaceDir, ScmPlugin.IsSyncFlag.LOCAL_CHANGES_ONLY)) {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY_UNSYNC_LOCAL_CHANGES), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()));

			if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY_WITH_UNSYNC_LOCAL_CHANGES)) {
				return;
			}
		} else {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()));

			if (!Util.handleDoYouWantToContinue(Util.DO_YOU_WANT_TO_CONTINUE_CONTEXT_DELETE_WORKSPACE_DIRECTORY)) {
				return;
			}
		}

		try {
			FileUtils.deleteDirectory(pathWorkspaceDir.toFile());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.PEEK);
		this.workspacePlugin.deleteWorkspaceDir(workspaceDir);
	}

	/**
	 * Implements the "build-clean-all" command.
	 */
	private void buildCleanAllCommand() {
		Set<WorkspaceDirPath> setWorkspaceDirPath;

		if (this.commandLine.getArgs().length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		setWorkspaceDirPath = WorkspaceManagerTool.getSortedSetWorkspaceDirPath();

		for (WorkspaceDirPath workspaceDirPath: setWorkspaceDirPath) {
			Module module;
			BuilderPlugin builderPlugin;

			module = this.model.getModule(workspaceDirPath.moduleVersion.getNodePath());
			builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

			this.workspacePlugin.getWorkspaceDir(workspaceDirPath.workspaceDirUserModuleVersion, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), workspaceDirPath.pathWorkspaceDir, workspaceDirPath.moduleVersion))) {
				builderPlugin.clean(workspaceDirPath.pathWorkspaceDir, writerLog);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			} finally {
				this.workspacePlugin.releaseWorkspaceDir(workspaceDirPath.pathWorkspaceDir);
			}
		}
	}

	/**
	 * Implements the "build-clean-module-version" command.
	 */
	private void buildCleanModuleVersionCommand() {
		ModuleVersion moduleVersion;
		WorkspaceDirUserModuleVersion workspaceDirUserModuleVersion;
		Set<WorkspaceDir> setWorkspaceDir;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
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
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_NO_WORKSPACE_DIRECTORY_FOR_MODULE_VERSION), moduleVersion));
		}

		for (WorkspaceDir workspaceDir: setWorkspaceDir) {
			Module module;
			BuilderPlugin builderPlugin;
			Path pathWorkspaceDir;

			module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
			builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

			pathWorkspaceDir = this.workspacePlugin.getWorkspaceDir(workspaceDir, WorkspacePlugin.GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspacePlugin.WorkspaceDirAccessMode.READ_WRITE);

			try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_DELETE_WORKSPACE_DIRECTORY), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()))) {
				builderPlugin.clean(pathWorkspaceDir, writerLog);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			} finally {
				this.workspacePlugin.releaseWorkspaceDir(pathWorkspaceDir);
			}
		}
	}

	/**
	 * Implements the "build-clean-dir" command.
	 */
	private void buildCleanDirCommand() {
		Path pathWorkspaceDir;
		WorkspaceDir workspaceDir;
		Module module;
		BuilderPlugin builderPlugin;

		if (this.commandLine.getArgs().length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		pathWorkspaceDir = ((WorkspaceExecContext)this.execContext).getPathWorkspaceDir().resolve(this.commandLine.getArgs()[1]);

		if (!this.workspacePlugin.isPathWorkspaceDirExists(pathWorkspaceDir)) {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_UNKNOWN), pathWorkspaceDir));
			return;
		}

		workspaceDir = this.workspacePlugin.getWorkspaceDirFromPath(pathWorkspaceDir);

		if (!(workspaceDir instanceof WorkspaceDirUserModuleVersion)) {
			this.userInteractionCallbackPlugin.provideInfo(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_WORKSPACE_DIRECTORY_NOT_USER), pathWorkspaceDir));
			return;
		}

		module = this.model.getModule(((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion().getNodePath());
		builderPlugin = module.getNodePlugin(BuilderPlugin.class, null);

		try (Writer writerLog = this.userInteractionCallbackPlugin.provideInfoWithWriter(MessageFormat.format(WorkspaceManagerTool.resourceBundle.getString(WorkspaceManagerTool.MSG_PATTERN_KEY_CLEAN), pathWorkspaceDir, ((WorkspaceDirUserModuleVersion)workspaceDir).getModuleVersion()))) {
			builderPlugin.clean(pathWorkspaceDir, writerLog);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			this.workspacePlugin.releaseWorkspaceDir(pathWorkspaceDir);
		}
	}

	/**
	 * Implements the "fix" command.
	 */
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
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(WorkspaceManagerTool.class, "WorkspaceManagerToolHelp.txt"), System.out);
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
			workspaceDirPath.pathWorkspaceDir = workspacePlugin.getWorkspaceDir(workspaceDir, GetWorkspaceDirMode.ENUM_SET_GET_EXISTING, WorkspaceDirAccessMode.PEEK);

			sortedSetWorkspaceDirPath.add(workspaceDirPath);
		}

		return sortedSetWorkspaceDirPath;
	}
}
