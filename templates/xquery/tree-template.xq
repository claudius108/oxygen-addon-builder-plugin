declare variable $root-nodes := ${root-nodes};
declare variable $item-template := ${item-template};

declare function local:process-node($node) {
    let $treeitem := ${treeitem}
    let $children := $node/element()
    
    return
        string-join(
            (
                concat("{", "&quot;title&quot;: &quot;", $treeitem, "&quot;")
                ,
                concat("&quot;key&quot;: &quot;", $treeitem, "&quot;", if (empty($children)) then "}" else ())
                ,
                if (not(empty($children)))
                then
                    let $children :=
                        for $child in $children
                        return local:process-node($child)
                        
                    return concat("&quot;folder&quot;: true, ", "&quot;children&quot;: [", string-join($children, ", "), "]}")
                else ()
            )
            ,
            ", "
        )
};

let $datasource :=
    concat(
        "["
        ,
        string-join(
            for $root-node in $root-nodes
            return local:process-node($root-node)
            ,
            ","
        )
        ,
        "]"
    )

return
    <html>
        <head>
            <title>Tree template</title>
            <meta charset="UTF-8" />
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <link rel="stylesheet" type="text/css" href="file://${oxygenAddonBuilder.pluginInstallDir}/css/footer.css"/>
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/jquery/jquery-2.1.4.min.js">/**/</script>
            <link type="text/css" rel="stylesheet" href="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/jquery-ui/jquery-ui.css" />
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/jquery-ui/jquery-ui.min.js">/**/</script>
            <link href="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css" />
            <script src="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/fancytree/jquery.fancytree-all.min.js" type="text/javascript">/**/</script>
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/javascript/jquery.ui-contextmenu/jquery.ui-contextmenu.min.js">/**/</script>
            <style type="text/css">
              .ui-menu {{
                width: 100px;
                font-size: 63%;
                z-index: 3; /* over ext-wide titles */
              }}
            </style>
            <script type="text/javascript">
            datasource = {$datasource}
            </script>            
            <script type="text/javascript">
                <![CDATA[
                    function initialize() {
                        $(document).ready(function() {
                            var CLIPBOARD = null;
                            var frameworkPath = window.location.search.substring(15);
                                    
                            var findChildNodeByTitle = function(parentNode, childNodeTitle) {
                                var childNodes = parentNode.getChildren();
                                var result = null;
                                
                                if (childNodes != null) {
                                    for (var i = 0, il = childNodes.length; i < il; i++) {
                                        var childNode = childNodes[i];
                                        if (childNode.title == childNodeTitle) {
                                            result = childNode;
                                        } else {
                                            continue;
                                        }
                                    }               
                                }
                                
                                return result;
                            }
                
                            $("#tree").fancytree({
                                source: JSON.parse(datasource),
                                minExpandLevel: 2,                  
                                extensions: ["dnd", "edit"],
                                dnd: {
                                    autoExpandMS: 400,
                                    focusOnClick: true,
                                    preventVoidMoves: true,
                                    preventRecursiveMoves: true,
                                    dragStart: function(node, data) {
                                      return true;
                                    },
                                    dragEnter: function(node, data) {
                                       return true;
                                    },
                                    dragDrop: function(node, data) {
                                        var sourceNode = data.otherNode;
                                        var operation = OxygenAddonBuilder.move(sourceNode.getKeyPath(), node.getKeyPath());
                                        if (operation) {
                                            sourceNode.moveTo(node, data.hitMode);
                                        }                       
                                    }
                                },              
                                edit: {
                                    triggerStart: ["f2", "dblclick", "shift+click", "mac+enter"],
                                    beforeEdit: function(event, data) {
                                    },
                                    edit: function(event, data){
                                    },
                                    beforeClose: function(event, data) {
                                    },
                                    save: function(event, data){
                                        OxygenAddonBuilder.rename(data.node.getKeyPath(), data.input.val());
                                        return true;
                                    },
                                    close: function(event, data){
                                    }
                                }       
                            });
                            
                            var rootNode = $("#tree").fancytree("getRootNode").getFirstChild();
                            rootNode.key = frameworkPath.substring(1);
                            //node.sortChildren(null, true);
                            
                            
                            $("#tree").contextmenu({
                              delegate: "span.fancytree-title",
                              menu: [
                                {title: "Create dir(s)", cmd: "create-directory" },
                                {title: "Create file", cmd: "create-file" },
                                {title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil" },
                                {title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash" },
                                {title: "----"},
                                {title: "Cut", cmd: "cut", uiIcon: "ui-icon-scissors"},
                                {title: "Copy", cmd: "copy", uiIcon: "ui-icon-copy"},
                                {title: "Paste", cmd: "paste", uiIcon: "ui-icon-clipboard" }
                              ],
                              beforeOpen: function(event, ui) {
                                var node = $.ui.fancytree.getNode(ui.target);
                                if (node.isFolder()) {
                                    $("#tree").contextmenu("showEntry", "create-directory", true);
                                    $("#tree").contextmenu("showEntry", "create-file", true);               
                                    $("#tree").contextmenu("showEntry", "edit", false);
                                    $("#tree").contextmenu("showEntry", "paste", true);
                                } else {
                                    $("#tree").contextmenu("showEntry", "create-directory", false);
                                    $("#tree").contextmenu("showEntry", "create-file", false);
                                    $("#tree").contextmenu("showEntry", "edit", true);
                                    $("#tree").contextmenu("showEntry", "paste", false);
                                }
                                node.setActive();
                              },
                              select: function(event, ui) {
                                var node = $.ui.fancytree.getNode(ui.target);
                                var cmd = ui.cmd;
                                switch (cmd) {
                                    case "create-directory":
                                        var response = prompt("Enter the name of the directory");
                                        var dirNames = response.split('/');
                                        var dirPath = node.getKeyPath();
                                        var nodeTitle = node.title;
                                        var parentNode = node;
                                        var newNode = node;
                                        
                                        for (var i = 0, il = dirNames.length; i < il; i++) {
                                            var dirName = dirNames[i];
                                            dirPath += "/" + dirName;
                
                                            var duplicateNode = findChildNodeByTitle(parentNode, dirName);
                                            var newNodeExists = (duplicateNode != null) ? duplicateNode.isChildOf(parentNode) : false;
                                            
                                            if (!newNodeExists) {
                                                var operation = OxygenAddonBuilder.create(dirPath, true);
                                                if (operation) {
                                                    newNode = parentNode.addNode({"title": dirName, "folder":true, "key": dirName, "children":[], "data": {"path": dirPath}});
                                                    parentNode = newNode;
                                                }
                                            } else {
                                                parentNode = duplicateNode;
                                            }                           
                                        }
                                        newNode.setActive();
                                    break;
                                    case "create-file":
                                        var fileName = prompt("Enter the name of the file");
                                        var parentNode = node;
                                        
                                        var duplicateNode = findChildNodeByTitle(parentNode, fileName);
                                        var newNodeExists = (duplicateNode != null) ? duplicateNode.isChildOf(parentNode) : false;
                                        
                                        if (!newNodeExists) {
                                            var filePath = parentNode.getKeyPath() + "/" + fileName;
                                            var operation = OxygenAddonBuilder.create(filePath, false);
                                            if (operation) {
                                                var newNode = parentNode.addNode({"title": fileName, "key": fileName, "data": {"path": filePath}});
                                                newNode.setActive();
                                            }
                                        } else {
                                            alert('The file already exists!');
                                            return;
                                        }                       
                                    break;
                                    case "edit":
                                        OxygenAddonBuilder.edit(node.getKeyPath());
                                    break;              
                                    case "delete":
                                        var response = confirm("Do you really want to delete this item?");
                                        if (response) {
                                            OxygenAddonBuilder.delete(node.getKeyPath());
                                            node.remove();
                                            CLIPBOARD = {
                                              cmd: "",
                                              node: ""
                                            };                          
                                        }   
                                    break;
                                    case "cut":
                                      CLIPBOARD = {
                                        cmd: cmd,
                                        node: node
                                      };
                                    break;                  
                                    case "copy":
                                      CLIPBOARD = {
                                        cmd: cmd,
                                        node: node.toDict(function(n) {
                                          delete node.key;
                                        }),
                                        nodePath: node.getKeyPath()                     
                                      };
                                    break;
                                    case "paste":
                                      if (CLIPBOARD.cmd === "cut") {
                                        var sourceNode = CLIPBOARD.node;
                                        var operation = OxygenAddonBuilder.move(sourceNode.getKeyPath(), node.getKeyPath());
                                        if (operation) {
                                            sourceNode.moveTo(node, "child");
                                            sourceNode.setActive();
                                        }                       
                                      } else if (CLIPBOARD.cmd === "copy") {
                                        var sourceNode = CLIPBOARD.node;
                                        var operation = OxygenAddonBuilder.copy(CLIPBOARD.nodePath, node.getKeyPath());
                                        if (operation) {
                                            var targetNode = node.addChildren(sourceNode);
                                            targetNode.setActive();
                                        }           
                                      }
                                    break;                      
                                }
                                
                              }
                            }); 
                        });     
                    }
                ]]>
            </script>            
        </head>
        <body onload="window.setTimeout(initialize, 0);">
            <ol id="tree"></ol>
            <div id="footer">
                <button style="float: right;" onclick="OxygenAddonBuilder.closeDialogWindow();">Close</button>
            </div>      
        </body>
    </html>
