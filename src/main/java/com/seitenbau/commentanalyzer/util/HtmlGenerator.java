/*-
 * -\-\-
 * com.github.seitenbau:comment-analyzer-maven-plugin
 * --
 * Copyright (C) 2016 - 2019 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.seitenbau.commentanalyzer.util;

/*-
 * #%L
 * com.github.seitenbau:comment-analyzer-maven-plugin
 * %%
 * Copyright (C) 2019 SEITENBAU GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

    CommentAnalyzer.getInstance().getLog().info(commentList.size() + " comments found by tag " + checkTag);

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
