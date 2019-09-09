package com.seitenbau.commentanalyzer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommentInfo
{
  private boolean found;

  private String classPath, methodName;

  private List<Integer> lines = new ArrayList<>();

  private String codeSnipped;

}
