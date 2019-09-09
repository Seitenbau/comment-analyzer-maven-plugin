# comment-analyzer-maven-plugin

parameters / configuration

* checkTag -> tag for scanning comments in files (default=DEBT)
* dirToCheck -> directory to scanning all .java files (default=./)
* htmlResultPath -> configure path for html with result (default=./generated.html)

create own analyzer for another filetype:

* implement AnalyzerBase
* use @Analyzer as annotation
* handel scanFile() and create CommentInfo for every code area you want
* Your Analyzer will be automatically used and the result will be shown in the generated html file

