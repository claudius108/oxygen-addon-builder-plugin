xquery version "3.0";

declare variable $framework-id := (//field[@name  = 'name']/String/text())[1];
declare variable $cssDescriptors-field := //field[@name  = 'cssDescriptors']/cssFile-array;
declare variable $classpath-field := //field[@name  = 'classpath']/String-array;
declare variable $classpath-field-string := //field[@name  = 'classpath']/String-array/string();
declare variable $classpath-items := ("${framework}/java/plugin.jar", "${framework}/java/" || $framework-id || ".jar", "${oxygenInstallDir}/jre/lib/ext/jfxrt.jar");
declare variable $authorExtensionStateListener-item := //field[@name  = 'authorExtensionStateListener'];

declare updating function local:process-css-descriptors() {
    let $cssFile-descriptor :=
        <cssFile>
            <field name="href">
                <String>${{framework}}/resources/css/framework.less</String>
            </field>
            <field name="title">
                <String/>
            </field>
            <field name="alternate">
                <Boolean>false</Boolean>
            </field>
        </cssFile>
    
    return insert node $cssFile-descriptor into $cssDescriptors-field
};

declare updating function local:process-classpath-field($classpath-item as xs:string) {
    insert node element String {$classpath-item} into $classpath-field
};

declare updating function local:process-authorExtensionStateListener-field() {
    (
        delete node $authorExtensionStateListener-item/element()
        ,
        insert node element String {"AuthorExtensionStateListener"} into $authorExtensionStateListener-item
    )
};

(
    if (contains($cssDescriptors-field/string(), '${framework}/resources/css/framework.less'))
    then ()
    else local:process-css-descriptors()
    ,
    for $classpath-item in $classpath-items
    return
        if (contains($classpath-field-string, $classpath-item))
        then ()
        else local:process-classpath-field($classpath-item)
    ,
    local:process-authorExtensionStateListener-field()  
)
