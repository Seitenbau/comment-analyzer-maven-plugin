package com.seitenbau.commentanalyzer.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.seitenbau.commentanalyzer.model.CommentInfo;

public interface AnalyzerBase
{
  /**
   * This method can be used for scan a file
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
