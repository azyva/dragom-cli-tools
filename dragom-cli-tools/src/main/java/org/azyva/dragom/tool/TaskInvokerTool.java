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
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.TaskInvoker;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

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
	 * Logger for the class.
	 */
	//private static final Logger logger = LoggerFactory.getLogger(TaskInvokerTool.class);

	/**
	 * Name of the ResourceBundle of the class.
	 */
	public static final String RESOURCE_BUNDLE = "org/azyva/tool/TaskInvokerToolResourceBundle";

	/**
	 * Indicates that the class has been initialized.
	 */
	private static boolean indInit;

	@SuppressWarnings("unused")
	/**
	 * ResourceBundle specific to this class.
	 */
	private static ResourceBundle resourceBundle;

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

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(TaskInvokerTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(Util.getResourceBundle(), Util.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE, pe.getMessage());
			}

			if (commandLine.hasOption("help")) {
				TaskInvokerTool.help(helpRessource);
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length != 0) {
				throw new RuntimeExceptionUserError(Util.getResourceBundle(), Util.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT);
			}

			Util.setupExecContext(commandLine, true);

			taskInvoker = new TaskInvoker(taskPluginId, taskId, Util.getListModuleVersionRoot(commandLine));
			taskInvoker.setReferencePathMatcher(Util.getReferencePathMatcher(commandLine));

			taskInvoker.performTask();
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
		if (!TaskInvokerTool.indInit) {
			Option option;

			TaskInvokerTool.options = new Options();

			option = new Option(null, null);
			option.setLongOpt("root-module-version");
			option.setArgs(1);
			TaskInvokerTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("reference-path-matcher");
			option.setArgs(1);
			TaskInvokerTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("help");
			TaskInvokerTool.options.addOption(option);

			Util.addStandardOptions(TaskInvokerTool.options);

			TaskInvokerTool.resourceBundle = ResourceBundle.getBundle(TaskInvokerTool.RESOURCE_BUNDLE);

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
