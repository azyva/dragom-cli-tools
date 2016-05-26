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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.Checkout;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.job.RootModuleVersionJobAbstractImpl;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Generic tool wrapper for many classes which derive from
 * {@link RootModuleVersionJobAbstractImpl}.
 * <p>
 * Many jobs, such as {@link Checkout}, which derive from
 * RootModuleVersionJobAbstractImpl, do not require complex invocation arguments
 * and can be invoked by this generic tool wrapper and avoid having to introduce
 * specific tool classes.
 * <p>
 * This tool first expects the following arguments which allows it to identify the
 * RootModuleVersionJobAbstractImpl subclass to use and provide an appropriate
 * help file:
 * <p>
 * <li>Fully qualified name of the RootModuleVersionJobAbstractImpl subclass</li>
 * <li>Text resource for the help file</li>
 * <p>
 * These arguments are not validated as if they were specified by the user. They
 * are expected to be specified by a script that invokes the tool.
 * <p>
 * After these arguments, the regular user-level options and arguments are
 * expected.
 *
 * @author David Raymond
 */
public class GenericRootModuleVersionJobInvokerTool {
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
		String rootModuleVersionJobAbstractImplSubclass;
		String helpRessource;
		DefaultParser defaultParser;
		CommandLine commandLine = null;
		Constructor<? extends RootModuleVersionJobAbstractImpl> constructor;
		RootModuleVersionJobAbstractImpl rootModuleVersionJobAbstractImpl;

		rootModuleVersionJobAbstractImplSubclass = args[0];
		helpRessource = args[1];

		args = Arrays.copyOfRange(args, 2, args.length);

		GenericRootModuleVersionJobInvokerTool.init();

		rootModuleVersionJobAbstractImpl = null;

		try {
			defaultParser = new DefaultParser();

			try {
				commandLine = defaultParser.parse(GenericRootModuleVersionJobInvokerTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				GenericRootModuleVersionJobInvokerTool.help(helpRessource);
			} else {
				args = commandLine.getArgs();

				if (args.length != 0) {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
				}

				CliUtil.setupExecContext(commandLine, true);

				try {
					constructor = Class.forName(rootModuleVersionJobAbstractImplSubclass).asSubclass(RootModuleVersionJobAbstractImpl.class).getConstructor(List.class);
					rootModuleVersionJobAbstractImpl = constructor.newInstance(CliUtil.getListModuleVersionRoot(commandLine));
				} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
					throw new RuntimeException(e);
				}

				rootModuleVersionJobAbstractImpl.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));
				rootModuleVersionJobAbstractImpl.performJob();
			}
		} catch (RuntimeExceptionUserError reue) {
			System.err.println(reue.getMessage());
			System.exit(1);
		} catch (RuntimeException re) {
			re.printStackTrace();
			System.exit(1);
		} finally {
			if ((rootModuleVersionJobAbstractImpl != null) && rootModuleVersionJobAbstractImpl.isListModuleVersionRootChanged()) {
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
		if (!GenericRootModuleVersionJobInvokerTool.indInit) {
			GenericRootModuleVersionJobInvokerTool.options = new Options();

			CliUtil.addStandardOptions(GenericRootModuleVersionJobInvokerTool.options);
			CliUtil.addRootModuleVersionOptions(GenericRootModuleVersionJobInvokerTool.options);

			GenericRootModuleVersionJobInvokerTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help(String ressource) {
		try {
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(GenericRootModuleVersionJobInvokerTool.class, ressource),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}