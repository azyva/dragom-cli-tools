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
import org.azyva.dragom.job.Checkout;
import org.azyva.dragom.job.ConfigHandleStaticVersion;
import org.azyva.dragom.job.ConfigReentryAvoider;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.job.RootModuleVersionJob;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

/**
 * Generic tool wrapper for classes which implement {@link RootModuleVersionJob}.
 * <p>
 * Many jobs, such as {@link Checkout}, which implement RootModuleVersionJob, do
 * not require complex invocation arguments and can be invoked by this generic
 * tool wrapper and avoid having to introduce specific tool classes.
 * <p>
 * This tool first expects the following arguments:
 * <ul>
 * <li>Fully qualified name of the RootModuleVersionJob implementation class
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
    String rootModuleVersionJobClass;
    String helpResource;
    DefaultParser defaultParser;
    CommandLine commandLine = null;
    Constructor<? extends RootModuleVersionJob> constructor;
    RootModuleVersionJob rootModuleVersionJob;
    int exitStatus;

    rootModuleVersionJobClass = args[0];
    helpResource = args[1];

    args = Arrays.copyOfRange(args, 2, args.length);

    GenericRootModuleVersionJobInvokerTool.init();

    rootModuleVersionJob = null;

    try {
      defaultParser = new DefaultParser();

      try {
        commandLine = defaultParser.parse(GenericRootModuleVersionJobInvokerTool.options, args);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
      }

      if (CliUtil.hasHelpOption(commandLine)) {
        GenericRootModuleVersionJobInvokerTool.help(helpResource);
      } else {
        args = commandLine.getArgs();

        if (args.length != 0) {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
        }

        CliUtil.setupExecContext(commandLine, true);

        try {
          constructor = Class.forName(rootModuleVersionJobClass).asSubclass(RootModuleVersionJob.class).getConstructor(List.class);
          rootModuleVersionJob = constructor.newInstance(CliUtil.getListModuleVersionRoot(commandLine));
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
          throw new RuntimeException(e);
        }

        rootModuleVersionJob.setReferencePathMatcherProvided(CliUtil.getReferencePathMatcher(commandLine));

        if (rootModuleVersionJob instanceof ConfigHandleStaticVersion) {
          if (commandLine.hasOption("no-handle-static-version")) {
            ((ConfigHandleStaticVersion)rootModuleVersionJob).setIndHandleStaticVersion(false);
          }
        }

        if (rootModuleVersionJob instanceof ConfigReentryAvoider) {
          if (commandLine.hasOption("no-avoid-reentry")) {
            ((ConfigReentryAvoider)rootModuleVersionJob).setIndAvoidReentry(false);
          }
        }

        rootModuleVersionJob.performJob();
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
      if ((rootModuleVersionJob != null) && rootModuleVersionJob.isListModuleVersionRootChanged()) {
        // It can be the case that RootManager does not specify any root ModuleVersion. In
        // that case calling RootManager.saveListModuleVersion simply saves an empty list,
        // even if the user has specified a root ModuleVersion on the command line.
        RootManager.saveListModuleVersion();
      }

      ExecContextHolder.endToolAndUnset();
    }

    System.exit(exitStatus);
  }

  /**
   * Initializes the class.
   */
  private synchronized static void init() {
    if (!GenericRootModuleVersionJobInvokerTool.indInit) {
      Option option;
      GenericRootModuleVersionJobInvokerTool.options = new Options();

      CliUtil.initJavaUtilLogging();

      // TODO: Should probably put these in some properties file (i18n).
      option = new Option(null, null);
      option.setLongOpt("no-avoid-reentry");
      GenericRootModuleVersionJobInvokerTool.options.addOption(option);

      option = new Option(null, null);
      option.setLongOpt("no-handle-static-version");
      GenericRootModuleVersionJobInvokerTool.options.addOption(option);

      CliUtil.addStandardOptions(GenericRootModuleVersionJobInvokerTool.options);
      CliUtil.addRootModuleVersionOptions(GenericRootModuleVersionJobInvokerTool.options);

      GenericRootModuleVersionJobInvokerTool.indInit = true;
    }
  }

  /**
   * Displays help information.
   *
   * @param resource Base name of the resource containing the help file.
   */
  private static void help(String resource) {
    try {
      IOUtils.copy(CliUtil.getLocalizedTextResourceReader(GenericRootModuleVersionJobInvokerTool.class, resource),  System.out);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}