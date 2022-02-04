Maven Plugin that searches for comments with the configured tags in the
projects source code and generates an HTML page with the found results.

The main usage of this plugin is to search for code that are marked as (technical) debt via code comments.

Currently, the plugin supports java code and will analyze line and block comments
on classes, methods and in method bodies.

## Parameters / configuration [![Build Status](https://travis-ci.org/Seitenbau/comment-analyzer-maven-plugin.svg?branch=master)](https://travis-ci.org/Seitenbau/comment-analyzer-maven-plugin)

* checkTag -> tag for scanning comments in files (default=DEBT)
* dirToCheck -> directory for scanning files (default=./)
* htmlResultPath -> configure path for generated html showing related code areas (default=./generated.html)
* excludes -> skipping files by regex


## Create own analyzer for another filetype:

* implement AnalyzerBase
* use @Analyzer as annotation
* handel scanFile() and create CommentInfo for every code area you want
* Your Analyzer will be automatically used and the result will be shown in the generated html file



## Usage:
```
      <plugin>
        <groupId>com.github.seitenbau</groupId>
        <artifactId>comment-analyzer-maven-plugin</artifactId>
        <version>0.1.15</version>
        <configuration>
            <dirToCheck>./</dirToCheck>
            <htmlResultPath>generated.html</htmlResultPath>
            <checkTag>DEBT</checkTag>
            <excludes>
                <exclude>.*.Test.*.java</exclude>
            </excludes>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```
