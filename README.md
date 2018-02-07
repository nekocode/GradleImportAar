In some cases, we need to reference some Android classes in a pure Java gradle module. If these classes are in a common jar, it's very easy to import them into your module. But if they are in a AAR package, there's no an official way to import them.

But we can solve it by using some tricks. This project demonstrates these tricks. See the major gradle file [build.gradle](pure-java-lib/build.gradle) for more detail.
