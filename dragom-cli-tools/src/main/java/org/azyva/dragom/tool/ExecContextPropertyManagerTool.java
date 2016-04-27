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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.ExecContext;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Class for managing {@link ExecContext} properties.
 * <p>
 * This class is not really a job but is a helper class allowing the user to
 * manage ExecContext properties.
 *
 * @author David Raymond
 */
public class ExecContextPropertyManagerTool {
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
		CommandLine commandLine;
		String command;

		ExecContextPropertyManagerTool.init();

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(ExecContextPropertyManagerTool.options, args);
			} catch (org.apache.commons.cli.ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				ExecContextPropertyManagerTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length < 1) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
			}

			CliUtil.setupExecContext(commandLine, true);

			command = args[0];

			if (command.equals("get-properties")) {
				ExecContextPropertyManagerTool.getPropertiesCommand(commandLine);
			} else if (command.equals("get-property")) {
				ExecContextPropertyManagerTool.getPropertyCommand(commandLine);
			} else if (command.equals("set-property")) {
				ExecContextPropertyManagerTool.setPropertyCommand(commandLine);
			} else if (command.equals("remove-property")) {
				ExecContextPropertyManagerTool.removePropertyCommand(commandLine);
			} else if (command.equals("remove-properties")) {
				ExecContextPropertyManagerTool.removePropertiesCommand(commandLine);
			} else if (command.equals("get-init-properties")) {
				ExecContextPropertyManagerTool.getInitPropertiesCommand(commandLine);
			} else if (command.equals("get-init-property")) {
				ExecContextPropertyManagerTool.getInitPropertyCommand(commandLine);
			} else {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_COMMAND), command, CliUtil.getHelpCommandLineOption()));
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
		if (!ExecContextPropertyManagerTool.indInit) {
			ExecContextPropertyManagerTool.options = new Options();

			CliUtil.addStandardOptions(ExecContextPropertyManagerTool.options);

			ExecContextPropertyManagerTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(RootManagerTool.class, "ExecContextPropertyManagerToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * Implements the "get-properties" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void getPropertiesCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;
		Set<String> setProperty;
		List<String> listProperty;

		args = commandLine.getArgs();

		if (args.length > 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		setProperty = execContext.getSetProperty((args.length == 1) ? args[0] : null);
		listProperty = new ArrayList<String>(setProperty);

		Collections.sort(listProperty);

		for (String property: listProperty) {
			System.out.println(property + "=" + execContext.getProperty(property));
		}
	}

	/**
	 * Implements the "get-property" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void getPropertyCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		System.out.println(args[0] + "=" + execContext.getProperty(args[0]));
	}

	/**
	 * Implements the "set-property" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void setPropertyCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;

		args = commandLine.getArgs();

		if (args.length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		execContext.setProperty(args[0], args[1]);

		System.out.println(args[0] + "=" + args[1]);
	}

	/**
	 * Implements the "remove-property" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removePropertyCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		execContext.removeProperty(args[0]);
	}

	/**
	 * Implements the "remove-properties" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removePropertiesCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		execContext.removeProperties(args[0]);
	}

	/**
	 * Implements the "get-init-properties" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void getInitPropertiesCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;
		Set<String> setInitProperty;
		List<String> listInitProperty;

		args = commandLine.getArgs();

		if (args.length != 0) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		setInitProperty = execContext.getSetInitProperty();
		listInitProperty = new ArrayList<String>(setInitProperty);

		Collections.sort(listInitProperty);

		for (String initProperty: listInitProperty) {
			System.out.println(initProperty + "=" + execContext.getInitProperty(initProperty));
		}
	}

	/**
	 * Implements the "get-init-property" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void getInitPropertyCommand(CommandLine commandLine) {
		String[] args;
		ExecContext execContext;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		execContext = ExecContextHolder.get();

		System.out.println(args[0] + "=" + execContext.getInitProperty(args[0]));
	}
}