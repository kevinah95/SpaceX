# Sharing Build Logic using `buildSrc`

For more information [here](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html#sec:sharing_logic_via_convention_plugins).

## Convention plugin

If you create a script file like `java-common-conventions.gradle(.kts)`, 
you can treat it as a plugin and apply it in your subprojects. 
The ID of the plugin is the name of the build file without the `gradle(.kts)` extension. 
This kind of plugin is called a **convention plugin**.

