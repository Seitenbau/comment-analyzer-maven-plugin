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

package com.seitenbau.commentanalyzer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommentInfo
{
  /**
   * should set to true if checkTag was found
   */
  private boolean found;

  /**
   * will be displayed like this -> com.seitebau.commentanalyzer.CommentAnalyzer::execute()
   */
  private String classPath, methodName;

  /**
   * here you can save the line number of the comment
   */
  private List<Integer> lines = new ArrayList<>();

  /**
   * here you set the code area how ever you want
   */
  private String codeSnipped;

}
