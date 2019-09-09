package com.seitenbau.commentanalyzer.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.seitenbau.commentanalyzer.CommentAnalyzer;
import com.seitenbau.commentanalyzer.model.CommentInfo;

public class HtmlGenerator
{

  /**
   * Generates a html document and shows there affected areas base of the <code>commentList</code>
   * @param commentList
   * @param htmlResultPath
   * @param checkTag
   * @throws IOException
   */
  public static void generate(List<CommentInfo> commentList, String htmlResultPath, String checkTag) throws IOException
  {
    StringBuilder sb = new StringBuilder();
    File destinationFile = new File(htmlResultPath);
    URL url = new CommentAnalyzer().getClass().getClassLoader().getResource("result.html");
    String main = IOUtils.toString(url, StandardCharsets.UTF_8);
    url = new CommentAnalyzer().getClass().getClassLoader().getResource("snipped.html");
    String snipped = IOUtils.toString(url, StandardCharsets.UTF_8);

    System.out.println(commentList.size() + " comments found by tag " + checkTag);

    if (!commentList.isEmpty())
    {
      commentList.forEach(commentInfo ->
      {
        String newSnipped = snipped.replace("{classPath}", commentInfo.getClassPath())
            .replace("{methodName}", commentInfo.getMethodName())
            .replace("{codeSnipped}", commentInfo.getCodeSnipped())
            .replace("{count}", String.valueOf(commentInfo.getLines().size()));

        sb.append(newSnipped);
      });

    }
    else
    {
      sb.append("  <div class=\"card-panel teal lighten-2\">No comments with tag " + checkTag + " found!</div>\n");
    }

    FileUtils.writeStringToFile(destinationFile, main.replace("{commentList}", sb.toString()));
  }
}
