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
import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.job.TaskInvoker;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Tool wrapper for the TaskInvoker class.
 *
 * Many tools, such as the Checkout tool, can be implemented using TaskPlugin.
 * This class allows invoking a TaskPlugin generically. It avoid having to
 * introduce tool classes only to invoke specific TaskPlugin.
 *
 * This tool first expects the following arguments which allows it to identify the
 * TaskPlugin to invoke:
 *
 * - TaskPlugin ID
 * - Task ID
 * - Text ressource for the help file
 *
 * These arguments are not validated as if they were specified by the user. They
 * are expected to be specified by a script that invokes the tool.
 *
 * After these arguments, the regular user-level options and arguments are
 * expected.
 *
 * @see TaskInvoker
 * @author David Raymond
 */
public class TaskInvokerTool {
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
		String taskPluginId;
		String taskId;
		String helpRessource;
		Parser parser;
		CommandLine commandLine = null;
		TaskInvoker taskInvoker;

		taskPluginId = args[0];
		taskId = args[1];
		helpRessource = args[2];

		args = Arrays.copyOfRange(args, 3, args.length);

		TaskInvokerTool.init();

		taskInvoker = null;

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(TaskInvokerTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				TaskInvokerTool.help(helpRessource);
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length != 0) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
			}

			CliUtil.setupExecContext(commandLine, true);

			taskInvoker = new TaskInvoker(taskPluginId, taskId, CliUtil.getListModuleVersionRoot(commandLine));
			taskInvoker.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));
			taskInvoker.performTask();
		} catch (RuntimeExceptionUserError reue) {
			System.err.println(reue.getMessage());
			System.exit(1);
		} finally {
			if ((taskInvoker != null) && taskInvoker.isListModuleVersionRootChanged()) {
				// It can be the case that RootManager does not specify any root ModuleVersion. In
				// that case calling RootManager.saveListModuleVersion simply saves an empty list,
				// even if the user has specified a root ModuleVersion on the command line.
				RootManager.saveListModuleVersion();
			}

			ExecContextHolder.endToolAndUnset();
		}
	}

	/**
	 * Initializes the class.
	 */
	private synchronized static void init() {
		if (!TaskInvokerTool.indInit) {
			TaskInvokerTool.options = new Options();

			CliUtil.addStandardOptions(TaskInvokerTool.options);
			CliUtil.addRootModuleVersionOptions(TaskInvokerTool.options);

			TaskInvokerTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help(String ressource) {
		try {
			IOUtils.copy(TaskInvokerTool.class.getResourceAsStream(ressource),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
