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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.seitenbau.commentanalyzer.CommentAnalyzer;
import com.seitenbau.commentanalyzer.model.CommentInfo;

@Analyzer
public class JavaAnalyzer implements AnalyzerBase
{

  private String checkTag;

  /**
   * Scan the given file for {@link #checkTag} with a java parser in two steps
   * 1. scan class by {@link #checkClass(CompilationUnit, String)} Method
   * 2. scan trough all methods of class with {@link #scanMethod(MethodDeclaration)}
   *
   * @param file
   * @throws IOException
   */
  @Override
  public List<CommentInfo> scanFile(File file, String checkTag) throws IOException
  {
    this.checkTag = checkTag;

    List<CommentInfo> commentList = new ArrayList<>();

    CommentAnalyzer.getInstance().getLog().debug("Scanning File: " + file.getName());
    CompilationUnit compilationUnit = StaticJavaParser.parse(FileUtils.readFileToString(file, "UTF-8"));

    String classPath = compilationUnit.getPackageDeclaration().get().getName() + "." + file.getName();

    CommentInfo commentInfo = checkClass(compilationUnit, classPath);

    if (commentInfo.isFound())
    {
      commentList.add(commentInfo);
    }

    commentList.addAll(loopMethods(compilationUnit, classPath, commentInfo));

    return commentList;
  }

  /**
   * If the comments of the class contain the {@łink #checkTag}, {@link CommentInfo} will be created and added to param
   * commentList
   *
   * @param compilationUnit
   * @param classPath
   * @return
   */
  private CommentInfo checkClass(CompilationUnit compilationUnit, String classPath)
  {
    CommentInfo commentInfo = new CommentInfo();
    commentInfo.setClassPath(classPath);
    commentInfo.setMethodName("class");

    compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterfaceDeclaration ->
    {
      if (classOrInterfaceDeclaration.getComment().isPresent() &&
          classOrInterfaceDeclaration.getComment().get().toString().contains(checkTag))
      {
        commentInfo.setFound(true);
        commentInfo.getLines().add(classOrInterfaceDeclaration.getComment().get().getRange().get().begin.line);
      }
    });

    if (commentInfo.isFound())
    {
      commentInfo.setCodeSnipped(compilationUnit.toString());
    }

    return commentInfo;
  }

  /**
   * Loop through all methods and add found comments to <code>commentList</code>, if the class is not completely
   * tracked
   *
   * @param compilationUnit
   * @param classPath
   * @param commentInfo
   */
  private List<CommentInfo> loopMethods(CompilationUnit compilationUnit, String classPath, CommentInfo commentInfo)
  {
    List<CommentInfo> commentList = new ArrayList<>();

    compilationUnit.findAll(MethodDeclaration.class).stream().forEach(methodDeclaration ->
    {
      CommentInfo scanDebt = scanMethod(methodDeclaration);
      scanDebt.setClassPath(classPath);
      scanDebt.setMethodName(methodDeclaration.getNameAsString());

      if (scanDebt.isFound() && commentInfo.isFound())
      {
        commentInfo.getLines().addAll(scanDebt.getLines());
      }
      else if (scanDebt.isFound() && !commentInfo.isFound())
      {
        commentList.add(scanDebt);
      }
    });

    return commentList;
  }

  /**
   * The methods java doc and all comments inside the method will be check for the {@link #checkTag}
   *
   * @param methodDeclaration
   * @return
   */
  private CommentInfo scanMethod(MethodDeclaration methodDeclaration)
  {
    CommentInfo commentInfo = new CommentInfo();

    Optional<JavadocComment> optionalJavadocComment = methodDeclaration.getJavadocComment();
    if (optionalJavadocComment.isPresent() && optionalJavadocComment.get().toString().contains(checkTag))
    {
      commentInfo.setFound(true);
      commentInfo.getLines().add(optionalJavadocComment.get().getRange().get().begin.line);
    }

    methodDeclaration.getChildNodes().forEach(node ->
    {

      node.getAllContainedComments().stream().filter(comment -> comment.toString().contains(checkTag)).forEach(comment ->
      {
        commentInfo.setFound(true);
        commentInfo.getLines().add(comment.getRange().get().begin.line);
      });
    });

    if (commentInfo.isFound())
    {
      commentInfo.setCodeSnipped(methodDeclaration.toString());
    }

    return commentInfo;
  }

  @Override
  public String getFileExtension()
  {
    return ".java";
  }
}
