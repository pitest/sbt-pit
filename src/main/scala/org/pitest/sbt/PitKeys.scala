package org.pitest.sbt

import sbt._
import Keys._

private[sbt] case class Configuration(engine : String, mutators : Seq[String], outputFormats : Seq[String], jvmArgs : Seq[String], includedGroups : Seq[String], excludedGroups : Seq[String])
private[sbt] case class Options(detectInlinedCode : Boolean, mutateStaticInitializers : Boolean, threads : Int, maxMutationsPerClass : Int, verbose : Boolean, timestampedReports : Boolean, mutationUnitSize : Int, timeoutFactor : Float, timeoutConst : Long )
private[sbt] case class PathSettings(baseDir : File, targetPath: File, mutatablePath: Seq[File], classPath: Classpath, sources : Seq[File])
private[sbt] case class FilterSettings(targetClasses : Seq[String], targetTests : Seq[String], dependencyDistance : Int)
private[sbt] case class Excludes(excludedClasses : Seq[String], excludedMethods : Seq[String], avoidCallsTo : Seq[String])

object PitKeys {
    val pitestTask = TaskKey[Unit]("pitest")
    
    val engine = SettingKey[String]("pit-engine")
    val mutators = SettingKey[Seq[String]]("pit-mutators")
    val outputFormats = SettingKey[Seq[String]]("pit-outputFormats")
    val includedGroups = SettingKey[Seq[String]]("pit-includedGroups")
    val excludedGroups = SettingKey[Seq[String]]("pit-excludedGroups")
    
    val targetClasses = SettingKey[Seq[String]]("pit-target-classes")
    val targetTests = SettingKey[Seq[String]]("pit-target-tests")
    val dependencyDistance = SettingKey[Int]("pit-max-dependency-distance")
    val excludedMethods = SettingKey[Seq[String]]("pit-excluded-methods")
    val excludedClasses = SettingKey[Seq[String]]("pit-excluded-classes")
    val avoidCallsTo = SettingKey[Seq[String]]("pit-avoid-calls-to")    
    val threads = SettingKey[Int]("pit-threads") 
    val maxMutationsPerClass = SettingKey[Int]("pit-max-mutation-per-class")
    val verbose = SettingKey[Boolean]("pit-verbose") 
    val timestampedReports = SettingKey[Boolean]("pit-timestampedReports") 
    val mutationUnitSize = SettingKey[Int]("pit-mutationUnitSize")
    val timeoutFactor = SettingKey[Float]("pit-timeoutFactor")
    val timeoutConst = SettingKey[Long]("pit-timeoutConst")
    val mutateStaticInitializers = SettingKey[Boolean]("pit-mutate-static-initializers")
    val detectInlinedCode =  SettingKey[Boolean]("pit-detect-inlined-code")
    
    /** Output path for reports. Defaults to <code>target / "pit-reports"</code>. */
    val reportPath = SettingKey[File]("pit-target-path")
    
    
    private[sbt] val mutableCodePaths = TaskKey[Seq[File]]("mutable-code-path")
    private[sbt] val pathSettings = TaskKey[PathSettings]("pit-path-settings")           
    private[sbt] val filterSettings = TaskKey[FilterSettings]("pit-filter-settings")
    private[sbt] val excludes = TaskKey[Excludes]("pit-excludes")
    private[sbt] val options = TaskKey[Options]("pit-options")
    private[sbt] val pitConfiguration = TaskKey[Configuration]("pit-configuration")
    

}