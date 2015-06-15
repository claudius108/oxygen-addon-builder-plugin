declare variable $root-nodes := ${root-nodes};
declare variable $root-nodes-path := ${root-nodes-path};
declare variable $item-template := ${item-template};
declare variable $tree-height := "${tree-height}";

declare function local:process-node($node as node()?, $node-path as xs:string?) {
    let $treeitem := substring(translate(normalize-space(string-join(($node/@level, if ($node/tmx:form) then concat($node/tmx:form, ' =') else (), $node/tmx:def), ' ')), "&quot;", "\&quot;"), 0, 100)
    let $node-name := $node/name()
    let $children := $node/element()[name() = $node-name]
    
    return
        string-join(
            (
                concat("{", "&quot;title&quot;: &quot;", $treeitem, "&quot;")
                ,
                concat("&quot;key&quot;: &quot;", $node-path, "&quot;", if (empty($children)) then "}" else ())
                ,
                if (not(empty($children)))
                then
                    let $children :=
                        for $child at $pos in $children
                        return local:process-node($child, concat($node-path, "/", $child/name(), "[position() = ", $pos , " and namespace-uri() = '", namespace-uri($child),"']"))
                        
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
            for $root-node at $pos in $root-nodes
            return local:process-node($root-node, concat($root-nodes-path, "[", $pos , "]"))
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
                z-index: 3;
              }}
              #tree .fancytree-container {{
                height: {$tree-height};
              }}
            </style>
            <script type="text/javascript">
                datasource = {$datasource};
            </script>            
            <script type="text/javascript" src="file://${oxygenAddonBuilder.pluginInstallDir}/resources/javascript/tree-template/tree-template.js">/**/</script>            
        </head>
        <body onload="window.setTimeout(initialize, 0);">
            <ol id="tree"></ol>     
        </body>
    </html>
