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
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.reference.ReferencePathMatcher;
import org.azyva.dragom.reference.ReferencePathMatcherByElement;
import org.azyva.dragom.reference.ReferencePathMatcherOr;
import org.azyva.dragom.util.RuntimeExceptionUserError;

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
		Parser parser;
		CommandLine commandLine;
		String command;

		RootManagerTool.init();

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(RootManagerTool.options, args);
			} catch (org.apache.commons.cli.ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				RootManagerTool.help();
				System.exit(0);
			}

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
		if (!RootManagerTool.indInit) {
			Option option;

			RootManagerTool.options = new Options();

			option = new Option(null, null);
			option.setLongOpt("allow-duplicate-modules");
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
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(RootManagerTool.class, "RootManagerToolHelp.txt"),  System.out);
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
		String[] args;
		List<ModuleVersion> listModuleVersion;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		listModuleVersion = RootManager.getListModuleVersion();

		if (listModuleVersion.isEmpty()) {
			System.out.println(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_LIST_OF_ROOT_MODULE_VERSIONS_EMPTY));
		} else {
			for (ModuleVersion moduleVersion: listModuleVersion) {
				System.out.println(moduleVersion.toString());
			}
		}
	}

	/**
	 * Implements the "add" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void addCommand(CommandLine commandLine) {
		String[] args;
		ModuleVersion moduleVersion;

		args = commandLine.getArgs();

		if (args.length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		try {
			moduleVersion = ModuleVersion.parse(args[1]);
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		if (RootManager.containsModuleVersion(moduleVersion)) {
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ALREADY_IN_LIST_OF_ROOTS), moduleVersion));
		} else {
			boolean indAllowDuplicateModule;

			indAllowDuplicateModule = commandLine.hasOption("ind-allow-duplicate-module");

			if (indAllowDuplicateModule) {
				RootManager.addModuleVersion(moduleVersion, true);
				System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
			} else {
				ModuleVersion moduleVersionOrg;

				moduleVersionOrg = RootManager.getModuleVersion(moduleVersion.getNodePath());

				if (moduleVersionOrg != null) {
					RootManager.replaceModuleVersion(moduleVersionOrg, moduleVersion);
					System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REPLACED_IN_LIST_OF_ROOTS), moduleVersionOrg, moduleVersion));
				} else {
					RootManager.addModuleVersion(moduleVersion, false);
					System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_ADDED_TO_LIST_OF_ROOTS), moduleVersion));
				}
			}
		}
	}

	/**
	 * Implements the "remove" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removeCommand(CommandLine commandLine) {
		String[] args;
		ModuleVersion moduleVersion;

		args = commandLine.getArgs();

		if (args.length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		try {
			moduleVersion = ModuleVersion.parse(args[1]);
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		if (!RootManager.containsModuleVersion(moduleVersion)) {
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_NOT_IN_LIST_OF_ROOTS), moduleVersion));
		} else {
			RootManager.removeModuleVersion(moduleVersion);
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_MODULE_VERSION_REMOVED_FROM_LIST_OF_ROOTS), moduleVersion));
		}
	}

	/**
	 * Implements the "remove-all" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removeAllCommand(CommandLine commandLine) {
		String[] args;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		RootManager.removeAllModuleVersion();
		System.out.println(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ALL_MODULE_VERSIONS_REMOVED_FROM_LIST_OF_ROOTS));
	}

	/**
	 * Implements the "list-reference-path-matchers" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void listReferencePathMatchersCommand(CommandLine commandLine) {
		String[] args;
		ReferencePathMatcherOr referencePathMatcherOr;
		List<ReferencePathMatcher> listReferencePathMatcher;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
		listReferencePathMatcher = referencePathMatcherOr.getListReferencePathMatcher();

		if (listReferencePathMatcher.isEmpty()) {
			System.out.println(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_LIST_REFERENCE_PATH_MATCHERS_EMPTY));
		} else {
			for (ReferencePathMatcher referencePathMatcher: listReferencePathMatcher) {
				System.out.println(referencePathMatcher.toString());
			}
		}
	}

	/**
	 * Implements the "add-reference-path-matcher" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void addReferencePathMatcherCommand(CommandLine commandLine) {
		String[] args;
		ReferencePathMatcherOr referencePathMatcherOr;
		ReferencePathMatcherByElement referencePathMatcherByElement;

		args = commandLine.getArgs();

		if (args.length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
		try {
			referencePathMatcherByElement = ReferencePathMatcherByElement.parse(args[1], ExecContextHolder.get().getModel());
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		if (referencePathMatcherOr.getListReferencePathMatcher().contains(referencePathMatcherByElement)) {
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ALREADY_IN_LIST), referencePathMatcherByElement));
		} else {
			referencePathMatcherOr.addReferencePathMatcher(referencePathMatcherByElement);
			RootManager.saveReferencePathMatcherOr();
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_ADDED_TO_LIST), referencePathMatcherByElement));
		}
	}

	/**
	 * Implements the "remove-reference-path-matcher" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removeReferencePathMatcherCommand(CommandLine commandLine) {
		String[] args;
		ReferencePathMatcherOr referencePathMatcherOr;
		ReferencePathMatcherByElement referencePathMatcherByElement;

		args = commandLine.getArgs();

		if (args.length != 2) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		referencePathMatcherOr = RootManager.getReferencePathMatcherOr();

		try {
			referencePathMatcherByElement = ReferencePathMatcherByElement.parse(args[1], ExecContextHolder.get().getModel());
		} catch (ParseException pe) {
			throw new RuntimeExceptionUserError(pe.getMessage());
		}

		if (!referencePathMatcherOr.getListReferencePathMatcher().contains(referencePathMatcherByElement)) {
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_NOT_IN_LIST), referencePathMatcherByElement));
		} else {
			referencePathMatcherOr.getListReferencePathMatcher().remove(referencePathMatcherByElement);
			System.out.println(MessageFormat.format(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST), referencePathMatcherByElement));
			RootManager.saveReferencePathMatcherOr();
		}
	}

	/**
	 * Implements the "remove-all-reference-path-matchers" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void removeAllReferencePathMatchersCommand(CommandLine commandLine) {
		String[] args;
		ReferencePathMatcherOr referencePathMatcherOr;

		args = commandLine.getArgs();

		if (args.length != 1) {
			throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
		}

		referencePathMatcherOr = RootManager.getReferencePathMatcherOr();
		referencePathMatcherOr.getListReferencePathMatcher().clear();

		RootManager.saveReferencePathMatcherOr();
		System.out.println(RootManagerTool.resourceBundle.getString(RootManagerTool.MSG_PATTERN_KEY_ALL_REFERENCE_PATH_MATCHER_REMOVED_FROM_LIST));
	}
}
