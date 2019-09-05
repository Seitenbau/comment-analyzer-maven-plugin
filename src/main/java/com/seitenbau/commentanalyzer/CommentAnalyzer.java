package com.seitenbau.commentanalyzer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;

@Mojo(name = "check", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CommentAnalyzer extends AbstractMojo
{

  @Parameter( property = "technicalDebtCheck.checkTag", defaultValue = "DEBT" )
  private String checkTag;

  @Parameter( property = "technicalDebtCheck.dirToCheck", defaultValue = "./" )
  private String dirToCheck;

  @Parameter( property = "technicalDebtCheck.htmlResultPath", defaultValue = "./debtListGenerated.html" )
  private String htmlResultPath;

  public static void main(String[] startArgs) throws MojoFailureException, MojoExecutionException, IOException
  {
    CommentAnalyzer commentAnalyzer = new CommentAnalyzer();

    commentAnalyzer.dirToCheck = "/home/jgraf2/IdeaProjects/pkp";
    commentAnalyzer.checkTag = "DEBT";
    commentAnalyzer.htmlResultPath = "./debtListGenerated.html";

    commentAnalyzer.execute();

  }

  public void execute()
  {
    File dir = new File(dirToCheck);

    List<CommentInfo> debtList = new ArrayList<>();

    for (File file : getAllJavaFiles(dir)) {
      try
      {
        scanFile(file, debtList);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    try
    {
      generateHtml(debtList);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void scanFile(File file, List<CommentInfo> commentList) throws IOException
  {
    System.out.println("Scanning File: " + file.getName());
    CompilationUnit compilationUnit = StaticJavaParser.parse(FileUtils.readFileToString(file, "UTF-8"));

    String classPath = compilationUnit.getPackageDeclaration().get().getName() + "." + file.getName();

    CommentInfo commentInfo = checkClass(compilationUnit, classPath, commentList);

    loopMethods(compilationUnit, classPath, commentList, commentInfo);
  }

  public CommentInfo checkClass(CompilationUnit compilationUnit, String classPath, List<CommentInfo> commentList)
  {
    CommentInfo commentInfo = new CommentInfo();
    commentInfo.setClassPath(classPath);
    commentInfo.setMethodName("class");

    compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterfaceDeclaration ->
    {
      if (classOrInterfaceDeclaration.getComment().isPresent() && classOrInterfaceDeclaration.getComment().get().toString().contains(checkTag))
      {
        commentInfo.setFound(true);
        commentInfo.getLines().add(classOrInterfaceDeclaration.getComment().get().getRange().get().begin.line);
      }
    });

    if (commentInfo.isFound())
    {
      commentInfo.setCodeSnipped(compilationUnit.toString());
      commentList.add(commentInfo);
    }

    return commentInfo;
  }

  public void loopMethods(CompilationUnit compilationUnit, String classPath, List<CommentInfo> commentList, CommentInfo commentInfo)
  {
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
  }

  public CommentInfo scanMethod(MethodDeclaration methodDeclaration)
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

  public void generateHtml(List<CommentInfo> commentList) throws IOException
  {
    StringBuilder sb = new StringBuilder();
    File destinationFile = new File(htmlResultPath);
    URL url = getClass().getClassLoader().getResource("result.html");
    String main = IOUtils.toString(url, StandardCharsets.UTF_8);
    url = getClass().getClassLoader().getResource("snipped.html");
    String snipped = IOUtils.toString(url, StandardCharsets.UTF_8);

    getLog().info(commentList.size() + " comments found by tag "+ checkTag);

    if(!commentList.isEmpty()) {
      commentList.forEach(commentInfo ->
      {
        String newSnipped = snipped.replace("{classPath}", commentInfo.getClassPath())
            .replace("{methodName}", commentInfo.getMethodName())
            .replace("{codeSnipped}", commentInfo.getCodeSnipped())
            .replace("{count}", String.valueOf(commentInfo.getLines().size()));

        sb.append(newSnipped);
      });

    } else {
      sb.append("  <div class=\"card-panel teal lighten-2\">No comments with tag "+ checkTag + " found!</div>\n");
    }

    FileUtils.writeStringToFile(destinationFile, main.replace("{debtList}", sb.toString()));
  }

  public List<File> getAllJavaFiles(File dir) {
    List<File> list = new ArrayList<>();
    Arrays.stream(dir.listFiles()).forEach(file -> {
      if(file.isDirectory()) {
        list.addAll(getAllJavaFiles(file));
      } else if(file.isFile() && file.getName().endsWith(".java")) {
        list.add(file);
      }
    });
    return list;
  }
}
