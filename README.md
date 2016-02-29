This is based on https://www.ricston.com/blog/xjc-plugin/

Create a directory to hold the new plugins project and move into it:
``` bash
mkdir -p ~/Projects/xjc-plugin-example
cd ~/Projects/xjc-plugin-example
```

Initialize the project structure using:
``` bash
gradle init --type java-library
```

NOTE: More info can be found at: https://docs.gradle.org/current/userguide/build_init_plugin.html

Add the following XJC Plugin dependency to the build.gradle:
``` bash
    compile 'com.sun.xml.bind:jaxb-xjc:2.2.6'
```

Download the example plugin:
``` bash
PACKAGE_NAME_DIR='com/thescheideggers/xjcpluginexample'
mkdir -p src/main/java/${PACKAGE_NAME_DIR}
wget -P src/main/java/${PACKAGE_NAME_DIR} \
   https://raw.githubusercontent.com/wdschei/xjc-plugin-example/master/src/java/com/thescheideggers/xjcpluginexample/XJCPluginExample.java
```

Update the package name of the downloaded example:
``` bash
PACKAGE_NAME=$(echo ${PACKAGE_NAME_DIR} | sed 's/\//\./g')
sed -i "s/^package .*$/package ${PACKAGE_NAME};/" src/main/java/${PACKAGE_NAME_DIR}/XJCPluginExample.java
```

Create the required XJC Plugin configuration file:
``` bash
mkdir -p src/main/resources/META-INF/services
echo ${PACKAGE_NAME}.XJCPluginExample > src/main/resources/META-INF/services/com.sun.tools.xjc.Plugin
```

Add a handy Gradle tasks:
``` ruby
cat << 'EOF' >> build.gradle

task unversionedJar(type: Jar, dependsOn: 'jar') {
    version = null
    from sourceSets.main.output
}
EOF
```

Build the new XJC Plugin Example:
``` bash
gradle clean unversionedJar build
```

To use the locally built plugin with the gradle-jaxb-plugin, add the following to the `build.gradle`:
``` ruby
repositories {
    flatDir { dirs '../xjc-plugin-example/build/libs' } // gradle unversionedJar
    //maven {                                           // Rackspace public repository
    //    url "https://maven.research.rackspacecloud.com/content/repositories/public/"
    //}
}

dependencies {
    xjc ":xjc-plugin-example"                           // gradle unversionedJar
    //xjc "<PACKAGE_GROUP>:xjc-plugin-example:0.0.0"    // Published release
}

jaxb {
    xjc {
        args = [
            "-Xexample-plugin",
            "-Xexample-plugin-varname", "varname",
            "-Xexample-plugin-getter", "getter",
            "-Xexample-plugin-setter", "setter"
        ]
    }
}
```
