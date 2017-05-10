/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.ModelVisitorJob;
import org.azyva.dragom.model.NodePath;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

/**
 * Generic tool wrapper for classes which implement {@link ModelVisitorJob}.
 *
 * <p>Many jobs which implement ModelVisitorJob, do not require complex invocation
 * arguments and can be invoked by this generic tool wrapper and avoid having to
 * introduce specific tool classes.
 * <p>
 * This tool first expects the following arguments:
 * <ul>
 * <li>Fully qualified name of the ModelVisitorJob implementation class
 * <li>Text resource for the help file
 * </ul>
 * These arguments are not validated as if they were specified by the user. They
 * are expected to be specified by a script that invokes the tool.
 * <p>
 * After these arguments, the regular user-level options and arguments are
 * expected.
 *
 * @author David Raymond
 */
public class GenericModelVisitorJobInvokerTool {
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
    String modelVisitorJobClass;
    String helpResource;
    DefaultParser defaultParser;
    CommandLine commandLine = null;
    Constructor<? extends ModelVisitorJob> constructor;
    ModelVisitorJob modelVisitorJob;
    int exitStatus;

    modelVisitorJobClass = args[0];
    helpResource = args[1];

    args = Arrays.copyOfRange(args, 2, args.length);

    GenericModelVisitorJobInvokerTool.init();

    modelVisitorJob = null;

    try {
      defaultParser = new DefaultParser();

      try {
        commandLine = defaultParser.parse(GenericModelVisitorJobInvokerTool.options, args);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
      }

      if (CliUtil.hasHelpOption(commandLine)) {
        GenericModelVisitorJobInvokerTool.help(helpResource);
      } else {
        List<NodePath> listNodePathBase;

        args = commandLine.getArgs();

        CliUtil.setupExecContext(commandLine, true);

        if (args.length != 0) {
          listNodePathBase = new ArrayList<>();

          for (String arg: args) {
            try {
              listNodePathBase.add(NodePath.parse(arg));
            } catch (java.text.ParseException pe) {
              throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
            }
          }
        } else {
          listNodePathBase = null;
        }

        try {
          constructor = Class.forName(modelVisitorJobClass).asSubclass(ModelVisitorJob.class).getConstructor(List.class);
          modelVisitorJob = constructor.newInstance(listNodePathBase);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
          throw new RuntimeException(e);
        }

        modelVisitorJob.performJob();
      }

      // Need to call before ExecContextHolder.endToolAndUnset.
      exitStatus = Util.getExitStatusAndShowReason();
    } catch (RuntimeExceptionUserError reue) {
      System.err.println(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_USER_ERROR_PREFIX) + reue.getMessage());
      exitStatus = 1;
    } catch (RuntimeException re) {
      re.printStackTrace();
      exitStatus = 1;
    } finally {
      ExecContextHolder.endToolAndUnset();
    }

    System.exit(exitStatus);
  }

  /**
   * Initializes the class.
   */
  private synchronized static void init() {
    if (!GenericModelVisitorJobInvokerTool.indInit) {
      Option option;
      GenericModelVisitorJobInvokerTool.options = new Options();

      CliUtil.initJavaUtilLogging();

      CliUtil.addStandardOptions(GenericModelVisitorJobInvokerTool.options);

      GenericModelVisitorJobInvokerTool.indInit = true;
    }
  }

  /**
   * Displays help information.
   *
   * @param resource Base name of the resource containing the help file.
   */
  private static void help(String resource) {
    try {
      IOUtils.copy(CliUtil.getLocalizedTextResourceReader(GenericModelVisitorJobInvokerTool.class, resource),  System.out);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
