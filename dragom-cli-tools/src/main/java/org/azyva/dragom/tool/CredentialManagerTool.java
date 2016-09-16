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
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.plugin.CredentialStorePlugin;
import org.azyva.dragom.execcontext.plugin.UserInteractionCallbackPlugin;
import org.azyva.dragom.execcontext.plugin.impl.DefaultCredentialStorePluginImpl;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Tool wrapper for the RootManager class.
 *
 * See the help information displayed by the RootManagerTool.help method.
 *
 * @author David Raymond
 */
public class CredentialManagerTool {
	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_USER = "USER";

	/**
	 * See description in ResourceBundle.
	 */
	private static final String MSG_PATTERN_KEY_REALM = "REALM";

	/**
	 * ResourceBundle specific to this class.
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(CredentialManagerTool.class.getName() + "ResourceBundle");

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

		CredentialManagerTool.init();

		try {
			defaultParser = new DefaultParser();

			try {
				commandLine = defaultParser.parse(CredentialManagerTool.options, args);
			} catch (org.apache.commons.cli.ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				CredentialManagerTool.help();
			} else {
				args = commandLine.getArgs();

				if (args.length == 0) {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
				}

				CliUtil.setupExecContext(commandLine, true);

				command = args[0];

				if (command.equals("enum-resource-realm-mappings")) {
					CredentialManagerTool.enumResourceRealmMappingsCommand(commandLine);
				} else if (command.equals("enum-passwords")) {
					CredentialManagerTool.enumPasswordsCommand(commandLine);
				} else if (command.equals("get-password")) {
					CredentialManagerTool.getPasswordCommand(commandLine);
				} else if (command.equals("enum-default-users")) {
					CredentialManagerTool.enumDefaultUsersCommand(commandLine);
				} else if (command.equals("set-password")) {
					CredentialManagerTool.setPasswordCommand(commandLine);
				} else if (command.equals("remove-password")) {
					CredentialManagerTool.removePasswordCommand(commandLine);
				} else if (command.equals("set-default-user")) {
					CredentialManagerTool.setDefaultUserCommand(commandLine);
				} else if (command.equals("remove-default-user")) {
					CredentialManagerTool.removeDefaultUserCommand(commandLine);
				} else {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_COMMAND), command, CliUtil.getHelpCommandLineOption()));
				}
			}
		} catch (RuntimeExceptionUserError reue) {
			System.err.println(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_USER_ERROR_PREFIX) + reue.getMessage());
			System.exit(1);
		} catch (RuntimeException re) {
			re.printStackTrace();
			System.exit(1);
		} finally {
			ExecContextHolder.endToolAndUnset();
		}
	}

	/**
	 * Initializes the class.
	 */
	private synchronized static void init() {
		if (!CredentialManagerTool.indInit) {
			Option option;

			CredentialManagerTool.options = new Options();

			CliUtil.addStandardOptions(CredentialManagerTool.options);

			CredentialManagerTool.indInit = true;
		}
	}

	/**
	 * Implements the "enum-resource-realm-mappings" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void enumResourceRealmMappingsCommand(CommandLine commandLine) {
		UserInteractionCallbackPlugin userInteractionCallbackPlugin;
		DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;
		List<DefaultCredentialStorePluginImpl.ResourcePatternRealmUser> listResourcePatternRealmUser;
		StringBuilder stringBuilder;

		userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
		defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

		listResourcePatternRealmUser = defaultCredentialStorePluginImpl.getListResourcePatternRealmUser();

		stringBuilder = new StringBuilder();

		for (DefaultCredentialStorePluginImpl.ResourcePatternRealmUser resourcePatternRealmUser: listResourcePatternRealmUser) {
			stringBuilder.append(resourcePatternRealmUser.patternResource.toString()).append(" -> ").append(resourcePatternRealmUser.realm);

			if (resourcePatternRealmUser.user != null) {
				stringBuilder
						.append(" (")
						.append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_USER))
						.append(": ")
						.append(resourcePatternRealmUser.user)
						.append(')');
			}

			stringBuilder.append('\n');
		}

		if (stringBuilder.length() != 0) {
			// Remove the useless trailing linefeed.
			stringBuilder.setLength(stringBuilder.length() - 1);

			userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
		}
	}

	/**
	 * Implements the "enum-passwords" command.
	 *
	 * @param commandLine CommandLine.
	 */
	private static void enumPasswordsCommand(CommandLine commandLine) {
		UserInteractionCallbackPlugin userInteractionCallbackPlugin;
		DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;
		List<DefaultCredentialStorePluginImpl.ResourcePatternRealmUser> listResourcePatternRealmUser;
		StringBuilder stringBuilder;

		userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
		defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

		listResourcePatternRealmUser = defaultCredentialStorePluginImpl.getListRealmUser();

		stringBuilder = new StringBuilder();

		for (DefaultCredentialStorePluginImpl.ResourcePatternRealmUser resourcePatternRealmUser: listResourcePatternRealmUser) {
			stringBuilder.append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_REALM)).append(": ").append(resourcePatternRealmUser.realm).append(" -> ").append(resourcePatternRealmUser.realm);

			if (resourcePatternRealmUser.user != null) {
				stringBuilder
						.append(" (")
						.append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_REALM))
						.append(": ")
						.append(resourcePatternRealmUser.realm)
						.append(' ')
						.append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_USER))
						.append(": ")
						.append(resourcePatternRealmUser.user)
						.append('\n');
			}
		}

		if (stringBuilder.length() != 0) {
			// Remove the useless trailing linefeed.
			stringBuilder.setLength(stringBuilder.length() - 1);

			userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
		}
	}

	private static void getPasswordCommand(CommandLine commandLine) {

	}

	private static void enumDefaultUsersCommand(CommandLine commandLine) {

	}

	private static void setPasswordCommand(CommandLine commandLine) {

	}

	private static void removePasswordCommand(CommandLine commandLine) {

	}

	private static void setDefaultUserCommand(CommandLine commandLine) {

	}

	private static void removeDefaultUserCommand(CommandLine commandLine) {

	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(CredentialManagerTool.class, "CredentialManagerToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}
