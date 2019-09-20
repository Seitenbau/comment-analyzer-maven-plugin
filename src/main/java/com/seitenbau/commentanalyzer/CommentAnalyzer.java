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

package com.seitenbau.commentanalyzer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
public final class CommentAnalyzer extends AbstractMojo
{
  @Getter
  private static CommentAnalyzer instance;

  @Getter
  @Parameter(property = "commentanalyzer.checkTag", defaultValue = "DEBT")
  private String checkTag;

  @Parameter(property = "commentanalyzer.dirToCheck", defaultValue = "./")
  private String dirToCheck;

  @Parameter(property = "commentanalyzer.htmlResultPath", defaultValue = "generated.html")
  private String htmlResultPath;

  @Parameter(property = "exclude")
  private String[] excludes;

  private Map<String, AnalyzerBase> analyzerMap = new HashMap<>();

  /**
   * Method called by the maven check goal during the "generate resources" lifecycle phase.
   */
  @Override
  public void execute()
  {
    this.instance = this;

    Reflection.getAnalyzerClasses().forEach(aClass ->
    {
      try
      {
        AnalyzerBase base = (AnalyzerBase) aClass.newInstance();
        analyzerMap.put(base.getFileExtension(), base);
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

    List<File> fileList = getAllFiles(new File(dirToCheck));

    List<File> newFileList = excludeFiles(fileList);

    fileList.removeAll(newFileList);
    fileList.forEach(file -> getLog().debug("skipping " + file.getName()));

    newFileList.forEach(file ->
    {
      try
      {
        String fileEnding = "." + FilenameUtils.getExtension(file.getName());

        List<CommentInfo> result = analyzerMap.get(fileEnding).scanFile(file, checkTag);
        commentList.addAll(result);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    });

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
   * loops through directory and return all files
   *
   * @return list with all files
   */
  private List<File> getAllFiles(File dir)
  {
    List<File> list = new ArrayList<>();

    File[] array = dir.listFiles(file ->
    {
      String fileEnding = "." + FilenameUtils.getExtension(file.getName());
      return analyzerMap.containsKey(fileEnding) || file.isDirectory();
    });

    Arrays.stream(array).forEach(file ->
    {
      if (file.isDirectory())
      {
        list.addAll(getAllFiles(file));
      }
      else if (file.isFile())
      {
        list.add(file);
      }
    });
    return list;
  }

  public List<File> excludeFiles(List<File> files)
  {
    List<File> result = new ArrayList<>();

    Arrays.asList(this.excludes).forEach(regex -> result.addAll(filterFiles(files, regex)));
    return result;
  }

  public List<File> filterFiles(List<File> files, String regex)
  {
    Pattern pattern = Pattern.compile(regex);
    return files.stream().filter(file -> !pattern.matcher(file.getName()).matches()).collect(Collectors.toList());
  }
}
