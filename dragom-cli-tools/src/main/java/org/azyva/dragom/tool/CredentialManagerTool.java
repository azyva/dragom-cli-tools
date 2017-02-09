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
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.plugin.CredentialStorePlugin;
import org.azyva.dragom.execcontext.plugin.UserInteractionCallbackPlugin;
import org.azyva.dragom.execcontext.plugin.impl.DefaultCredentialStorePluginImpl;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.security.CredentialStore;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;

/**
 * Tool wrapper for {@link DefaultCredentialStorePluginImpl}.
 *
 * See the help information displayed by the {@link CredentialManagerTool#help}.
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
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_INTERACTIVE_MODE_REQUIRED = "INTERACTIVE_MODE_REQUIRED";

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
        } else if (command.equals("set-password")) {
          CredentialManagerTool.setPasswordCommand(commandLine);
        } else if (command.equals("remove-password")) {
          CredentialManagerTool.removePasswordCommand(commandLine);
        } else if (command.equals("enum-default-users")) {
          CredentialManagerTool.enumDefaultUsersCommand(commandLine);
        } else if (command.equals("get-default-user")) {
          CredentialManagerTool.getDefaultUserCommand(commandLine);
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

    System.exit(Util.getToolResult().getResultCode());
  }

  /**
   * Initializes the class.
   */
  private synchronized static void init() {
    if (!CredentialManagerTool.indInit) {
      CliUtil.initJavaUtilLogging();

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
    List<CredentialStore.ResourcePatternRealmUser> listResourcePatternRealmUser;
    StringBuilder stringBuilder;

    if (commandLine.getArgs().length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    listResourcePatternRealmUser = defaultCredentialStorePluginImpl.getCredentialStore().getListResourcePatternRealmUser();

    stringBuilder = new StringBuilder();

    for (CredentialStore.ResourcePatternRealmUser resourcePatternRealmUser: listResourcePatternRealmUser) {
      stringBuilder
          .append(resourcePatternRealmUser.patternResource.toString())
          .append(" -> ").append(resourcePatternRealmUser.realm);

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
    List<CredentialStore.RealmUser> listRealmUser;
    StringBuilder stringBuilder;

    if (commandLine.getArgs().length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    listRealmUser = defaultCredentialStorePluginImpl.getCredentialStore().getListRealmUser();

    stringBuilder = new StringBuilder();

    for (CredentialStore.RealmUser realmUser: listRealmUser) {
      stringBuilder
          .append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_REALM))
          .append(": ")
          .append(realmUser.realm)
          .append(' ')
          .append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_USER))
          .append(": ")
          .append(realmUser.user)
          .append('\n');
    }

    if (stringBuilder.length() != 0) {
      // Remove the useless trailing linefeed.
      stringBuilder.setLength(stringBuilder.length() - 1);

      userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
    }
  }

  /**
   * Implements the "get-password" command.
   *
   * @param commandLine CommandLine.
   */
  private static void getPasswordCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    String user;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;
    String password;

    args = commandLine.getArgs();

    if ((args.length < 2) || (args.length > 3)) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];

    if (args.length == 3) {
      user = args[2];
    } else {
      user = null;
    }

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    password = defaultCredentialStorePluginImpl.getCredentialStore().getPassword(resource, user);

    if (password != null) {
      System.out.print(password);
    } else {
      System.exit(1);
    }
  }

  /**
   * Implements the "set-password" command.
   *
   * @param commandLine CommandLine.
   */
  private static void setPasswordCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    String user;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;

    args = commandLine.getArgs();

    if ((args.length < 2) || (args.length > 3)) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];

    if (args.length == 3) {
      user = args[2];
    } else {
      user = null;
    }

    if (ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class).isBatchMode()) {
      throw new RuntimeExceptionUserError(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_INTERACTIVE_MODE_REQUIRED));
    }

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    defaultCredentialStorePluginImpl.resetCredentials(resource, user);

    // Getting the credentials after having reset them causes
    // DefaultCredentialStorePluginImpl to request them.
    if (defaultCredentialStorePluginImpl.getCredentials(resource, user, null) == null) {
      System.exit(1);
    }
  }

  /**
   * Implements the "remote-password" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removePasswordCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    String user;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;

    args = commandLine.getArgs();

    if ((args.length < 2) || (args.length > 3)) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];

    if (args.length == 3) {
      user = args[2];
    } else {
      user = null;
    }

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    defaultCredentialStorePluginImpl.resetCredentials(resource, user);
  }

  /**
   * Implements the "enum-defaults-users" command.
   *
   * @param commandLine CommandLine.
   */
  private static void enumDefaultUsersCommand(CommandLine commandLine) {
    UserInteractionCallbackPlugin userInteractionCallbackPlugin;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;
    List<CredentialStore.RealmUser> listRealmUser;
    StringBuilder stringBuilder;

    if (commandLine.getArgs().length != 1) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);
    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    listRealmUser = defaultCredentialStorePluginImpl.getCredentialStore().getListRealmUserDefault();

    stringBuilder = new StringBuilder();

    for (CredentialStore.RealmUser realmUser: listRealmUser) {
      stringBuilder
      .append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_REALM))
      .append(": ")
      .append(realmUser.realm)
      .append(' ')
      .append(CredentialManagerTool.resourceBundle.getString(CredentialManagerTool.MSG_PATTERN_KEY_USER))
      .append(": ")
      .append(realmUser.user)
      .append('\n');
    }

    if (stringBuilder.length() != 0) {
      // Remove the useless trailing linefeed.
      stringBuilder.setLength(stringBuilder.length() - 1);

      userInteractionCallbackPlugin.provideInfo(stringBuilder.toString());
    }
  }

  /**
   * Implements the "get-default-user" command.
   *
   * @param commandLine CommandLine.
   */
  private static void getDefaultUserCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    String user;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;

    args = commandLine.getArgs();

    if (args.length != 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    user = defaultCredentialStorePluginImpl.getCredentialStore().getDefaultUser(resource);

    if (user != null) {
      System.out.print(user);
    } else {
      System.exit(1);
    }
  }

  /**
   * Implements the "set-default-user" command.
   *
   * @param commandLine CommandLine.
   */
  private static void setDefaultUserCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    String user;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;

    args = commandLine.getArgs();

    if (args.length != 3) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];
    user = args[2];

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    if (!defaultCredentialStorePluginImpl.getCredentialStore().setDefaultUser(resource, user)) {
      System.exit(1);
    }
  }

  /**
   * Implements the "remove-default-user" command.
   *
   * @param commandLine CommandLine.
   */
  private static void removeDefaultUserCommand(CommandLine commandLine) {
    String[] args;
    String resource;
    DefaultCredentialStorePluginImpl defaultCredentialStorePluginImpl;

    args = commandLine.getArgs();

    if (args.length != 2) {
      throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT), CliUtil.getHelpCommandLineOption()));
    }

    resource = args[1];

    defaultCredentialStorePluginImpl = (DefaultCredentialStorePluginImpl)ExecContextHolder.get().getExecContextPlugin(CredentialStorePlugin.class);

    defaultCredentialStorePluginImpl.getCredentialStore().deleteDefaultUser(resource);
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
