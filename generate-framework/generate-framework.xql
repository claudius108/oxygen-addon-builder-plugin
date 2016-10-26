xquery version "3.0";

declare namespace file = "http://expath.org/ns/file";

declare variable $pluginInstallDir external;
declare variable $pluginTemplatesDir := file:path-to-native($pluginInstallDir || "/templates)
declare variable $pluginTemplatesDir-css := file:path-to-native($$pluginTemplatesDir || "/css)

declare variable $frameworkDir := static-base-uri();
declare variable $frameworkDir-resources-css := file:path-to-native($frameworkDir || "/resources/css");
declare variable $dir-separator := file:dir-separator;

(
	file:copy(
		file:path-to-native($pluginInstallDir || "/lib/addon-builder-plugin.jar"),
		file:path-to-native($frameworkDir ||"/java")
	)
	,
	if (not(file:exists(file:path-to-native($frameworkDir-resources-css || "framework.less"))))
	then file:copy(
		file:path-to-native($pluginTemplatesDir-css || "/framework.less"),
		$frameworkDir-resources-css
	)
	else ()
)

(:

:)