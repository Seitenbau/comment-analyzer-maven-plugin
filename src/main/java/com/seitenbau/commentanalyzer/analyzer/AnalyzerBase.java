package com.seitenbau.commentanalyzer.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.seitenbau.commentanalyzer.model.CommentInfo;

public interface AnalyzerBase
{
  List<CommentInfo> scanFile(File file, String checkTag) throws IOException;


  String getFileEnding();
}
