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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.job.SwitchToDynamicVersion;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Main class for the tool switch-to-dynamic-version tool.
 *
 * Tool wrapper for the SwitchToDynamicVersion class.
 *
 * See the help information displayed by the SwitchToDynamicVersionTool.help
 * method.
 *
 * @author David Raymond
 */
public class SwitchToDynamicVersionTool {
	/**
	 * Logger for the class.
	 */
	//private static final Logger logger = LoggerFactory.getLogger(SwitchToDynamicVersionTool.class);

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
		Parser parser;
		CommandLine commandLine = null;
		SwitchToDynamicVersion switchToDynamicVersion;

		SwitchToDynamicVersionTool.init();

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(SwitchToDynamicVersionTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				SwitchToDynamicVersionTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length != 0) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
			}

			CliUtil.setupExecContext(commandLine, true);

			switchToDynamicVersion = new SwitchToDynamicVersion(CliUtil.getListModuleVersionRoot(commandLine));
			switchToDynamicVersion.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));

			// It can be the case that RootManager does not specify any root ModuleVersion. In
			// that case calling RootManager.saveListModuleVersion simply saves an empty list,
			// even if the user has specified a root ModuleVersion on the command line.
			if (switchToDynamicVersion.performTask()) {
				RootManager.saveListModuleVersion();
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
		if (!SwitchToDynamicVersionTool.indInit) {
			SwitchToDynamicVersionTool.options = new Options();

			CliUtil.addStandardOptions(SwitchToDynamicVersionTool.options);
			CliUtil.addRootModuleVersionOptions(SwitchToDynamicVersionTool.options);

			SwitchToDynamicVersionTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		//TODO: Adjust help for roots
		//TODO: Explain that all occurrences that match the graph path are switched.
		//TODO: Maybe none? (options)
		//TODO: Document --no-confirm, --new-version, --base-version and --commit-behavior
		// TODO: Maybe should allow specifying a module in different ways: SCM URL? Artifact coordinates?
		//TODO: Is there a difference between source and artifact version? (version)
		//TODO: Not sure that artifact references are meaningful here, in a reference graph?
		//If A refers to a submodule of B, is the path specifically to the submodule or canonically to the main module?
		try {
			IOUtils.copy(SwitchToDynamicVersionTool.class.getResourceAsStream("SwitchToDynamicVersionToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
