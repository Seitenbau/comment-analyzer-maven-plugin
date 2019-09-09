package com.seitenbau.commentanalyzer.util;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.seitenbau.commentanalyzer.analyzer.Analyzer;

public class Reflection
{

  public static Set<Class<?>> getAnalyzerClasses() {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage("com.seitenbau.commentanalyzer"))
        .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));

    return reflections.getTypesAnnotatedWith(Analyzer.class);
  }
}
