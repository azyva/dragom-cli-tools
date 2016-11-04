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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.BuildReferenceGraph;
import org.azyva.dragom.job.SetupJenkinsJobs;
import org.azyva.dragom.reference.ReferenceGraph;
import org.azyva.dragom.util.RuntimeExceptionUserError;

/**
 * Tool for setting up jobs in Jenkins based on the {@link ModuleVersion's} in a
 * {@link ReferenceGraph}.
 * <p>
 * See the help information displayed by the SetupJenkinsJobsTool.help method.
 *
 * @author David Raymond
 */
public class SetupJenkinsJobsTool {
	/**
	 * ResourceBundle specific to this class.
	 */
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(SetupJenkinsJobsTool.class.getName() + "ResourceBundle");

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
		CommandLine commandLine = null;
		Path pathJobsCreatedFile;
		BuildReferenceGraph buildReferenceGraph;
		SetupJenkinsJobs setupJenkinsJobs;

		SetupJenkinsJobsTool.init();

		try {
			defaultParser = new DefaultParser();

			try {
				commandLine = defaultParser.parse(SetupJenkinsJobsTool.options, args);
			} catch (ParseException pe) {
				throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
			}

			if (CliUtil.hasHelpOption(commandLine)) {
				SetupJenkinsJobsTool.help();
			} else {
				args = commandLine.getArgs();

				if (args.length != 0) {
					throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
				}

				if (commandLine.hasOption("jobs-created-file")) {
					pathJobsCreatedFile = Paths.get(commandLine.getOptionValue("jobs-created-file"));
				} else {
					pathJobsCreatedFile = null;
				}

				CliUtil.setupExecContext(commandLine, true);

				buildReferenceGraph = new BuildReferenceGraph(null, CliUtil.getListModuleVersionRoot(commandLine));
				buildReferenceGraph.setReferencePathMatcher(CliUtil.getReferencePathMatcher(commandLine));
				buildReferenceGraph.performJob();

				setupJenkinsJobs = new SetupJenkinsJobs(buildReferenceGraph.getReferenceGraph());

				if (pathJobsCreatedFile != null) {
					setupJenkinsJobs.setPathJobsCreatedFile(pathJobsCreatedFile);
				}

				setupJenkinsJobs.performJob();
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
		if (!SetupJenkinsJobsTool.indInit) {
			Option option;

			SetupJenkinsJobsTool.options = new Options();

			option = new Option(null, null);
			option.setLongOpt("jobs-created-file");
			option.setArgs(1);
			SetupJenkinsJobsTool.options.addOption(option);

			CliUtil.addStandardOptions(SetupJenkinsJobsTool.options);
			CliUtil.addRootModuleVersionOptions(SetupJenkinsJobsTool.options);

			SetupJenkinsJobsTool.indInit = true;
		}
	}

	/**
	 * Displays help information.
	 */
	private static void help() {
		try {
			IOUtils.copy(CliUtil.getLocalizedResourceAsStream(SetupJenkinsJobsTool.class, "SetupJenkinsJobsToolHelp.txt"),  System.out);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
