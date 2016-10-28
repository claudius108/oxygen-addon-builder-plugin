xquery version "3.0";

declare namespace file = "http://expath.org/ns/file";

declare variable $pluginInstallDirPath external;
declare variable $frameworkDirPath external;

declare variable $pluginTemplatesDir := file:path-to-native($pluginInstallDirPath || "/templates");

declare variable $frameworkId := file:name($frameworkDirPath);
declare variable $frameworkTargetDir := file:path-to-native($frameworkDirPath || "/target");

(
	file:copy(
		file:path-to-native($pluginInstallDirPath || "/lib/addon-builder-plugin.jar"),
		file:path-to-native($frameworkDirPath ||"/java")
	)
	,
	if (not(file:exists(file:path-to-native($frameworkDirPath || "/addon.xml"))))
	then 
		let $text := file:read-text(file:path-to-native($pluginTemplatesDir || "/addon/addon.xml"))
		let $text := replace($text, "frameworkId", $frameworkId)
		
		return file:write-text(file:path-to-native($frameworkDirPath || "/addon.xml"), $text)
	else ()
	,
	if (not(file:exists(file:path-to-native($frameworkDirPath || "/addon.xq"))))
	then file:copy(
		file:path-to-native($pluginTemplatesDir || "/xquery/addon.xq"),
		$frameworkDirPath
	)
	else ()
	,
	let $text := file:read-text(file:path-to-native($frameworkDirPath || "/addon.xml"))
	let $text := replace($text, "</xt:version>", "." || format-dateTime(current-dateTime(), "[M01][D01][H01][m01]") || "</xt:version>")
	
	return file:write-text(file:path-to-native($frameworkTargetDir || "/addon.xml"), $text)
	,
	file:delete(file:path-to-native($frameworkTargetDir || "/" || $frameworkId || ".jar"))
	,
	let $file-names := file:list($frameworkTargetDir)
	let $processed-file-names :=
		for $file-name in $file-names[ends-with(., '.ser') or . = 'special-characters.xml']
		
		return $file-name
		
	let $processed-file-names := string-join($processed-file-names, "&#10;")
	
	return file:write-text(file:path-to-native($frameworkTargetDir || "/a.xml"), $processed-file-names)
)
	
