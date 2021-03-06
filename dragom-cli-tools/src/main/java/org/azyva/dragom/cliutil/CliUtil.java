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

package org.azyva.dragom.cliutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.execcontext.ExecContext;
import org.azyva.dragom.execcontext.ExecContextFactory;
import org.azyva.dragom.execcontext.ToolLifeCycleExecContext;
import org.azyva.dragom.execcontext.WorkspaceExecContextFactory;
import org.azyva.dragom.execcontext.plugin.UserInteractionCallbackPlugin;
import org.azyva.dragom.execcontext.support.ExecContextFactoryHolder;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.job.RootManager;
import org.azyva.dragom.model.Model;
import org.azyva.dragom.model.ModuleVersion;
import org.azyva.dragom.reference.ReferencePathMatcher;
import org.azyva.dragom.reference.ReferencePathMatcherAnd;
import org.azyva.dragom.reference.ReferencePathMatcherByElement;
import org.azyva.dragom.reference.ReferencePathMatcherNot;
import org.azyva.dragom.reference.ReferencePathMatcherOr;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static utility methods.
 *
 * For now this class is a mixed bag of utility methods. With time and maturity
 * some groups of utility methods may be migrated to separate classes.
 *
 * @author David Raymond
 */
public final class CliUtil {
  /**
   * Logger for the class.
   */
  private static final Logger logger = LoggerFactory.getLogger(CliUtil.class);

  /**
   * System property that specifies if a user properties file is supported.
   */
  public static final String SYS_PROPERTY_IND_USER_PROPERTIES = "org.azyva.dragom.IndUserProperties";

  /**
   * System property that specifies the default user properties file. "~" in the
   * value of this property is replaced by the user home directory.
   */
  public static final String SYS_PROPERTY_DEFAULT_USER_PROPERTIES_FILE = "org.azyva.dragom.DefaultUserProperties";

  /**
   * System property that specifies the command line option used to specify the user
   * properties file.
   */
  public static final String SYS_PROPERTY_USER_PROPERTIES_FILE_COMMAND_LINE_OPTION = "org.azyva.dragom.UserPropertiesCommandLineOption";

  /**
   * Default command line option for specifying the user properties file.
   */
  public static final String DEFAULT_USER_PROPERTIES_COMMAND_LINE_OPTION = "user-properties";

  /**
   * System property that specifies if single tool properties are supported.
   */
  public static final String SYS_PROPERTY_IND_SINGLE_TOOL_PROPERTIES = "org.azyva.dragom.IndSingleToolProperties";

  /**
   * System property that specifies if a tool properties file is supported.
   */
  public static final String SYS_PROPERTY_IND_TOOL_PROPERTIES = "org.azyva.dragom.IndToolProperties";

  /**
   * System property that specifies the command line option used to specify the
   * workspace path.
   */
  public static final String SYS_PROPERTY_WORKSPACE_PATH_COMMAND_LINE_OPTION = "org.azyva.dragom.WorkspacePathCommandLineOption";

  /**
   * Default command line option for specifying the workspace path.
   */
  public static final String DEFAULT_WORKSPACE_PATH_COMMAND_LINE_OPTION = "workspace";

  /**
   * System property that specifies the command line option used to specify the tool
   * properties file.
   */
  public static final String SYS_PROPERTY_TOOL_PROPERTIES_FILE_COMMAND_LINE_OPTION = "org.azyva.dragom.ToolPropertiesCommandLineOption";

  /**
   * Default command line option for specifying the tool properties file.
   */
  public static final String DEFAULT_TOOL_PROPERTIES_COMMAND_LINE_OPTION = "tool-properties";

  /**
   * System property that specifies the command line option specifying whether
   * confirmation is required.
   */
  public static final String SYS_PROPERTY_NO_CONFIRM_COMMAND_LINE_OPTION = "org.azyva.dragom.NoConfirmCommandLineOption";

  /**
   * Default command line option for specifying whether confirmation is required.
   */
  public static final String DEFAULT_NO_CONFIRM_COMMAND_LINE_OPTION = "no-confirm";

  /**
   * System property that specifies the command line option specifying whether
   * confirmation is required for a particular context.
   */
  public static final String SYS_PROPERTY_NO_CONFIRM_CONTEXT_COMMAND_LINE_OPTION = "org.azyva.dragom.NoConfirmContextCommandLineOption";

  /**
   * Default command line option for specifying whether confirmation is required
   * for a particular context.
   */
  public static final String DEFAULT_NO_CONFIRM_CONTEXT_COMMAND_LINE_OPTION = "no-confirm-context";

  /**
   * System property that specifies the help command line option.
   */
  public static final String SYS_PROPERTY_HELP_COMMAND_LINE_OPTION = "org.azyva.dragom.HelpCommandLineOption";

  /**
   * Default help command line option.
   */
  public static final String DEFAULT_HELP_COMMAND_LINE_OPTION = "help";

  /**
   * System property that specifies the root {@link ModuleVersion} command line
   * option.
   */
  public static final String SYS_PROPERTY_ROOT_MODULE_VERSION_COMMAND_LINE_OPTION = "org.azyva.dragom.RootModuleVersionCommandLineOption";

  /**
   * Default root {@link ModuleVersion} command line option.
   */
  public static final String DEFAULT_ROOT_MODULE_VERSION_COMMAND_LINE_OPTION = "root-module-version";

  /**
   * System property that specifies the {@link ReferencePathMatcherByElement}
   * command line option.
   */
  public static final String SYS_PROPERTY_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION = "org.azyva.dragom.ReferencePathMatcherCommandLineOption";

  /**
   * Default {@link ReferencePathMatcherByElement} command line option.
   */
  public static final String DEFAULT_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION = "reference-path-matcher";

  /**
   * System property that specifies the exclude
   * {@link ReferencePathMatcherByElement} command line option.
   */
  public static final String SYS_PROPERTY_EXCLUDE_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION = "org.azyva.dragom.ExcludeReferencePathMatcherCommandLineOption";

  /**
   * Default exclude {@link ReferencePathMatcherByElement} command line option.
   */
  public static final String DEFAULT_EXCLUDE_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION = "exclude-reference-path-matcher";

  /**
   * System property defining the Java Util Logging configuration file to use for
   * initializing the Java Util Logging framework. See
   * {@link CliUtil#initJavaUtilLogging}.
   */
  public static final String SYS_PROPERTY_JAVA_UTIL_LOGGING_CONFIG_FILE = "org.azyva.dragom.JavaUtilLoggingConfigFile";

  /**
   * System property prefix for initialization properties.
   *
   * <p>System properties having this prefix are interpreted as initialization
   * properties.
   */
  private static final String SYS_PROPERTY_PREFIX_INIT_PROPERTY = "org.azyva.dragom.init-property.";

  /**
   * Context for {@link Util#handleDoYouWantToContinue} that represents using the
   * "**" ReferencePathMatcher because no --reference-path-matcher option was
   * specified on the command line.
   */
  private static final String DO_YOU_WANT_TO_CONTINUE_CONTEXT_NO_REFERENCE_PATH_MATCHER = "NO_REFERENCE_PATH_MATCHER";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_USER_ERROR_PREFIX = "USER_ERROR_PREFIX";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE = "ERROR_PARSING_COMMAND_LINE";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE_OPTION = "ERROR_PARSING_COMMAND_LINE_OPTION";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_INVALID_ARGUMENT_COUNT = "INVALID_ARGUMENT_COUNT";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_INVALID_COMMAND = "INVALID_COMMAND";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ROOT_MODULE_VERSION_NOT_ALLOWED_WHEN_SPECIFIED_WORKSPACE = "ROOT_MODULE_VERSION_NOT_ALLOWED_WHEN_SPECIFIED_WORKSPACE";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ROOT_MODULE_VERSION_REQUIRED_WHEN_NOT_SPECIFIED_WORKSPACE = "ROOT_MODULE_VERSION_REQUIRED_WHEN_NOT_SPECIFIED_WORKSPACE";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ROOT_MODULE_VERSION_REQUIRED_WORKSPACE = "ROOT_MODULE_VERSION_REQUIRED_WORKSPACE";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_NOT_SPECIFIED = "REFERENCE_PATH_MATCHER_NOT_SPECIFIED";

  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_ABORT_REFERENCE_PATH_MATCHER_NOT_SPECIFIED = "ABORT_REFERENCE_PATH_MATCHER_NOT_SPECIFIED";

  /**
   * ResourceBundle specific to this class.
   * <p>
   * Being a utility class, this ResourceBundle also contains global locale-specific
   * resources which can be used by other classes.
   */
  private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(CliUtil.class.getName() + "ResourceBundle");

  /**
   * Pattern to find property references.
   */
  private static Pattern patternPropertyReference = Pattern.compile("\\$\\{([^\\}]+)\\}");

  /**
   * @return User properties file command line option.
   */
  public static String getUserPropertiesFileCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_USER_PROPERTIES_FILE_COMMAND_LINE_OPTION, CliUtil.DEFAULT_USER_PROPERTIES_COMMAND_LINE_OPTION);
  }

  /**
   * @return Tool properties file command line option.
   */
  public static String getToolPropertiesFileCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_TOOL_PROPERTIES_FILE_COMMAND_LINE_OPTION, CliUtil.DEFAULT_TOOL_PROPERTIES_COMMAND_LINE_OPTION);
  }

  /**
   * @return Workspace path command line option.
   */
  public static String getWorkspacePathCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_WORKSPACE_PATH_COMMAND_LINE_OPTION, CliUtil.DEFAULT_WORKSPACE_PATH_COMMAND_LINE_OPTION);
  }

  /**
   * @return Command line option specifying whether confirmation is required.
   */
  public static String getNoConfirmCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_NO_CONFIRM_COMMAND_LINE_OPTION, CliUtil.DEFAULT_NO_CONFIRM_COMMAND_LINE_OPTION);
  }

  /**
   * @return Command line option specifying whether confirmation is required for a
   *   particular context.
   */
  public static String getNoConfirmContextCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_NO_CONFIRM_CONTEXT_COMMAND_LINE_OPTION, CliUtil.DEFAULT_NO_CONFIRM_CONTEXT_COMMAND_LINE_OPTION);
  }

  /**
   * @return Help command line option.
   */
  public static String getHelpCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_HELP_COMMAND_LINE_OPTION, CliUtil.DEFAULT_HELP_COMMAND_LINE_OPTION);
  }

  /**
   * @return Root {@link ModuleVersion} command line option.
   */
  public static String getRootModuleVersionCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_ROOT_MODULE_VERSION_COMMAND_LINE_OPTION, CliUtil.DEFAULT_ROOT_MODULE_VERSION_COMMAND_LINE_OPTION);
  }

  /**
   * @return ReferencePathMatcher command line option.
   */
  public static String getReferencePathMatcherCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION, CliUtil.DEFAULT_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION);
  }

  /**
   * @return Exclude ReferencePathMatcher command line option.
   */
  public static String getExcludeReferencePathMatcherCommandLineOption() {
    Util.applyDragomSystemProperties();
    return System.getProperty(CliUtil.SYS_PROPERTY_EXCLUDE_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION, CliUtil.DEFAULT_EXCLUDE_REFERENCE_PATH_MATCHER_COMMAND_LINE_OPTION);
  }

  /**
   * Utility method to add standard Option's.
   * <p>
   * The user properties and tools properties Option's are added depending on
   * whether user properties and tool properties are supported.
   * <p>
   * The following Option's are also added:
   * <ul>
   * <li>Workspace
   * <li>Specifying whether confirmation is required
   * <li>Specifying whether confirmation is required for a particular context
   * <li>Help
   * </ul>
   * Used by tools when initializing Options.
   *
   * @param options Options.
   */
  public static void addStandardOptions(Options options) {
    Option option;

    Util.applyDragomSystemProperties();

    if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_USER_PROPERTIES))) {
      option = new Option(null, null);
      option.setLongOpt(CliUtil.getUserPropertiesFileCommandLineOption());
      option.setArgs(1);
      options.addOption(option);
    }

    if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_SINGLE_TOOL_PROPERTIES))) {
      option = new Option("D", null);
      option.setValueSeparator('=');
      option.setArgs(2);
      options.addOption(option);
    }

    if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_TOOL_PROPERTIES))) {
      option = new Option(null, null);
      option.setLongOpt(CliUtil.getToolPropertiesFileCommandLineOption());
      option.setArgs(1);
      options.addOption(option);
    }

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getWorkspacePathCommandLineOption());
    option.setArgs(1);
    options.addOption(option);

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getNoConfirmCommandLineOption());
    options.addOption(option);

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getNoConfirmContextCommandLineOption());
    option.setArgs(1);
    options.addOption(option);

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getHelpCommandLineOption());
    options.addOption(option);
  }

  /**
   * Utility method to add Option's related to the root {@link ModuleVersion}.
   * <p>
   * The following Option's are added:
   * <ul>
   * <li>Root {@link ModuleVersion}
   * <li>{@link ReferencePathMatcherByElement}
   * <li>Exclude ReferencePathMatcherByElement
   * </ul>
   * Used by tools that use root {@link ModuleVersion}'s when initializing Options.
   *
   * @param options Options.
   */
  public static void addRootModuleVersionOptions(Options options) {
    Option option;

    Util.applyDragomSystemProperties();

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getRootModuleVersionCommandLineOption());
    option.setArgs(1);
    options.addOption(option);

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getReferencePathMatcherCommandLineOption());
    option.setArgs(1);
    options.addOption(option);

    option = new Option(null, null);
    option.setLongOpt(CliUtil.getExcludeReferencePathMatcherCommandLineOption());
    option.setArgs(1);
    options.addOption(option);
  }

  /**
   * Sets up an {@link ExecContext} assuming an {@link ExecContextFactory} that
   * supports the concept of workspace directory.
   * <p>
   * {@link ExecContextFactoryHolder} is used to get the ExecContextFactory so it
   * is not guaranteed that it will support the concept of workspace directory and
   * implement {@link WorkspaceExecContextFactory}. In such as case an exception is
   * raised.
   * <p>
   * The strategy for setting up the ExecContext supports a user service
   * implementation where a single JVM remains running in the background in the
   * context of a given user account (not system-wide) and can execute Dragom tools
   * for multiple different workspaces while avoiding tool startup overhead.
   * <a href="http://www.martiansoftware.com/nailgun/" target="_blank">Nailgun</a>
   * can be useful for that purpose.
   * <p>
   * A user service implementation is supported by differentiating between workspace
   * initialization Properties passed to {@link ExecContextFactory#getExecContext}
   * and tool initialization Properties passed to
   * {@link ToolLifeCycleExecContext#startTool}.
   * <p>
   * Workspace initialization Properties are constructed in the following way:
   * <ul>
   * <li>Dragom properties are merged into System properties using
   *     {@link Util#applyDragomSystemProperties}. System properties take precedence
   *     over Dragom properties;
   * <li>Initialize an empty Properties with system properties (System.getProperties)
   *     that are prefixed with "org.azyva.dragom.init-property" as defaults. This
   *     Properties when fully initialized will become the workspace initialization
   *     Properties;
   * <li>If the org.azyva.IndUserProperties system property is defined, load the
   *     Properties defined in the properties file specified by the
   *     user-properties command line option. If not defined, use the properties
   *     file specified by the org.azyva.DefaultUserProperties system property. If
   *     not defined or if the properties file does not exist, do not load the
   *     Properties;
   * <li>The workspace directory is added to the Properties created above.
   * </ul>
   * The name of the user-properties command line option can be overridden with the
   * org.azyva.dragom.UserPropertiesCommandLineOption system property.
   * <p>
   * Tool initialization Properties are constructed in the following way (if indSet):
   * <ul>
   * <li>Initialize an empty Properties. This Properties when fully initialized will
   *     become the tool initialization Properties;
   * <li>If the org.azyva.IndToolProperties system property is defined, load the
   *     Properties defined in the Properties file specified by the
   *     tool-properties command line option. If not defined or if the properties
   *     file does not exist, do not load the Properties.
   * </ul>
   * The name of the tool-properties command line option can be overridden with the
   * org.azyva.dragom.ToolPropertiesCommandLineOption system property.
   * <p>
   * It is possible that ExecContextFactory.getExecContext uses a cached ExecContext
   * corresponding to a workspace that has already been initialized previously. In
   * that case workspace initialization Properties will not be considered since
   * ExecContextFactory.getExecContext considers them only when a new ExecContext is
   * created. This is not expected to be a problem or source of confusion since this
   * can happen only if a user service implementation is actually used and in such a
   * case Dragom and system properties are expected to be considered only once when
   * initializing the user service and users are expected to understand that user
   * properties are considered only when initializing a new workspace.
   * <p>
   * If indSet, {@link ExecContextHolder#setAndStartTool} is called with the
   * ExecContext to make it easier for tools to prepare for execution. But it is
   * still the tool's responsibility to call
   * {@link ExecContextHolder#endToolAndUnset} before exiting. This is somewhat
   * asymmetric, but is sufficiently convenient to be warranted. The case where it
   * can be useful to set indSet to false is for subsequently calling
   * {@link ExecContextHolder#forceUnset}.
   * <p>
   * If indSet, the IND_NO_CONFIRM and {@code IND_NO_CONFIRM.<context>} runtime
   * properties are read from the CommandLine.
   *
   * @param commandLine CommandLine where to obtain the user and tool properties
   *   files as well as the workspace path.
   * @param indSet Indicates to set the ExecContext in ExecContextHolder.
   * @return ExecContext.
   */
  public static ExecContext setupExecContext(CommandLine commandLine, boolean indSet) {
    ExecContextFactory execContextFactory;
    WorkspaceExecContextFactory workspaceExecContextFactory;
    Properties propertiesInit;
    String workspaceDir;
    String stringPropertiesFile;
    ExecContext execContext;

    execContextFactory = ExecContextFactoryHolder.getExecContextFactory();

    if (!(execContextFactory instanceof WorkspaceExecContextFactory)) {
      throw new RuntimeException("The ExecContextFactory does not support the workspace directory concept.");
    }

    workspaceExecContextFactory = (WorkspaceExecContextFactory)execContextFactory;

    propertiesInit = Util.getPropertiesDefaultInit();

    Util.applyDragomSystemProperties();

    if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_USER_PROPERTIES))) {
      stringPropertiesFile = commandLine.getOptionValue(CliUtil.getUserPropertiesFileCommandLineOption());

      if (stringPropertiesFile == null) {
        stringPropertiesFile = System.getProperty(CliUtil.SYS_PROPERTY_DEFAULT_USER_PROPERTIES_FILE);
      }

      if (stringPropertiesFile != null) {
        propertiesInit = CliUtil.loadProperties(stringPropertiesFile, propertiesInit);
      }
    }

    // In general initialization properties defined as system properties with the
    // "org.azyva.dragom.init-property." prefix are expected to have been provided
    // as -D JVM arguments and as such are expected by the user to have precedence
    // over initialization properties provided in the user properties file loaded just
    // above. Initialization properties defined in the dragom.properties file
    // (see Util#applyDragomSystemProperties) will therefore also have precedence,
    // which is generally not desirable. That is why a separate dragom-init.properties
    // file (loaded with Util.getPropertiesDefaultInit above) is used for default
    // initialization properties.
    for (String initProperty: System.getProperties().stringPropertyNames()) {
      if (initProperty.startsWith(CliUtil.SYS_PROPERTY_PREFIX_INIT_PROPERTY)) {
        propertiesInit.setProperty(initProperty.substring(CliUtil.SYS_PROPERTY_PREFIX_INIT_PROPERTY.length()), System.getProperty(initProperty));
      }
    }

    workspaceDir = commandLine.getOptionValue(CliUtil.getWorkspacePathCommandLineOption());

    if (workspaceDir != null) {
      propertiesInit.setProperty(workspaceExecContextFactory.getWorkspaceDirInitProperty(), workspaceDir);
    }

    execContext = execContextFactory.getExecContext(propertiesInit);

    if (indSet) {
      Properties propertiesTool;

      propertiesTool = null;

      if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_TOOL_PROPERTIES))) {
        stringPropertiesFile = commandLine.getOptionValue(CliUtil.getToolPropertiesFileCommandLineOption());

        if (stringPropertiesFile != null) {
          propertiesTool = CliUtil.loadProperties(stringPropertiesFile, null);
        }
      }

      if (Util.isNotNullAndTrue(System.getProperty(CliUtil.SYS_PROPERTY_IND_SINGLE_TOOL_PROPERTIES))) {
        Properties propertiesToolSingle;

        propertiesToolSingle = commandLine.getOptionProperties("D");

        if (propertiesTool != null) {
          // Explicit single properties override similar properties defined in the tool
          // properties file.
          propertiesTool.putAll(propertiesToolSingle);
        } else {
          propertiesTool = propertiesToolSingle;
        }
      }

      if (propertiesTool == null) {
        propertiesTool = new Properties();
      }

      // The following properties can also be generically specified as single tool
      // properties. But they are supported as explicit command line arguments since
      // they are general and often used.
      if (commandLine.hasOption(CliUtil.getNoConfirmCommandLineOption())) {
        propertiesTool.setProperty(Util.RUNTIME_PROPERTY_IND_NO_CONFIRM, "true");
      } else {
        String[] tabNoConfirmContext;

        tabNoConfirmContext = commandLine.getOptionValues(CliUtil.getNoConfirmContextCommandLineOption());

        if (tabNoConfirmContext != null) {
          for (String context: tabNoConfirmContext) {
            propertiesTool.setProperty(Util.RUNTIME_PROPERTY_IND_NO_CONFIRM + '.' + context, "true");
          }
        }
      }

      ExecContextHolder.setAndStartTool(execContext, propertiesTool);
    }

    return execContext;
  }

  /**
   * Helper method that factors the code for loading a Properties file.
   * <p>
   * All occurrences of "~" in the path to the Properties files are replaced with
   * the value of the user.home system property.
   * <p>
   * If the properties file is not found, propertiesDefault is returned (may be
   * null).
   * <p>
   * If propertiesDefault is null, a new Properties is created without default
   * Properties.
   * <p>
   * If propertiesDefault is not null, a new Properties is created with these
   * default Properties.
   *
   * @param stringPropertiesFile Path to the Properties file in String form.
   * @param propertiesDefault Default Properties.
   * @return Properties. May be null.
   */
  private static Properties loadProperties(String stringPropertiesFile, Properties propertiesDefault) {
    Properties properties;

    properties = propertiesDefault;

    CliUtil.logger.debug("Loading properties from " + stringPropertiesFile);

    stringPropertiesFile = stringPropertiesFile.replace("~", Matcher.quoteReplacement(System.getProperty("user.home")));

    try (InputStream inputStreamProperties = new FileInputStream(stringPropertiesFile )) {
      if (propertiesDefault == null) {
        properties = new Properties();
      } else {
        properties = new Properties(propertiesDefault);
      }
      properties.load(inputStreamProperties);
    } catch (FileNotFoundException fnfe) {
      CliUtil.logger.debug("Properties file " + stringPropertiesFile + " not found.");
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

    return properties;
  }

  /**
   * Indicates if the help command line option is specified.
   *
   * @param commandLine CommandLine.
   * @return Indicates if the help command line option is specified.
   */
  public static boolean hasHelpOption(CommandLine commandLine) {
    return commandLine.hasOption(CliUtil.getHelpCommandLineOption());
  }

  /**
   * Helper method to return the List of root {@link ModuleVersion}'s used by many
   * tools.
   * <p>
   * If the command line specifies the --root-module-version option, no root
   * ModuleVersions's must be specified by RootManager, and the List of root
   * ModuleVerion's contains the ModuleVersion's specified by these options that
   * specify ModuleVersion literals.
   * <p>
   * Otherwise, RootManager must specify at least one root ModuleVersion and this
   * List of root ModuleVersion's specified by RootManager is returned.
   *
   * @param commandLine CommandLine. Can be null to indicate that root
   *   ModuleVersion's cannot be specified on the command line.
   * @return List of root ModuleVersion's.
   */
  public static List<ModuleVersion> getListModuleVersionRoot(CommandLine commandLine) {
    String[] arrayStringRootModuleVersion;
    List<ModuleVersion> listModuleVersionRoot;

    if (commandLine != null) {
      arrayStringRootModuleVersion = commandLine.getOptionValues(CliUtil.getRootModuleVersionCommandLineOption());
    } else {
      arrayStringRootModuleVersion = null;
    }

    if (arrayStringRootModuleVersion != null) {
      if (!RootManager.getListModuleVersion().isEmpty()) {
        throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.resourceBundle.getString(CliUtil.MSG_PATTERN_KEY_ROOT_MODULE_VERSION_NOT_ALLOWED_WHEN_SPECIFIED_WORKSPACE), CliUtil.getRootModuleVersionCommandLineOption(), CliUtil.getHelpCommandLineOption()));
      }

       listModuleVersionRoot = new ArrayList<ModuleVersion>();

      for (int i = 0; i < arrayStringRootModuleVersion.length; i++) {
        try {
          listModuleVersionRoot.add(ModuleVersion.parse(arrayStringRootModuleVersion[i]));
        } catch (ParseException pe) {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE_OPTION), CliUtil.getRootModuleVersionCommandLineOption(), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
        }
      }
    } else {
      if (RootManager.getListModuleVersion().isEmpty()) {
        if (commandLine == null) {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.resourceBundle.getString(CliUtil.MSG_PATTERN_KEY_ROOT_MODULE_VERSION_REQUIRED_WHEN_NOT_SPECIFIED_WORKSPACE), CliUtil.getHelpCommandLineOption()));
        } else {
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.resourceBundle.getString(CliUtil.MSG_PATTERN_KEY_ROOT_MODULE_VERSION_REQUIRED_WHEN_NOT_SPECIFIED_WORKSPACE), CliUtil.getRootModuleVersionCommandLineOption(), CliUtil.getHelpCommandLineOption()));
        }
      }

      listModuleVersionRoot = RootManager.getListModuleVersion();
    }

    return listModuleVersionRoot;
  }

  /**
   * Helper method to return a ReferencePathMatcherAnd that is built from the
   * ReferencePathMatcherOr specified by RootManager and a ReferencePathMatcherOr
   * built from the --reference-path-matcher options that specify
   * ReferencePathMatcherByElement literals.
   *
   * @param commandLine CommandLine. Can be null to indicate that
   *   ReferencePathMatcherByElement's cannot be specified on the command line. In
   *   this case only the ReferencePathMatcherOr specified by RootManager is
   *   returned, equivalent to as if the one specified on the command line was "**".
   * @return ReferencePathMatcher.
   */
  public static ReferencePathMatcher getReferencePathMatcher(CommandLine commandLine) {
    Model model;
    String[] arrayStringReferencePathMatcher;
    String[] arrayStringExcludeReferencePathMatcher;
    ReferencePathMatcherOr referencePathMatcherOrCommandLine;
    ReferencePathMatcherOr referencePathMatcherOrExcludeCommandLine;
    ReferencePathMatcherAnd referencePathMatcherAnd;

    model = ExecContextHolder.get().getModel();

    if (commandLine != null) {
      arrayStringReferencePathMatcher = commandLine.getOptionValues(CliUtil.getReferencePathMatcherCommandLineOption());
      arrayStringExcludeReferencePathMatcher = commandLine.getOptionValues(CliUtil.getExcludeReferencePathMatcherCommandLineOption());

      if ((arrayStringReferencePathMatcher == null) && (arrayStringExcludeReferencePathMatcher == null)) {
        UserInteractionCallbackPlugin userInteractionCallbackPlugin;

        userInteractionCallbackPlugin = ExecContextHolder.get().getExecContextPlugin(UserInteractionCallbackPlugin.class);

        userInteractionCallbackPlugin.provideInfo(MessageFormat.format(CliUtil.resourceBundle.getString(CliUtil.MSG_PATTERN_KEY_REFERENCE_PATH_MATCHER_NOT_SPECIFIED), CliUtil.getReferencePathMatcherCommandLineOption(), CliUtil.getExcludeReferencePathMatcherCommandLineOption()));

        if (!Util.handleDoYouWantToContinueSimple(CliUtil.DO_YOU_WANT_TO_CONTINUE_CONTEXT_NO_REFERENCE_PATH_MATCHER)) {
          // Generally when handling "do you want to continue", we require the caller to use
          // Util.isAbort to know if the user requested to abort. But it is more convenient
          // for callers of this method to rely on an exception being thrown.
          throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.resourceBundle.getString(CliUtil.MSG_PATTERN_KEY_ABORT_REFERENCE_PATH_MATCHER_NOT_SPECIFIED), CliUtil.getReferencePathMatcherCommandLineOption(), CliUtil.getExcludeReferencePathMatcherCommandLineOption()));
        }
      }

      if (arrayStringReferencePathMatcher != null) {
        referencePathMatcherOrCommandLine = new ReferencePathMatcherOr();

        for (int i = 0; i < arrayStringReferencePathMatcher.length; i++) {
          try {
            referencePathMatcherOrCommandLine.addReferencePathMatcher(ReferencePathMatcherByElement.parse(arrayStringReferencePathMatcher[i], model));
          } catch (ParseException pe) {
            throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE_OPTION), CliUtil.getReferencePathMatcherCommandLineOption(), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
          }
        }
      } else {
        referencePathMatcherOrCommandLine = null;
      }

      if (arrayStringExcludeReferencePathMatcher != null) {
        referencePathMatcherOrExcludeCommandLine = new ReferencePathMatcherOr();

        for (int i = 0; i < arrayStringExcludeReferencePathMatcher.length; i++) {
          try {
            referencePathMatcherOrExcludeCommandLine.addReferencePathMatcher(ReferencePathMatcherByElement.parse(arrayStringExcludeReferencePathMatcher[i], model));
          } catch (ParseException pe) {
            throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE_OPTION), CliUtil.getExcludeReferencePathMatcherCommandLineOption(), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
          }
        }
      } else {
        referencePathMatcherOrExcludeCommandLine = null;
      }

      if ((referencePathMatcherOrCommandLine == null) && (referencePathMatcherOrExcludeCommandLine == null)) {
        return RootManager.getReferencePathMatcherOr();
      } else {
        referencePathMatcherAnd = new ReferencePathMatcherAnd();

        referencePathMatcherAnd.addReferencePathMatcher(RootManager.getReferencePathMatcherOr());

        if (referencePathMatcherOrCommandLine != null) {
          referencePathMatcherAnd.addReferencePathMatcher(referencePathMatcherOrCommandLine);
        }

        if (referencePathMatcherOrExcludeCommandLine != null) {
          referencePathMatcherAnd.addReferencePathMatcher(new ReferencePathMatcherNot(referencePathMatcherOrExcludeCommandLine));
        }

        return referencePathMatcherAnd;
      }
    } else {
      return RootManager.getReferencePathMatcherOr();
    }
  }

  /**
   * Returns a message pattern corresponding to a key.
   *
   * @param msgPatternKey Message pattern key within the ResourceBundle.
   * @return Message pattern associated with the key.
   */
  public static String getLocalizedMsgPattern(String msgPatternKey) {
    return CliUtil.resourceBundle.getString(msgPatternKey);
  }

  /**
   * Returns the version of a text resource appropriate for the current default
   * Locale.
   *
   * <p>A Reader is returned so that character encoding is taken into consideration.
   * The text resource is assumed to be encoded with UTF-8, regardless of the
   * platform default encoding.
   *
   * <p>The algorithm used for selecting the appropriate resource is similar to the
   * one implemented by ResourceBundle.getBundle.

   * <p>The resource base name is split on the last ".", if any, and the candidate
   * variants are inserted before it.
   *
   * @param clazz Class to which the resource belongs.
   * @param resourceBaseName Base name of the resource.
   * @return Resource as an InputStream, just as Class.getResourceAsStream would
   *   return. null if no resource version exists.
   */
  public static Reader getLocalizedTextResourceReader(Class<?> clazz, String resourceBaseName) {
    int indexDot;
    String resourceBaseNamePrefix;
    String resourceBaseNameSuffix;
    Locale locale;
    String[] arrayCandidate;

    indexDot = resourceBaseName.lastIndexOf('.');

    if (indexDot != -1) {
      resourceBaseNamePrefix = resourceBaseName.substring(0, indexDot);
      resourceBaseNameSuffix = resourceBaseName.substring(indexDot);
    } else {
      resourceBaseNamePrefix = resourceBaseName;
      resourceBaseNameSuffix = "";
    }

    locale = Locale.getDefault();

    arrayCandidate = new String[7];

    arrayCandidate[0] = resourceBaseNamePrefix + "_" + locale.getLanguage() + "_" + locale.getScript() + "_" + locale.getCountry() + "_" + locale.getVariant();
    arrayCandidate[1] = resourceBaseNamePrefix + "_" + locale.getLanguage() + "_" + locale.getScript() + "_" + locale.getCountry();
    arrayCandidate[2] = resourceBaseNamePrefix + "_" + locale.getLanguage() + "_" + locale.getScript();
    arrayCandidate[3] = resourceBaseNamePrefix + "_" + locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant();
    arrayCandidate[4] = resourceBaseNamePrefix + "_" + locale.getLanguage() + "_" + locale.getCountry();
    arrayCandidate[5] = resourceBaseNamePrefix + "_" + locale.getLanguage();
    arrayCandidate[6] = resourceBaseNamePrefix;

    for (String candidate: arrayCandidate) {
      if (!candidate.endsWith("_")) {
        InputStream inputStreamResource;

        inputStreamResource = clazz.getResourceAsStream(candidate + resourceBaseNameSuffix);

        if (inputStreamResource != null) {
          try {
            return new InputStreamReader(inputStreamResource, "UTF-8");
          } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
          }
        }
      }
    }

    return null;
  }

  /**
   * Initializes the Java Util Logging framework by implementing replaceable
   * properties in the configuration file.
   *
   * <p>If the java.util.logging.config.file system property is defined, this method
   * does nothing, leaving the default initialization process be used.
   *
   * <p>If the java.util.logging.config.file system property is not defined and the
   * org.azyva.dragom.JavaUtilLoggingConfigFile system property is defined, this
   * method calls LogManager.readConfiguration with an InputStream which represents
   * the file but with property references replaced by the corresponding system
   * property.
   *
   * <p>If none of these two system properties are defined, this method does
   * nothing.
   */
  public static void initJavaUtilLogging() {
    String javaUtilLoggingConfigFile;
    String javaUtilLoggingConfig;
    Matcher matcher;
    StringBuffer stringBufferNewJavaUtilLoggingConfig;

    Util.applyDragomSystemProperties();

    if (   (System.getProperty("java.util.logging.config.file") == null)
        && ((javaUtilLoggingConfigFile = System.getProperty(CliUtil.SYS_PROPERTY_JAVA_UTIL_LOGGING_CONFIG_FILE)) != null)) {

      try {
        javaUtilLoggingConfig = new String(Files.readAllBytes(Paths.get(javaUtilLoggingConfigFile)));

        matcher = CliUtil.patternPropertyReference.matcher(javaUtilLoggingConfig);

        stringBufferNewJavaUtilLoggingConfig = new StringBuffer();

        while (matcher.find()) {
          String property;
          String value;

          property = matcher.group(1);
          value = System.getProperty(property);

          if (value == null) {
            throw new RuntimeException("System property " + property + " referenced in " + javaUtilLoggingConfigFile + " is not defined.");
          }

          // In a Properties file, \ must be escaped.
          value = value.replace("\\", "\\\\");

          matcher.appendReplacement(stringBufferNewJavaUtilLoggingConfig, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(stringBufferNewJavaUtilLoggingConfig);

        java.util.logging.LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(stringBufferNewJavaUtilLoggingConfig.toString().getBytes()));
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }
  }

  /**
   * Logs the Dragom logo.
   */
  public static void logDragomLogo() {
    ByteArrayOutputStream byteArrayOutputStream;

    byteArrayOutputStream = new ByteArrayOutputStream();

    try {
      IOUtils.copy(CliUtil.getLocalizedTextResourceReader(CliUtil.class, "DragomLogo.txt"), byteArrayOutputStream);
      byteArrayOutputStream.close();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

    try {
      CliUtil.logger.info(byteArrayOutputStream.toString("UTF-8"));
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException(uee);
    }
  }
}
