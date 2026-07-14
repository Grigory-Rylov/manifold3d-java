#!/bin/bash

set -e
javac --enable-preview --source 17 -d target/classes src/main/java/com/cad/*.java

java --enable-preview --enable-native-access=ALL-UNNAMED -cp "target/classes" -Djava.library.path=/d/git/manifold-c/cad-app/src/resources/natives/win-x86_64 com.cad.ManifoldCADApp
