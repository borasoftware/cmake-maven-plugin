## cmake-maven-plugin

The Balau cmake-maven-plugin plugin provides a mechanism for driving CMake execution via Maven.

The plugin allows a simple and concise way of integrating CMake projects into a Maven based SDLC infrastructure. 

The following Maven goals are defined:

- clean;
- configure;
- compile;
- test-compile.

Three of these goals are similar to the standard Maven goals for cleaning build files, compiling production code, and compiling test code. The 'configure' goal (bound by default to the validate Maven phase) executes CMake to configure the project before compilation.

See the Maven chapter in the [Balau documentation](https://borasoftware.com/doc/balau/latest/manual/NonCode/Maven.html) for more information on usage.
