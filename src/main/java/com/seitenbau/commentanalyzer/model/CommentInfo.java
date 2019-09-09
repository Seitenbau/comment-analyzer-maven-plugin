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
