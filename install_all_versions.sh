#!/usr/bin/env bash
# versions=( "1.8" "1.8.3" "1.8.8" "1.9.2" "1.9.4" "1.10.2" "1.11.2" "1.12.2" "1.13" "1.13.1" "1.14.1" "1.15" "1.16.1" "1.16.3" "1.16.4" "1.17.1" "1.18.1" "1.18.2" "1.19.2" "1.20.1" )
versions=( "1.16.4" )

for version in "${versions[@]}"
do
  /Users/natgreen/Library/Java/JavaVirtualMachines/corretto-11.0.20/Contents/Home/bin/java -jar BuildTools.jar --rev "${version}" --remapped
  /Users/natgreen/Library/Java/JavaVirtualMachines/corretto-11.0.20/Contents/Home/bin/java -jar BuildTools.jar --rev "${version}" --compile craftbukkit
  #java -jar BuildTools.jar --rev "${version}" --remapped
  #java -jar BuildTools.jar --rev "${version}" --compile craftbukkit
done
