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
            <link rel="stylesheet" type="text/css" href="file://${oxygenAddonBuilder.pluginInstallDir}/resources/css/footer.css"/>
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/jquery/jquery-2.1.4.min.js">/**/</script>
            <link type="text/css" rel="stylesheet" href="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/jquery-ui/jquery-ui.css" />
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/jquery-ui/jquery-ui.min.js">/**/</script>
            <link href="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css" />
            <script src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/fancytree/jquery.fancytree-all.min.js" type="text/javascript">/**/</script>
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/jquery.ui-contextmenu/jquery.ui-contextmenu.min.js">/**/</script>
            <style type="text/css">
              .ui-menu {{
                width: 100px;
                font-size: 63%;
                z-index: 3; /* over ext-wide titles */
              }}
            </style>
            <script type="text/javascript">
                datasource = {$datasource};
            </script>            
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/tree-template/tree-template.js">/**/</script>            
        </head>
        <body onload="window.setTimeout(initialize, 0);">
            <ol id="tree"></ol>
            <div id="footer">
                <button style="float: right;" onclick="OxygenAddonBuilder.closeDialogWindow();">Close</button>
            </div>      
        </body>
    </html>
