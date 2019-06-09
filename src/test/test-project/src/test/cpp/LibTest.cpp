
#include <Lib.hpp>

#include <iostream>
#include <sstream>

class TestException : public std::exception {
    public: const char * what() {
        return "Strings are not equals.";
    }
};

int main() {
	std::cout << "Starting test tests.\n";

	std::ostringstream str;

	hello(str);

	const std::string expected = str.str();

	if (expected != "Hello, world.\n") {
    	throw TestException();
	}

	std::cout << "Finished test tests.\n";
	return 0;
}
