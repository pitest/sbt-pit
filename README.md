sbt-pit
=======

sbt plugin for [pitest](http://pitest.org) mutation testing system

**Requires pit 0.31-SNAPSHOT**

**Please note that pit cannot currently (meaningfully) mutate scala . . . but I'm working on it**

This plugin can however be used for java projects built using sbt.
 
## Setup

Add plugin to project/plugins.sbt

``` scala
addSbtPlugin("org.pitest.sbt" %  "sbt-pit" % "0.1")
```

Setup properties in build.sbt

``` scala
import org.pitest.sbt._

PitKeys.threads := 2

PitKeys.targetClasses := Seq("com.example.*")

PitKeys.excludedMethods := Seq("hashCode", "equals")

PitKeys.verbose := true
```

See source for details of available settings.



