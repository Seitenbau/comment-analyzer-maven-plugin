

## Parameters / configuration

* checkTag -> tag for scanning comments in files (default=DEBT)
* dirToCheck -> directory for scanning files (default=./)
* htmlResultPath -> configure path for generated html showing related code areas (default=./generated.html)

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
        <version>LATEST</version>
        <executions>
          <execution>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```
