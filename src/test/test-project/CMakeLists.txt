cmake_minimum_required(VERSION 3.6.3)
project(TestProject VERSION 1.0.0)

############################# CMAKE CONFIGURATION #############################

## Disallow in-source builds
if ("${PROJECT_BINARY_DIR}" STREQUAL "${PROJECT_SOURCE_DIR}")
	message(FATAL_ERROR
		"
No in-source builds please."
		"
  mkdir build; cd build; cmake .."
		"
Please first remove the files created by this failed run with"
		"
  rm -rf CMakeCache.txt CMakeFiles"
)
endif ()

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

include_directories(src/main/cpp)
include_directories(${CMAKE_BINARY_DIR})

################################### COMPILER ##################################

set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0 -g")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3")

message(STATUS "CMAKE_CXX_FLAGS: ${CMAKE_CXX_FLAGS}")

#################################### MAIN #####################################

add_library(Lib src/main/cpp/Lib.hpp src/main/cpp/Lib.cpp)

add_executable(App src/main/cpp/App.cpp)
target_link_libraries(App Lib)

#################################### TEST #####################################

add_executable(UnitTests src/test/cpp/LibTest.cpp)
target_link_libraries(UnitTests Lib)

################################# TEST RUNNER #################################

add_custom_target(
	RunUnitTests
	WORKING_DIRECTORY ${CMAKE_BINARY_DIR}/bin
	COMMAND UnitTests
)

add_dependencies(RunUnitTests UnitTests)
