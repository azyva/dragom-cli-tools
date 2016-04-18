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
import org.azyva.dragom.job.MergeReferenceGraph;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Main class for the merge-reference-graph.
 * <p>
 * Tool wrapper for {@link MergeReferenceGraph}.
 *
 * See the help information displayed by {@link MergeBaseTool#help}
 * method.
 *
 * @author David Raymond
 */
public class MergeBaseTool {
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
		MergeReferenceGraph mergeReferenceGraph;

		MergeBaseTool.init();

		mergeReferenceGraph = null;

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(MergeBaseTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				MergeBaseTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length != 0) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
			}

			CliUtil.setupExecContext(commandLine, true);

			mergeReferenceGraph = new MergeReferenceGraph(CliUtil.getListModuleVersionRoot(commandLine));
			mergeReferenceGraph.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));
			??? any other options?
			mergeReferenceGraph.performJob();
		} catch (RuntimeExceptionUserError reue) {
			System.err.println(reue.getMessage());
			System.exit(1);
		} finally {
			if ((mergeReferenceGraph != null) && mergeReferenceGraph.isListModuleVersionRootChanged()) {
				// It can be the case that RootManager does not specify any root ModuleVersion. In
				// that case calling RootManager.saveListModuleVersion simply saves an empty list,
				// even if the user has specified a root ModuleVersion on the command line.
				??? Can this list be modified?
				RootManager.saveListModuleVersion();
			}

			ExecContextHolder.endToolAndUnset();
		}
	}

	/**
	 * Initializes the class.
	 */
	private synchronized static void init() {
		if (!MergeBaseTool.indInit) {
			MergeBaseTool.options = new Options();

			CliUtil.addStandardOptions(MergeBaseTool.options);
			CliUtil.addRootModuleVersionOptions(MergeBaseTool.options);

			MergeBaseTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(MergeBaseTool.class, "MergeReferenceGraphToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
