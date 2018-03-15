In some cases, we need to reference some Android classes in a pure Java gradle module. If these classes are in a common jar, you can import them into your module directly. But if they are in a AAR package, there's no an official way to import them. You need to manually download the AAR package and unpack the `classes.jar` from it. And then import this `classes.jar` file directly.

But in fact, we can use the gradle script to automate these works. This project demonstrates how to do it. See the major gradle file [build.gradle](pure-java-lib/build.gradle) for more detail.
