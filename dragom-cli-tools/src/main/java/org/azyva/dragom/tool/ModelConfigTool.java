package org.azyva.dragom.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.model.ClassificationNode;
import org.azyva.dragom.model.MutableModel;
import org.azyva.dragom.model.MutableNode;
import org.azyva.dragom.model.NodePath;
import org.azyva.dragom.model.config.MutableConfig;
import org.azyva.dragom.util.RuntimeExceptionUserError;
import org.azyva.dragom.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interactive command-driven console tool allowing to manage the
 * {@link MutableModel} {@link MutableConfig}.
 *
 * @author David Raymond
 */
public class ModelConfigTool {
  /**
   * Logger for the class.
   */
  private static final Logger logger = LoggerFactory.getLogger(ModelConfigTool.class);

  /**
   * See description in ResourceBundle.
   */
  private static final String MSG_PATTERN_KEY_ = "";

  /**
   * ResourceBundle specific to this class.
   */
  private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ModelConfigTool.class.getName() + "ResourceBundle");

  /**
   * Indicates that the class has been initialized.
   */
  private static boolean indInit;

  /**
   * Options for parsing the command line.
   */
  private static Options options;

  /**
   * Map of short to long command names.
   */
  private static Map<String, String> mapShortToLongCommand;

  /**
   * BufferedReader for reading command lines.
   */
  private BufferedReader bufferedReaderCommandInput;

  /**
   * Current MutableNode.
   *
   * <p>Can be null if none (if {@link MutableModel} does not have a root
   * {@link ClassificationNode}).
   */
  private MutableNode mutableNodeCurrent;

  /**
   * List of NodePath's in the search results.
   *
   * <p>null if no active search results.
   *
   * <p>Empty List if active search results empty.
   */
  private List<NodePath> listNodePathSearchResult;

  /**
   * Zero-based index of current {@link NodePath} in search result.
   *
   * <p>-1 means before first search result.
   *
   * <p>Equal to number of NodePath's in search result means after last search
   * result.
   */
  private int indexCurrentNodePathSearchResult;

  /**
   * Method main.
   *
   * @param args Arguments.
   */
  public static void main(String[] args) {
    DefaultParser defaultParser;
    CommandLine commandLine = null;
    int exitStatus;

    ModelConfigTool.init();

    try {
      defaultParser = new DefaultParser();

      try {
        commandLine = defaultParser.parse(ModelConfigTool.options, args);
      } catch (ParseException pe) {
        throw new RuntimeExceptionUserError(MessageFormat.format(CliUtil.getLocalizedMsgPattern(CliUtil.MSG_PATTERN_KEY_ERROR_PARSING_COMMAND_LINE), pe.getMessage(), CliUtil.getHelpCommandLineOption()));
      }

      if (CliUtil.hasHelpOption(commandLine)) {
        ModelConfigTool.help();
        exitStatus = 0;
      } else {
        ModelConfigTool modelConfigTool;

        args = commandLine.getArgs();

        CliUtil.setupExecContext(commandLine, true);

        modelConfigTool = new ModelConfigTool();

        modelConfigTool.run();

        exitStatus = 0;
      }
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
    if (!ModelConfigTool.indInit) {
      Option option;
      ModelConfigTool.options = new Options();

      CliUtil.initJavaUtilLogging();

      CliUtil.addStandardOptions(ModelConfigTool.options);

      ModelConfigTool.mapShortToLongCommand = new HashMap<String, String>();

      ModelConfigTool.mapShortToLongCommand.put("short", "long");

      ModelConfigTool.mapShortToLongCommand.put("f", "flush");
      ModelConfigTool.mapShortToLongCommand.put("crcn", "create-root-classification-node");
      ModelConfigTool.mapShortToLongCommand.put("lc", "list-children");
      ModelConfigTool.mapShortToLongCommand.put("c", "child");
      ModelConfigTool.mapShortToLongCommand.put("p", "parent");
      ModelConfigTool.mapShortToLongCommand.put("np", "node-path");
      ModelConfigTool.mapShortToLongCommand.put("ns", "next-sibbling");
      ModelConfigTool.mapShortToLongCommand.put("ps", "prev-sibbling");
      ModelConfigTool.mapShortToLongCommand.put("ccn", "create-classification-node");
      ModelConfigTool.mapShortToLongCommand.put("cm", "create-module");
      ModelConfigTool.mapShortToLongCommand.put("del", "delete");
      ModelConfigTool.mapShortToLongCommand.put("wai", "where-am-i");
      ModelConfigTool.mapShortToLongCommand.put("d", "display");
      ModelConfigTool.mapShortToLongCommand.put("dph", "display-parent-hierarchy");
      ModelConfigTool.mapShortToLongCommand.put("ap", "add-property");
      ModelConfigTool.mapShortToLongCommand.put("up", "update-property");
      ModelConfigTool.mapShortToLongCommand.put("rp", "remove-property");
      ModelConfigTool.mapShortToLongCommand.put("apl", "add-plugin");
      ModelConfigTool.mapShortToLongCommand.put("upl", "update-plugin");
      ModelConfigTool.mapShortToLongCommand.put("rpl", "remove-plugin");
      ModelConfigTool.mapShortToLongCommand.put("lcfg", "list-config");
      ModelConfigTool.mapShortToLongCommand.put("dcfg", "display-config");
      ModelConfigTool.mapShortToLongCommand.put("ecfg", "edit-config");
      ModelConfigTool.mapShortToLongCommand.put("fp", "find-property");
      ModelConfigTool.mapShortToLongCommand.put("fpv", "find-property-value");
      ModelConfigTool.mapShortToLongCommand.put("fpvr", "find-property-value-regex");
      ModelConfigTool.mapShortToLongCommand.put("fpl", "find-plugin");
      ModelConfigTool.mapShortToLongCommand.put("fplid", "find-plugin-id");
      ModelConfigTool.mapShortToLongCommand.put("fplcls", "find-plugin-class");
      ModelConfigTool.mapShortToLongCommand.put("fcfg", "find-config");
      ModelConfigTool.mapShortToLongCommand.put("sr", "search-result");
      ModelConfigTool.mapShortToLongCommand.put("nr", "next-result");
      ModelConfigTool.mapShortToLongCommand.put("pr", "prev-result");
      ModelConfigTool.mapShortToLongCommand.put("fr", "first-result");
      ModelConfigTool.mapShortToLongCommand.put("lr", "last-result");
      ModelConfigTool.mapShortToLongCommand.put("q", "quit");

      ModelConfigTool.indInit = true;
    }
  }

  /**
   * Displays help information.
   */
  private static void help() {
    try {
      IOUtils.copy(CliUtil.getLocalizedResourceAsStream(ModelConfigTool.class, "ModelConfigToolHelp.txt"), System.out);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Main method of the tool.
   *
   * <p>This method prompts for a command, executes it and loops, until the user
   * submits a command which causes the tool to stop.
   */
  private void run() {
    boolean indQuit;

    this.bufferedReaderCommandInput = new BufferedReader(new InputStreamReader(System.in));

    indQuit = false;

    do {
      String[] arrayCommand;
      String commandName;
      String methodName;
      Method method;

      arrayCommand = this.getCommand();
      commandName = arrayCommand[0];

      methodName = "exec" + Util.convertLowercaseWithDashesToPascalCase(commandName) + "Command";
      method = null;

      try {
        method = this.getClass().getDeclaredMethod(methodName, String.class);
      } catch (NoSuchMethodException nsme) {
      }

      if (method == null) {
        commandName = ModelConfigTool.mapShortToLongCommand.get(commandName);

        if (commandName != null) {
          try {
            method = this.getClass().getDeclaredMethod("exec" + Util.convertLowercaseWithDashesToPascalCase(commandName) + "Command", String.class);
          } catch (NoSuchMethodException nsme) {
          }
        }
      }

      if (method == null) {
        System.out.println("Command " + arrayCommand[0] + " not recognized.");
        continue;
      }

      try {
        indQuit = (Boolean)method.invoke(this,  arrayCommand[1]);
      } catch (InvocationTargetException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } while (!indQuit);

    return;
  }

  /**
   * Reads a command line from stdin and returns the command and its arguments.
   *
   * @return A String[2] where the first element is the command and the second
   *   element is the arguments.
   */
  private String[] getCommand() {
    String commandLine;
    int indexFirstBlank;

    System.out.print("> ");

    try {
      commandLine = this.bufferedReaderCommandInput.readLine();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

    indexFirstBlank = commandLine.indexOf(' ');

    if (indexFirstBlank == -1) {
      return new String[] {commandLine, ""};
    } else {
      return new String[] {commandLine.substring(0, indexFirstBlank), commandLine.substring(indexFirstBlank + 1)};
    }
  }

  private boolean execFlushCommand(String arguments) {
    System.out.println("Flush");
    return false;
  }

  private boolean execCreateRootClassificationNodeCommand(String arguments) {
    System.out.println("CreateRootClassificationNode");
    return false;
  }

  private boolean execListChildrenCommand(String arguments) {
    System.out.println("ListChildren");
    return false;
  }

  private boolean execChildCommand(String arguments) {
    System.out.println("Child");
    return false;
  }

  private boolean execParentCommand(String arguments) {
    System.out.println("Parent");
    return false;
  }

  private boolean execNodePathCommand(String arguments) {
    System.out.println("NodePath");
    return false;
  }

  private boolean execNextSibblingCommand(String arguments) {
    System.out.println("NextSibbling");
    return false;
  }

  private boolean execPrevSibblingCommand(String arguments) {
    System.out.println("PrevSibbling");
    return false;
  }

  private boolean execCreateClassificationNodeCommand(String arguments) {
   System.out.println("CreateClassificationNode");
   return false;
  }

  private boolean execCreateModuleCommand(String arguments) {
    System.out.println("CreateModule");
    return false;
  }

  private boolean execDeleteCommand(String arguments) {
    System.out.println("Delete");
    return false;
  }

  private boolean execWhereAmICommand(String arguments) {
    System.out.println("WhereAmI");
    return false;
  }

  private boolean execDisplayCommand(String arguments) {
    System.out.println("Display");
    return false;
  }

  private boolean execDisplayParentHierarchyCommand(String arguments) {
    System.out.println("DisplayParentHierarchy");
    return false;
  }

  private boolean execAddPropertyCommand(String arguments) {
    System.out.println("AddProperty");
    return false;
  }

  private boolean execUpdatePropertyCommand(String arguments) {
    System.out.println("UpdateProperty");
    return false;
  }

  private boolean execRemovePropertyCommand(String arguments) {
    System.out.println("RemoveProperty");
    return false;
  }

  private boolean execAddPluginCommand(String arguments) {
    System.out.println("AddPlugin");
    return false;
  }

  private boolean execUpdatePluginCommand(String arguments) {
    System.out.println("UpdatePlugin");
    return false;
  }

  private boolean execRemovePluginCommand(String arguments) {
    System.out.println("RemovePlugin");
    return false;
  }

  private boolean execListConfigCommand(String arguments) {
    System.out.println("ListConfig");
    return false;
  }

  private boolean execDisplayConfigCommand(String arguments) {
    System.out.println("DisplayConfig");
    return false;
  }

  private boolean execEditConfigCommand(String arguments) {
    System.out.println("EditConfig");
    return false;
  }

  private boolean execFindPropertyCommand(String arguments) {
    System.out.println("FindProperty");
    return false;
  }

  private boolean execFindPropertyValueCommand(String arguments) {
    System.out.println("FindPropertyValue");
    return false;
  }

  private boolean execFindPropertyValueRegexCommand(String arguments) {
    System.out.println("FindPropertyValueRegex");
    return false;
  }

  private boolean execFindPluginCommand(String arguments) {
    System.out.println("FindPlugin");
    return false;
  }

  private boolean execFindPluginIdCommand(String arguments) {
    System.out.println("FindPluginId");
    return false;
  }

  private boolean execFindPluginClassCommand(String arguments) {
    System.out.println("FindPluginClass");
    return false;
  }

  private boolean execFindConfigCommand(String arguments) {
    System.out.println("FindConfig");
    return false;
  }

  private boolean execSearchResultCommand(String arguments) {
    System.out.println("SearchResult");
    return false;
  }

  private boolean execNextResultCommand(String arguments) {
    System.out.println("NextResult");
    return false;
  }

  private boolean execPrevResultCommand(String arguments) {
    System.out.println("PrevResult");
    return false;
  }

  private boolean execFirstResultCommand(String arguments) {
    System.out.println("FirstResult");
    return false;
  }

  private boolean execLastResultCommand(String arguments) {
    System.out.println("LastResult");
    return false;
  }

  private boolean execHelpCommand(String arguments) {
    System.out.println("Help");
    return false;
  }

  private boolean execQuitCommand(String arguments) {
    System.out.println("Quit");
    return true;
  }
}

/*
- Only command: Create root ClassificationNode (change focus)
Various commands related to the focus Node:
- Save (flush)
- List children
- Go to child
- Go to parent
- Go to specific Node (NodePath)
- Add child (change focus)
- Delete focus Node (change to parent focus Node)
  - Can delete root, loose focus
- Display focus Node data (properties, plugins)
- Display focus Node data with parent hierarchy (where am I)
- Add/modify/delete property
- Add/modify/delete plugin
- List data plugins
- Display data plugin (report of all pertinent data decided by the data plugin)
- Invoke data plugin editor
- From focus Node, search (recurse or one level) for
  - Node defining property (name or name with value or name with value regex)
  - Node defining plugin (plugin interface, plugin interface with ID, impl)
  - Node having specific data plugin with data
  - Default to search result before start
- Display search result (list with last selection highlighted, dim if not current focus)
- Move to search result before start
- Move to search result after end
- Next search result (change focus)
- Previous search result (change focus)

*/