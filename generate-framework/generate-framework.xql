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
declare variable $frameworkJarPath := file:path-to-native($frameworkTargetDirPath || "/framework.jar");
declare variable $frameworkUberJarPath := file:path-to-native($frameworkTargetDirPath || "/" || $frameworkId || ".jar");

declare function local:make-jar($source-dir, $jar-path, $filter) {
	let $jar-manifest-content := "Manifest-Version: 1.0" || "&#10;" || "Created-By: Saxon"
	let $processed-filter :=
		if ($filter)
		then $filter || " and not(ends-with(., '/'))"
		else "not(ends-with(., '/'))"
		
	let $file-names := 
		for $file-name in file:list($source-dir, true())[saxon:evaluate($processed-filter)]
		return $file-name
	let $file-paths :=
		for $file-name in $file-names
		return file:path-to-native($source-dir || "/" || $file-name)
	
	return
		file:write-binary(
			$jar-path,
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
};

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
	file:create-dir($frameworkTargetDirPath)
	,
	local:make-jar($frameworkTargetDirPath, $frameworkJarPath, "ends-with(., '.ser')")
	,	
	file:delete($frameworkTargetDirPath, true())
	,
	file:create-dir($frameworkTargetDirPath)
	,
	local:make-jar($frameworkDirPath, $frameworkUberJarPath, "not(contains(., '.git')) and not(. = '.project')")
	,
	let $text := file:read-text(file:path-to-native($frameworkDirPath || "/addon.xml"))
	let $text := replace($text, "</xt:version>", "." || format-dateTime(current-dateTime(), "[M01][D01][H01][m01]") || "</xt:version>")
	
	return file:write-text(file:path-to-native($frameworkTargetDirPath || "/addon.xml"), $text)	
)
