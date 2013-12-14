sbt-pit
=======

sbt plugin for [pitest](http://pitest.org) mutation testing system

**Please note that pit cannot currently (meaningfully) mutate scala . . . but I'm working on it**

This plugin can however be used for java projects built using sbt.
 
## Setup

pit-sbt is available from the typesafe community ivy repo.

Add plugin to project/plugins.sbt

``` scala
addSbtPlugin("org.pitest.sbt" %  "sbt-pit" % "0.32")
```

Setup properties in build.sbt

``` scala
import org.pitest.sbt._

PitKeys.threads := 2

PitKeys.targetClasses := Seq("com.example.*")

PitKeys.excludedMethods := Seq("hashCode", "equals")

PitKeys.mutators := Seq("DEFAULTS", "REMOVE_CONDITIONALS")

PitKeys.verbose := true
```

See source for details of available settings.

## Version history

### 0.32

Updated to use pitest 0.32

See http://pitest.org/downloads/ for details of changes in pitest-0.32

### 0.3

First release based on pit 0.31


