package org.pitest.sbt

import sbt._
import Keys._
import java.io.File
import inc.Locate
import PitKeys._
import org.pitest.mutationtest.ReportOptions
import org.pitest.mutationtest.SettingsFactory
import org.pitest.mutationtest.instrument.JarCreatingJarFinder
import org.pitest.internal.ClassPathByteArraySource
import org.pitest.mutationtest.instrument.KnownLocationJavaAgentFinder
import org.pitest.mutationtest.CompoundListenerFactory
import org.pitest.functional.FCollection
import org.pitest.mutationtest.report.OutputFormat
import org.pitest.coverage.execute.LaunchOptions
import org.pitest.classinfo.CodeSource
import org.pitest.mutationtest.Timings
import org.pitest.coverage.DefaultCoverageGenerator
import org.pitest.mutationtest.incremental.XStreamHistoryStore
import org.pitest.mutationtest.MutationStrategies
import org.pitest.mutationtest.MutationCoverage
import java.io.IOException
import java.net.URLDecoder
import org.pitest.util.Glob
import org.pitest.mutationtest.config.ConfigurationFactory
import org.pitest.testng.TestGroupConfig
import java.util.Collection
import java.util.ArrayList
import org.pitest.mutationtest.config.ConfigOption
import org.pitest.mutationtest.tooling.EntryPoint


object PitPlugin extends Plugin {

  lazy val pitestSettings = Seq(
    threads := 1, 
    maxMutationsPerClass := 0,
    verbose := false, 
    timestampedReports := true,
    mutationUnitSize := 0,
    timeoutFactor := 1.25f,
    timeoutConst := 4000,
    mutateStaticInitializers := false,
    detectInlinedCode := true,
    
    mutators := Seq(),
    outputFormats := Seq("HTML"),
    includedGroups := Seq(),
    excludedGroups := Seq(),
    
    dependencyDistance := -1,
    reportPath <<= crossTarget(_ / "pit-reports"),
    targetClasses <<= organization.apply( s => Seq(s + ".*")),
    targetTests <<= organization.apply( s => Seq(s + ".*")),  
    excludedMethods := Seq(),
    avoidCallsTo := Seq(),
    excludedClasses := Seq(),
    engine := "gregor",
    
    // using built in javaOptions for the jvmargs . . . is this the right thing to do?
    pitConfiguration <<= (engine, mutators, outputFormats, javaOptions in Test, includedGroups, excludedGroups) map Configuration,
    mutableCodePaths <<= classDirectory in Compile map (f => Seq(f)),
    pathSettings <<= (baseDirectory, reportPath, mutableCodePaths, fullClasspath in Test, sourceDirectories in Compile) map PathSettings dependsOn (compile in Compile),
    filterSettings <<= (targetClasses, targetTests, dependencyDistance) map FilterSettings,
    excludes <<= (excludedClasses, excludedMethods, avoidCallsTo) map Excludes,
    options <<=  (detectInlinedCode,mutateStaticInitializers,threads,maxMutationsPerClass,verbose,timestampedReports,mutationUnitSize,timeoutFactor,timeoutConst) map Options,
    pitestTask <<= (options, pitConfiguration, pathSettings, filterSettings, excludes) map runPitest)

  def runPitest(options : Options, conf : Configuration, paths: PathSettings, filters: FilterSettings, excludes: Excludes): Unit = {
    
    val originalCL = Thread.currentThread().getContextClassLoader()
    Thread.currentThread().setContextClassLoader(PitPlugin.getClass.getClassLoader())
    
    try {
      val pit = new EntryPoint
      val data = makeReportOptions(options, conf, paths, filters, excludes)
      val result = pit.execute( paths.baseDir, data)
      
      if ( result.getError().hasSome() ) {
        result.getError().value().printStackTrace()
      }
      
    } finally {
      Thread.currentThread().setContextClassLoader(originalCL)
    }

    println("done")

  }

  def makeReportOptions(options : Options, config : Configuration, paths: PathSettings, filters: FilterSettings, excludes: Excludes): ReportOptions = {
    val data = new ReportOptions
    data.setReportDir(paths.targetPath.getAbsolutePath())
    data.setClassPathElements(plainCollection(makeClasspath(paths)))
    data.setCodePaths(plainCollection (paths.mutatablePath map (p => p.getPath)) )
    data.setTargetClasses(plainCollection(filters.targetClasses map toGlob))
    data.setTargetTests(plainCollection(filters.targetTests map toGlob))
    data.setDependencyAnalysisMaxDistance(filters.dependencyDistance)
    data.setSourceDirs(plainCollection(paths.sources))
    data.setVerbose(options.verbose)
    data.setDetectInlinedCode(options.detectInlinedCode)
    data.setMutateStaticInitializers(options.mutateStaticInitializers)
    data.setNumberOfThreads(options.threads)
    data.setVerbose(options.verbose)
    data.setShouldCreateTimestampedReports(options.timestampedReports)
    data.setMutationUnitSize(options.mutationUnitSize)
    data.setTimeoutFactor(options.timeoutFactor)
    data.setTimeoutConstant(options.timeoutConst)
    data.setExcludedClasses(plainCollection(excludes.excludedClasses map toGlob))
    data.setExcludedMethods(plainCollection(excludes.excludedMethods map toGlob))
    data.setLoggingClasses(plainCollection(excludes.excludedMethods))
    
    data.setMutationEngine(config.engine)
    data.setMutators(plainCollection(config.mutators))
    data.addOutputFormats(plainCollection(config.outputFormats map (OutputFormat.valueOf(_))))
    
    val conf = new TestGroupConfig(plainCollection(config.excludedGroups),plainCollection(config.includedGroups))
    data.setGroupConfig(conf)
    
    val configFactory = new ConfigurationFactory(conf,
        new ClassPathByteArraySource(data.getClassPath()));
    data.setConfiguration(configFactory.createConfiguration())
    

    data
  }
  
  def toGlob(s : String ) = new Glob(s)
  /**
   * The implicit conversion to collection is not enough - we need a plain
   * old java collection class to satisify xstream
   */
  def plainCollection [T](s : Seq[T]) : java.util.List[T] = {
    import scala.collection.JavaConversions._
    new ArrayList(s)
  }

  private def makeClasspath(paths: org.pitest.sbt.PathSettings) = {
    val cp = paths.classPath.files map (f => f.getPath().trim())

    val ownPath = classOf[ReportOptions].getProtectionDomain().getCodeSource().getLocation().getPath();
    val decodedPath = URLDecoder.decode(ownPath, "UTF-8");
    Seq(decodedPath) ++ cp
  }

}