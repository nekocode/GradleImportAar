[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Release](https://jitpack.io/v/nekocode/Gradle-Import-Aar.svg)](https://jitpack.io/#nekocode/Gradle-Import-Aar)

In some cases, we need to reference some Android classes in a pure Java gradle module. If these classes are in a common jar, you can import them into your module directly. But if they are in a AAR package, there's no an official way to import them. You need to manually download the AAR package and unpack the `classes.jar` from it. And then import this `classes.jar` file directly.

This plugin can finish above tasks automatically.

## Import

In your root `build.gradle`:

```gradle
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:3.1.3"
        classpath "com.github.nekocode:Gradle-Import-Aar:{lastest-version}"
    }
}
```

In your pure java module's `build.gradle`:

```gradle
apply plugin: 'java-library'
apply plugin: 'import-aar'

dependencies {
    aarCompileOnly "com.android.support:appcompat-v7:27.0.2"
}
```

You can see the [pure-java-lib](pure-java-lib) module to learn how to use this plugin. And this plugin has been used in the [Items](https://github.com/nekocode/Items) project, you can also reference it.
