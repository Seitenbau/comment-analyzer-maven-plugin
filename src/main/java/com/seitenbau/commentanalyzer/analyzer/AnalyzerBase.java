/*-
 * -\-\-
 * com.github.seitenbau:comment-analyzer-maven-plugin
 * --
 * Copyright (C) 2019 SEITENBAU GmbH
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

package com.seitenbau.commentanalyzer.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.seitenbau.commentanalyzer.model.CommentInfo;

public interface AnalyzerBase
{
  /**
   * This method can be used for scan a file
   *
   * @param file
   * @param checkTag
   * @return
   * @throws IOException
   */
  List<CommentInfo> scanFile(File file, String checkTag) throws IOException;

  /**
   * @return should return file extension for filtering files (.java|.xml|.js)
   */
  String getFileExtension();
}
