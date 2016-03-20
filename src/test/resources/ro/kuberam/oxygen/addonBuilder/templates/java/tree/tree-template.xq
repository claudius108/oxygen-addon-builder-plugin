declare namespace ua = "http://expath.org/ns/user-agent";
declare namespace tmx = "http://www.lisa.org/tmx14";
declare namespace tei = "http://www.tei-c.org/ns/1.0";
declare variable $ua:document := /;
declare variable $bibl-template as element() :=
    <bibl xmlns="http://www.tei-c.org/ns/1.0" type="">
        <ptr target="" />
        <date type="" />
        <citedRange />
    </bibl>;
declare variable $cit-template as element() :=
    <cit xmlns="http://www.tei-c.org/ns/1.0">
        <quote />
        <bibl xmlns="http://www.tei-c.org/ns/1.0" type="">
        <ptr target="" />
        <date type="" />
        <citedRange />
    </bibl>
    </cit>;
declare variable $def-template as element() :=
    <def xmlns="http://www.tei-c.org/ns/1.0" n="" />;
declare variable $sense-template as element() :=
    <sense xmlns="http://www.tei-c.org/ns/1.0" xml:id="id" level="">
        <idno type="" />
        <def xmlns="http://www.tei-c.org/ns/1.0" n="" />
        <cit xmlns="http://www.tei-c.org/ns/1.0">
        <quote />
        <bibl xmlns="http://www.tei-c.org/ns/1.0" type="">
        <ptr target="" />
        <date type="" />
        <citedRange />
    </bibl>
    </cit>
    </sense>;
 
declare variable $root-nodes := //tmx:body/tmx:sense;
declare variable $root-nodes-path := "//tmx:body/tmx:sense";
declare variable $item-template := <sense xmlns="http://www.tei-c.org/ns/1.0" level="" xml:id="id"><idno type=""/><def n=""/><cit><quote/><bibl type=""><ptr target=""/><date type=""/><citedRange/></bibl></cit></sense>;
declare variable $tree-height := "250px";

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
    <html count="{count($root-nodes)}">
        <head>
            <title>Tree template</title>
            <meta charset="UTF-8" />
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <link rel="stylesheet" type="text/css" href="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/css/footer.css"/>
            <script type="text/javascript" src="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/jquery/jquery-2.1.4.min.js">/**/</script>
            <link type="text/css" rel="stylesheet" href="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/jquery-ui/jquery-ui.css" />
            <script type="text/javascript" src="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/jquery-ui/jquery-ui.min.js">/**/</script>
            <link href="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css" />
            <script src="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/fancytree/jquery.fancytree-all.min.js" type="text/javascript">/**/</script>
            <script type="text/javascript" src="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/jquery.ui-contextmenu/jquery.ui-contextmenu.min.js">/**/</script>
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
            <script type="text/javascript" src="file:///home/claudius/.com.oxygenxml.author/extensions/v16.1/plugins/http___claudius108.users.sourceforge.net_repos_addon_builder_plugin_addon.xml/addon-builder-plugin/resources/javascript/tree-template/tree-template.js">/**/</script>
        </head>
        <body onload="window.setTimeout(initialize, 0);">
            <ol id="tree"></ol>
            <div id="footer">
                <button style="float: right;" onclick="UserInterfaceBridge.closeDialogWindow();">Close</button>
            </div>
        </body>
    </html>
