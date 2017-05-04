package org.azyva.dragom.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.azyva.dragom.cliutil.CliUtil;
import org.azyva.dragom.execcontext.support.ExecContextHolder;
import org.azyva.dragom.model.ClassificationNode;
import org.azyva.dragom.model.MutableClassificationNode;
import org.azyva.dragom.model.MutableModel;
import org.azyva.dragom.model.MutableNode;
import org.azyva.dragom.model.Node;
import org.azyva.dragom.model.NodePath;
import org.azyva.dragom.model.config.MutableConfig;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
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
   * MutableModel.
   */
  private MutableModel mutableModel;

  /**
   * Indicates the tool must quit.
   */
  private boolean indQuit;

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
      } catch (org.apache.commons.cli.ParseException pe) {
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

        modelConfigTool.mutableModel = (MutableModel)ExecContextHolder.get().getModel();
        modelConfigTool.mutableNodeCurrent = (MutableNode)modelConfigTool.mutableModel.getClassificationNodeRoot();

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

    do {
      String[] arrayCommand;
      String commandName;
      String methodName;
      Method method;

      if (this.mutableNodeCurrent == null) {
        System.out.println("No node currently has the focus.");
      } else {
        switch (this.mutableNodeCurrent.getNodeType()) {
        case CLASSIFICATION:
          System.out.println("Current focus ClassificationNode: " + this.mutableNodeCurrent);
          break;
        case MODULE:
          System.out.println("Current focus module: " + this.mutableNodeCurrent);
          break;
        }
      }

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
        method.invoke(this,  arrayCommand[1]);
      } catch (InvocationTargetException ite) {
        System.out.println("Exception thown during execution of command.");
        ite.getCause().printStackTrace();
        System.out.println("Attempting to recover.");
        continue;
      } catch (IllegalAccessException iae) {
        throw new RuntimeException(iae);
      }

      System.out.println("Command " + arrayCommand[0] + " completed successfully.");
    } while (!this.indQuit);

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

  /**
   * Executes the "flush" command.
   */
  private void execFlushCommand(String arguments) {
    if (!this.validateNoArgument(arguments)) {
      return;
    }

    this.mutableModel.flush();
  }

  /**
   * Executes the "create-root-classification-node" command.
   */
  private void execCreateRootClassificationNodeCommand(String arguments) {
    if (!this.validateNoArgument(arguments)) {
      return;
    }

    if (this.mutableNodeCurrent != null) {
      System.out.println("This command can only be submited when there is no focus node, which is generally when the model does not have a root ClassificationNode. Please delete the root ClassificationNode.");
      return;
    }

    if (this.mutableModel.getClassificationNodeRoot() != null) {
      System.out.println("This command can only be submited when there is no root ClassificationNode. Please explicitly delete the root ClassificationNode.");
      return;
    }

    this.mutableNodeCurrent = this.mutableModel.createMutableClassificationNodeRoot();
  }

  /**
   * Executes the "list-children" command.
   */
  private void execListChildrenCommand(String arguments) {
    ClassificationNode classificationNode;
    List<Node> listNode;

    if (!this.validateNoArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    if (!(this.mutableNodeCurrent instanceof ClassificationNode)) {
      System.out.println("Focus node not a ClassificaitonNode.");
      return;
    }

    classificationNode = (ClassificationNode)this.mutableNodeCurrent;

    listNode = classificationNode.getListChildNode();

    for (int i = 0; i < listNode.size(); i++) {
      Node node;

      node = listNode.get(i);

      System.out.println(String.valueOf(i) + " - " + node.getName());
    }
  }

  /**
   * Executes the "child" command.
   */
  private void execChildCommand(String arguments) {
    ClassificationNode classificationNode;
    String childName;
    int childIndex = -1;

    if (!this.validateArgument(arguments)) {
        return;
      }

    if (!this.validateCurrentNode()) {
      return;
    }

    if (!(this.mutableNodeCurrent instanceof ClassificationNode)) {
      System.out.println("Focus node not a ClassificaitonNode.");
      return;
    }

    classificationNode = (ClassificationNode)this.mutableNodeCurrent;

    // May be an index.
    childName = arguments;

    try {
      childIndex = Integer.parseInt(childName);

      // To distinguish the two cases: Index or name.
      childName = null;
    } catch (NumberFormatException nfe) {
    }

    if (childName == null) {
      List<Node> listNode;

      listNode = classificationNode.getListChildNode();

      if (childIndex < 0 || childIndex >= listNode.size()) {
        System.out.println("Child index must be between 0 and " + (listNode.size() - 1) + '.');
        return;
      }

      this.mutableNodeCurrent = (MutableNode)listNode.get(childIndex);
    } else {
      Node nodeChild;

      nodeChild = classificationNode.getNodeChild(childName);

      if (nodeChild == null) {
        System.out.println("Child " + childName + " does not exist.");
      } else {
        this.mutableNodeCurrent = (MutableNode)nodeChild;
      }
    }
  }

  /**
   * Executes the "parent" command.
   */
  private void execParentCommand(String arguments) {
    if (!this.validateNoArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    if (this.mutableNodeCurrent.getClassificationNodeParent() == null) {
      System.out.println("The current node is the root ClassificationNode and has no parent.");
    } else {
      this.mutableNodeCurrent = (MutableNode)this.mutableNodeCurrent.getClassificationNodeParent();
    }
  }

  /**
   * Executes the "node-path" command.
   */
  private void execNodePathCommand(String arguments) {
    ClassificationNode classificationNode;
    NodePath nodePath;
    Node node;

    if (!this.validateArgument(arguments)) {
      return;
    }

    try {
      nodePath = NodePath.parse(arguments);
    } catch (ParseException pe) {
      System.out.println("The NodePath " + arguments + " specified is not valid: " + pe.getMessage());
      return;
    }

    if (nodePath.isPartial()) {
      try {
        node = (MutableNode)this.mutableModel.getClassificationNode(nodePath);
      } catch (IllegalArgumentException iae) {
        System.out.println("NodePath " + nodePath + " is partial and must reference a ClassificationNode, but the node referenced is not. Remove the trailing '/' to make the NodePath complete.");
        return;
      }
    } else {
      try {
        node = (MutableNode)this.mutableModel.getModule(nodePath);
      } catch (IllegalArgumentException iae) {
        System.out.println("NodePath " + nodePath + " is complete and must reference a Module, but the node referenced is not. Add a trailing '/' to make the NodePath partial.");
        return;
      }
    }

    if (node == null) {
      System.out.println("No node corresponds to NodePath " + nodePath);
      return;
    }

    this.mutableNodeCurrent = (MutableNode)node;
  }

  /**
   * Executes the "next-sibbling" command.
   */
  private void execNextSibblingCommand(String arguments) {
    ClassificationNode classificationNodeParent;
    List<Node> listNode;
    int currentChildIndex;

    if (!this.validateNoArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    classificationNodeParent = this.mutableNodeCurrent.getClassificationNodeParent();
    listNode = classificationNodeParent.getListChildNode();

    currentChildIndex = listNode.indexOf(this.mutableNodeCurrent);

    if (currentChildIndex >= (listNode.size() - 1)) {
      System.out.println("Current node does not have any next sibbling.");
      return;
    }

    this.mutableNodeCurrent = (MutableNode)listNode.get(currentChildIndex + 1);
  }

  /**
   * Executes the "prev-sibbling" command.
   */
  private void execPrevSibblingCommand(String arguments) {
    ClassificationNode classificationNodeParent;
    List<Node> listNode;
    int currentChildIndex;

    if (!this.validateNoArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    classificationNodeParent = this.mutableNodeCurrent.getClassificationNodeParent();
    listNode = classificationNodeParent.getListChildNode();

    currentChildIndex = listNode.indexOf(this.mutableNodeCurrent);

    if (currentChildIndex == 0) {
      System.out.println("Current node does not have any previous sibbling.");
      return;
    }

    this.mutableNodeCurrent = (MutableNode)listNode.get(currentChildIndex - 1);
  }

  /**
   * Executes the "create-classification-node" command.
   */
  private void execCreateClassificationNodeCommand(String arguments) {
    MutableClassificationNode mutableClassificationNode;
    MutableNode mutableNode;
    NodeConfigTransferObject nodeConfigTransferObject;

    if (!this.validateArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    if (!(this.mutableNodeCurrent instanceof ClassificationNode)) {
      System.out.println("Focus node not a ClassificaitonNode.");
      return;
    }

    mutableClassificationNode = (MutableClassificationNode)this.mutableNodeCurrent;

    mutableNode = mutableClassificationNode.createChildMutableClassificationNode();

    nodeConfigTransferObject = mutableNode.getNodeConfigTransferObject(null);
    nodeConfigTransferObject.setName(arguments);
    mutableNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

    this.mutableNodeCurrent = mutableNode;
  }

  /**
   * Executes the "create-module" command.
   */
  private void execCreateModuleCommand(String arguments) {
    MutableClassificationNode mutableClassificationNode;
    MutableNode mutableNode;
    NodeConfigTransferObject nodeConfigTransferObject;

    if (!this.validateArgument(arguments)) {
      return;
    }

    if (!this.validateCurrentNode()) {
      return;
    }

    if (!(this.mutableNodeCurrent instanceof ClassificationNode)) {
      System.out.println("Focus node not a ClassificaitonNode.");
      return;
    }

    mutableClassificationNode = (MutableClassificationNode)this.mutableNodeCurrent;

    mutableNode = mutableClassificationNode.createChildMutableModule();

    nodeConfigTransferObject = mutableNode.getNodeConfigTransferObject(null);
    nodeConfigTransferObject.setName(arguments);
    mutableNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

    this.mutableNodeCurrent = mutableNode;
  }

  /**
   * Executes the "delete" command.
   */
  private void execDeleteCommand(String arguments) {
    System.out.println("Delete");

  }

  /**
   * Executes the "where-am-i" command.
   */
  private void execWhereAmICommand(String arguments) {
    System.out.println("WhereAmI");

  }

  /**
   * Executes the "display" command.
   */
  private void execDisplayCommand(String arguments) {
    System.out.println("Display");

  }

  /**
   * Executes the "display-parent-hierarchy" command.
   */
  private void execDisplayParentHierarchyCommand(String arguments) {
    System.out.println("DisplayParentHierarchy");

  }

  /**
   * Executes the "add-property" command.
   */
  private void execAddPropertyCommand(String arguments) {
    System.out.println("AddProperty");

  }

  /**
   * Executes the "update-property" command.
   */
  private void execUpdatePropertyCommand(String arguments) {
    System.out.println("UpdateProperty");

  }

  /**
   * Executes the "remove-property" command.
   */
  private void execRemovePropertyCommand(String arguments) {
    System.out.println("RemoveProperty");

  }

  /**
   * Executes the "add-plugin" command.
   */
  private void execAddPluginCommand(String arguments) {
    System.out.println("AddPlugin");

  }

  /**
   * Executes the "update-plugin" command.
   */
  private void execUpdatePluginCommand(String arguments) {
    System.out.println("UpdatePlugin");

  }

  /**
   * Executes the "remove-plugin" command.
   */
  private void execRemovePluginCommand(String arguments) {
    System.out.println("RemovePlugin");

  }

  /**
   * Executes the "list-config" command.
   */
  private void execListConfigCommand(String arguments) {
    System.out.println("ListConfig");

  }

  /**
   * Executes the "display-config" command.
   */
  private void execDisplayConfigCommand(String arguments) {
    System.out.println("DisplayConfig");

  }

  /**
   * Executes the "edit-config" command.
   */
  private void execEditConfigCommand(String arguments) {
    System.out.println("EditConfig");

  }

  /**
   * Executes the "find-property" command.
   */
  private void execFindPropertyCommand(String arguments) {
    System.out.println("FindProperty");

  }

  /**
   * Executes the "find-property-value" command.
   */
  private void execFindPropertyValueCommand(String arguments) {
    System.out.println("FindPropertyValue");

  }

  /**
   * Executes the "find-property-value-regex" command.
   */
  private void execFindPropertyValueRegexCommand(String arguments) {
    System.out.println("FindPropertyValueRegex");

  }

  /**
   * Executes the "find-plugin" command.
   */
  private void execFindPluginCommand(String arguments) {
    System.out.println("FindPlugin");

  }

  /**
   * Executes the "find-plugin-id" command.
   */
  private void execFindPluginIdCommand(String arguments) {
    System.out.println("FindPluginId");

  }

  /**
   * Executes the "find-plugin-class" command.
   */
  private void execFindPluginClassCommand(String arguments) {
    System.out.println("FindPluginClass");

  }

  /**
   * Executes the "find-config" command.
   */
  private void execFindConfigCommand(String arguments) {
    System.out.println("FindConfig");

  }

  /**
   * Executes the "search-result" command.
   */
  private void execSearchResultCommand(String arguments) {
    System.out.println("SearchResult");

  }

  /**
   * Executes the "next-result" command.
   */
  private void execNextResultCommand(String arguments) {
    System.out.println("NextResult");

  }

  /**
   * Executes the "prev-result" command.
   */
  private void execPrevResultCommand(String arguments) {
    System.out.println("PrevResult");

  }

  /**
   * Executes the "first-result" command.
   */
  private void execFirstResultCommand(String arguments) {
    System.out.println("FirstResult");

  }

  /**
   * Executes the "last-result" command.
   */
  private void execLastResultCommand(String arguments) {
    System.out.println("LastResult");

  }

  /**
   * Executes the "help" command.
   */
  private void execHelpCommand(String arguments) {
    System.out.println("Help");

  }

  /**
   * Executes the "quit" command.
   */
  private void execQuitCommand(String arguments) {
    System.out.println("Quit");
    this.indQuit = true;
  }

  private boolean validateCurrentNode() {
    if (this.mutableNodeCurrent == null) {
      System.out.println("No focus node.");
      return false;
    } else {
      return true;
    }
  }

  private boolean validateNoArgument(String arguments) {
    if (!arguments.isEmpty()) {
      System.out.println("This command does not accept any argument.");
      return false;
    }

    return true;
  }

  private boolean validateArgument(String arguments) {
    if (arguments.isEmpty()) {
      System.out.println("This command requires an argument.");
      return false;
    }

    return true;
  }
}

/*
- Only command (2017-05-03 ??? without focus): Create root ClassificationNode (change focus)
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