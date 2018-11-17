With this plugin, you can import AARs (android libraries) to a pure java gradle project, so that you can reference classes in them.

## Usage

The ${lastest-version} of this plugin is [![Release](https://jitpack.io/v/nekocode/Gradle-Import-Aar.svg)](https://jitpack.io/#nekocode/Gradle-Import-Aar). Copy below code to the build.gradle of your java project:

```gradle
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.github.nekocode:Gradle-Import-Aar:{lastest-version}"
    }
}

apply plugin: 'import-aar'
```

Now you can use the `aarCompileOnly` configuration to add aar dependencies:

```gradle
dependencies {
    aarCompileOnly "com.android.support:appcompat-v7:27.0.2"
}
```

There is a demo module [pure-java-lib](pure-java-lib) in this porject, you can check it to learn more details.
