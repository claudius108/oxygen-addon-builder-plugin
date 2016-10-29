xquery version "3.0";

declare namespace file = "http://expath.org/ns/file";
declare namespace arch = "http://expath.org/ns/archive";
declare namespace bin = "http://expath.org/ns/binary";

declare variable $pluginInstallDirPath external;
declare variable $frameworkDirPath external;

declare variable $pluginTemplatesDirPath := file:path-to-native($pluginInstallDirPath || "/templates");

declare variable $frameworkId := file:name($frameworkDirPath);
declare variable $frameworkTargetDirPath := file:path-to-native($frameworkDirPath || "/target");
declare variable $frameworkJavaDirPath := file:path-to-native($frameworkDirPath || "/java");
declare variable $frameworkJarPath := file:path-to-native($frameworkTargetDirPath || "/" || $frameworkId || ".jar");

declare variable $jar-manifest-content := "Manifest-Version: 1.0" || "&#10;" || "Created-By: Saxon";

(
	file:copy(
		file:path-to-native($pluginInstallDirPath || "/lib/addon-builder-plugin.jar"),
		$frameworkJavaDirPath
	)
	,
	if (not(file:exists(file:path-to-native($frameworkDirPath || "/addon.xml"))))
	then 
		let $text := file:read-text(file:path-to-native($pluginTemplatesDirPath || "/addon/addon.xml"))
		let $text := replace($text, "frameworkId", $frameworkId)
		
		return file:write-text(file:path-to-native($frameworkDirPath || "/addon.xml"), $text)
	else ()
	,
	if (not(file:exists(file:path-to-native($frameworkDirPath || "/addon.xq"))))
	then file:copy(
		file:path-to-native($pluginTemplatesDirPath || "/xquery/addon.xq"),
		$frameworkDirPath
	)
	else ()
	,
	if (file:exists($frameworkJarPath))
	then file:delete($frameworkJarPath)
	else ()	
	,
	let $file-names := 
		for $file-name in file:list($frameworkTargetDirPath)[ends-with(., '.ser') or . = 'special-characters.xml']
		return $file-name
	let $file-paths :=
		for $file-name in $file-names
		return file:path-to-native($frameworkTargetDirPath || "/" || $file-name)
	
	return
		file:write-binary(
			file:path-to-native($frameworkJavaDirPath || "/framework.jar"),
			arch:create(
				(
					"META-INF/MANIFEST.MF"
					,
					for $file-name in $file-names
					return $file-name
				),
				(
					bin:encode-string($jar-manifest-content)
					,
					for $file-path in $file-paths
					return file:read-binary($file-path)
				)
			)
		)
	,	
	file:delete($frameworkTargetDirPath, true())
	,
	file:create-dir($frameworkTargetDirPath)
	,
	let $text := file:read-text(file:path-to-native($frameworkDirPath || "/addon.xml"))
	let $text := replace($text, "</xt:version>", "." || format-dateTime(current-dateTime(), "[M01][D01][H01][m01]") || "</xt:version>")
	
	return file:write-text(file:path-to-native($frameworkTargetDirPath || "/addon.xml"), $text)
)
