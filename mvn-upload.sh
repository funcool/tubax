#!/bin/sh
mvn deploy:deploy-file -Dfile=target/tubax.jar -DpomFile=pom.xml -DrepositoryId=clojars -Durl=https://clojars.org/repo/
