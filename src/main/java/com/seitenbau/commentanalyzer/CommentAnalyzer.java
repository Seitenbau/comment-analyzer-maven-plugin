package com.seitenbau.commentanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.seitenbau.commentanalyzer.analyzer.AnalyzerBase;
import com.seitenbau.commentanalyzer.model.CommentInfo;
import com.seitenbau.commentanalyzer.util.HtmlGenerator;
import com.seitenbau.commentanalyzer.util.Reflection;

import lombok.Getter;

/**
 * Plugin for finding code areas by a defined tag in comments.
 * The plugin will generate an overview with related code areas.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CommentAnalyzer extends AbstractMojo
{

  @Getter
  @Parameter(property = "commentanalyzer.checkTag", defaultValue = "DEBT")
  private String checkTag;

  @Parameter(property = "commentanalyzer.dirToCheck", defaultValue = "./")
  private String dirToCheck;

  @Parameter(property = "commentanalyzer.htmlResultPath", defaultValue = "generated.html")
  private String htmlResultPath;

  private Map<String, AnalyzerBase> analyzerMap = new HashMap<>();

  /**
   * Method called by the maven check goal during the "generate resources" lifecycle phase.
   */
  @Override
  public void execute()
  {
    Reflection.getAnalyzerClasses().forEach(aClass ->
    {
      try
      {
        AnalyzerBase base  = (AnalyzerBase) aClass.newInstance();
        analyzerMap.put(base.getFileEnding(), base);
      }
      catch (InstantiationException e)
      {
        e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
        e.printStackTrace();
      }
    });

    List<CommentInfo> commentList = new ArrayList<>();

    for (File file : getAllJavaFiles(new File(dirToCheck)))
    {
      try
      {
        String fileEnding = "."+FilenameUtils.getExtension(file.getName());
        List<CommentInfo> result = analyzerMap.get(fileEnding).scanFile(file, checkTag);
        commentList.addAll(result);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    try
    {
      HtmlGenerator.generate(commentList, htmlResultPath, checkTag);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * loops through directory and return all .java files
   * @return list with all .java files
   */
  private List<File> getAllJavaFiles(File dir)
  {
    List<File> list = new ArrayList<>();
    Arrays.stream(dir.listFiles()).forEach(file ->
    {
      if (file.isDirectory())
      {
        list.addAll(getAllJavaFiles(file));
      }
      else if (file.isFile() && file.getName().endsWith(".java"))
      {
        list.add(file);
      }
    });
    return list;
  }
}
