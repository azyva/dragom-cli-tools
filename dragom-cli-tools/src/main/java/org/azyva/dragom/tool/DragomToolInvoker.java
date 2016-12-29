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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.util.Util;


/**
 * Configurable single entry point for multiple (potentially all) Dragom-based
 * tools.
 * <p>
 * This class simplifies the development of shell scripts for invoking the various
 * tools.
 * <p>
 * Each tool becomes a command passed as the first argument to this meta-tool.
 * This command is used to lookup the details for invoking the corresponding tool
 * from system properties after calling {@link Util#applyDragomSystemProperties}.
 * <p>
 * This meta-tool also takes care of displaying help information about a tool
 * using the special "help" command, as well as general help information when no
 * argument is provided, including the list of available tools.
 *
 * @author David Raymond
 */
public class DragomToolInvoker {
  /**
   * See description in ResourceBundle.
   */
  public static final String MSG_PATTERN_KEY_UNKNOWN_TOOL = "UNKNOWN_TOOL";

  /**
   * System property specifying the list of supported tools.
   */
  private static final String SYS_PROPERTY_TOOLS = "org.azyva.dragom.Tools";

  /**
   * Prefix for a system property specifying some tool invocation information.
   */
  private static final String SYS_PROPERTY_PREFIX_TOOL = "org.azyva.dragom.Tool.";

  /**
   * Suffix for the system property specifying the class of the tool.
   */
  private static final String SYS_PROPERTY_SUFFIX_TOOL_CLASS = ".ToolClass";

  /**
   * Suffix for the system property specifying the fixed arguments to pass to "main"
   * before any other arguments.
   */
  private static final String SYS_PROPERTY_SUFFIX_FIXED_ARGS = ".FixedArgs";

  /**
   * ResourceBundle specific to this class.
   */
  private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(DragomToolInvoker.class.getName() + "ResourceBundle");

  /**
   * Indicates that the class has been initialized.
   */
  private static boolean indInit;

  /**
   * Specifies information useful for invoking a tool.
   */
  private static class ToolInvocationInfo {
    /**
     * Class of the tool.
     */
    Class<?> classTool;

    /**
     * Fixed arguments to pass to "main" before any other arguments.
     * <p>
     * Useful for generic tool classes such as {@link GenericRootModuleVersionJobInvokerTool} which can
     * implement multiple different tools from the user's point of view.
     */
    String[] arrayFixedArgs;
  }

  private static Map<String, ToolInvocationInfo> mapToolInvocationInfo;

  /**
   * Method main.
   *
   * @param args Arguments.
   */
  public static void main(String[] args) {
    String firstArg;
    String tool;
    ToolInvocationInfo toolInvocationInfo;


    DragomToolInvoker.init();

    if ((args.length == 0) || (args[0].equals("--" + CliUtil.getHelpCommandLineOption()))) {
      // We conveniently display help information if no argument is provided or if the
      // user specifies the "--help" option. But no argument being provided, or "--help"
      // being specified but with additional arguments is still considered a user error
      // and 1 is returned in these cases.

      DragomToolInvoker.help();
      System.exit(((args.length == 0) || (args.length > 1)) ? 1 : 0);
    }

    firstArg = args[0];

    if (firstArg.equals(CliUtil.getHelpCommandLineOption())) {
      if (args.length > 2) {
        DragomToolInvoker.help();
        System.exit(1);
      }

      tool = args[1];

      toolInvocationInfo = DragomToolInvoker.getToolInvocationInfo(tool);

      DragomToolInvoker.invokeTool(toolInvocationInfo, new String[] {"--" + CliUtil.getHelpCommandLineOption()});
    } else {
      tool = args[0];

      toolInvocationInfo = DragomToolInvoker.getToolInvocationInfo(tool);

      DragomToolInvoker.invokeTool(toolInvocationInfo, Arrays.copyOfRange(args, 1, args.length));
    }
  }

  /**
   * Initializes the class.
   */
  private synchronized static void init() {
    if (!DragomToolInvoker.indInit) {
      String[] arrayTool;
      ToolInvocationInfo toolInvocationInfo;

      CliUtil.initJavaUtilLogging();

      Util.applyDragomSystemProperties();

      // A LinkedHashMap is used to preserve order for the list of tools shown in the
      // help information.
      DragomToolInvoker.mapToolInvocationInfo = new LinkedHashMap<String, ToolInvocationInfo>();

      arrayTool = System.getProperty(DragomToolInvoker.SYS_PROPERTY_TOOLS).split(",");

      for (String tool: arrayTool) {
        String fixedArgs;

        toolInvocationInfo = new ToolInvocationInfo();

        try {
          toolInvocationInfo.classTool = Class.forName(System.getProperty(DragomToolInvoker.SYS_PROPERTY_PREFIX_TOOL + tool + DragomToolInvoker.SYS_PROPERTY_SUFFIX_TOOL_CLASS));
        } catch (ClassNotFoundException cnfe) {
          throw new RuntimeException(cnfe);
        }

        fixedArgs = System.getProperty(DragomToolInvoker.SYS_PROPERTY_PREFIX_TOOL + tool + DragomToolInvoker.SYS_PROPERTY_SUFFIX_FIXED_ARGS);

        if (fixedArgs != null) {
          toolInvocationInfo.arrayFixedArgs = fixedArgs.split(",");
        }

        DragomToolInvoker.mapToolInvocationInfo.put(tool, toolInvocationInfo);
      }

      DragomToolInvoker.indInit = true;
    }
  }

  /**
   * Invokes a tool.
   *
   * @param toolInvocationInfo ToolInvocationInfo.
   * @param arrayArgs Array of arguments.
   */
  private static void invokeTool(ToolInvocationInfo toolInvocationInfo, String[] arrayArgs) {
    Method method;
    String[] arrayRealArgs;

    if (toolInvocationInfo.arrayFixedArgs != null) {
      arrayRealArgs = new String[toolInvocationInfo.arrayFixedArgs.length + arrayArgs.length];
      System.arraycopy(toolInvocationInfo.arrayFixedArgs, 0,  arrayRealArgs, 0, toolInvocationInfo.arrayFixedArgs.length);
      System.arraycopy(arrayArgs, 0, arrayRealArgs, toolInvocationInfo.arrayFixedArgs.length, arrayArgs.length);
    } else {
      arrayRealArgs = arrayArgs;
    }

    try {
      method = toolInvocationInfo.classTool.getMethod("main", String[].class);
      method.invoke(null, new Object[] {arrayRealArgs});
    } catch (InvocationTargetException ite) {
      // This is to support integration tests which prevent System.exit() from
      // terminating the JVM by causing it to throw an ExitException instead.
      if (ite.getCause().getClass().getName().contains("ExitException")) {
        throw (RuntimeException)ite.getCause();
      }

      throw new RuntimeException(ite);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Returns invocation information about a tool.
   * <p>
   * If the tool is unknown an appropriate message is shown to the user and
   * System.exit(1) is called.
   *
   * @param tool Tool.
   * @return ToolInvocationInfo.
   */
  private static ToolInvocationInfo getToolInvocationInfo(String tool) {
    ToolInvocationInfo toolInvocationInfo;

    toolInvocationInfo = DragomToolInvoker.mapToolInvocationInfo.get(tool);

    if (toolInvocationInfo == null) {
      System.err.println(MessageFormat.format(DragomToolInvoker.resourceBundle.getString(DragomToolInvoker.MSG_PATTERN_KEY_UNKNOWN_TOOL), tool));
      System.exit(1);
    }

    return toolInvocationInfo;
  }

  /**
   * Displays help information.
   */
  private static void help() {
    try {
      IOUtils.copy(CliUtil.getLocalizedResourceAsStream(DragomToolInvoker.class, "DragomToolInvokerHelp.txt"), System.out);

      for (String tool: DragomToolInvoker.mapToolInvocationInfo.keySet()) {
        System.out.println(tool);
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
