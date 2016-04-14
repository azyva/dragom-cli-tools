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
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.BuildReferenceGraph;
import org.azyva.dragom.job.ReferenceGraphReport;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Tool for producing a reference graph report.
 *
 * See the help information displayed by the ReferenceGraphReportTool.help method.
 *
 * @author David Raymond
 */
public class ReferenceGraphReportTool {
	/**
	 * See description in ResourceBundle.
	 */
	public static final String MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE = "ERROR_PARSING_COMMAND_LINE";

	/**
	 * ResourceBundle specific to this class.
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ReferenceGraphReportTool.class.getName() + "ResourceBundle");

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
		BuildReferenceGraph buildReferenceGraph;
		ReferenceGraphReport referenceGraphReport;
		ReferenceGraphReport.OutputFormat outputFormat;

		ReferenceGraphReportTool.init();

		try {
			// Not obvious, but we must use GnuParser to support --long-option=value syntax.
			// Commons CLI 1.3 (as yet unreleased) is supposed to have a DefaultParser to
			// replace existing parser implementations.
			parser = new GnuParser();

			try {
				commandLine = parser.parse(ReferenceGraphReportTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				ReferenceGraphReportTool.help();
				System.exit(0);
			}

			args = commandLine.getArgs();

			if (args.length != 1) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
			}

			CliUtil.setupExecContext(commandLine, true);

			if (!commandLine.hasOption("output-format")) {
				outputFormat = ReferenceGraphReport.OutputFormat.TEXT;
			} else {
				try {
					outputFormat = ReferenceGraphReport.OutputFormat.valueOf(commandLine.getOptionValue("output-format"));
				} catch (IllegalArgumentException iae) {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE_OPTION), "output-format", ReferenceGraphReportTool.resourceBundle.getString(ReferenceGraphReportTool.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), CliUtil.getHelpCommandLineOption()));
				}
			}

			// TODO: avoid-redundancy not valid if not graph
			// only-multiple-versions not valid if not module-versions
			// most-recent-available-version-in-scm not valid if not module-version
			// reference-paths not valid if not module-version.

			buildReferenceGraph = new BuildReferenceGraph(null, CliUtil.getListModuleVersionRoot(commandLine));
			buildReferenceGraph.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));
			buildReferenceGraph.performJob();
			referenceGraphReport = new ReferenceGraphReport(buildReferenceGraph.getReferenceGraph(), outputFormat);
			referenceGraphReport.setOutputFilePath(Paths.get(args[0]));

			if (commandLine.hasOption("graph")) {
				ReferenceGraphReport.ReferenceGraphMode referenceGraphMode;

				if (commandLine.hasOption("avoid-redundancy")) {
					referenceGraphMode = ReferenceGraphReport.ReferenceGraphMode.TREE_NO_REDUNDANCY;
				} else {
					referenceGraphMode = ReferenceGraphReport.ReferenceGraphMode.FULL_TREE;
				}

				referenceGraphReport.includeReferenceGraph(referenceGraphMode);
			}

			if (commandLine.hasOption("module-versions")) {
				ReferenceGraphReport.ModuleFilter moduleFilter;

				if (commandLine.hasOption("only-multiple-versions")) {
					moduleFilter = ReferenceGraphReport.ModuleFilter.ONLY_MULTIPLE_VERSIONS;
				} else {
					moduleFilter = ReferenceGraphReport.ModuleFilter.ALL;
				}

				referenceGraphReport.includeModules(moduleFilter);
			}

			if (commandLine.hasOption("most-recent-static-version-in-scm")) {
				referenceGraphReport.includeMostRecentStaticVersionInScm();
			}

			if (commandLine.hasOption("referene-paths")) {
				referenceGraphReport.includeReferencePaths();
			}

			referenceGraphReport.performJob();
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
		if (!ReferenceGraphReportTool.indInit) {
			Option option;

			ReferenceGraphReportTool.options = new Options();

			option = new Option(null, null);
			option.setLongOpt("output-format");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("graph");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("avoid-redundancy");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("module-versions");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("only-multiple-versions");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("most-recent-static-version-in-scm");
			ReferenceGraphReportTool.options.addOption(option);

			option = new Option(null, null);
			option.setLongOpt("reference-paths");
			ReferenceGraphReportTool.options.addOption(option);

			CliUtil.addStandardOptions(ReferenceGraphReportTool.options);
			CliUtil.addRootModuleVersionOptions(ReferenceGraphReportTool.options);

			ReferenceGraphReportTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(ReferenceGraphReportTool.class.getResourceAsStream("ReferenceGraphReportToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
//report [--graph] [--avoid-redundancy] [--module-versions [--reference-graph-paths] [--most-recent-version] [--most-recent-available-version-scm]]


