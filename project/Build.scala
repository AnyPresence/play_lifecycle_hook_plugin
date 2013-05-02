import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {


    val appName         = "lifecyclehooks"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
        // Ensure that the application.conf and routes files are not included in the compiled jar file
        // when it is packaged
        defaultExcludes in Compile in unmanagedResources := "*.conf" || "routes" || "Route*"
      ).settings(
          // Exclude all compiled routing classes when the application is packaged
          mappings in (Compile,packageBin) ~= { (ms: Seq[(File, String)]) =>
            val routesStraightFilter = """(Routes\$?)(\$anon.*)?.class""".r
            val controllerRoutesFilter = """([\w\$/]*)controllers/routes([\w\$]*).class""".r
            val routesRefFilter = """([\w\$/]*)routes(\$ref|\$javascript)?.class""".r
            val refReverseFilter = """([\w\$/]*)ref/Reverse([\w\$]*).class""".r
            val controllerReverseFilter = """controllers/([\w\$/]*)Reverse([\w\$]*).class""".r
            val otherRoutesFilter  = """controllers/([\w\$/]*)routes.class""".r
            ms filter {
                case (file, toPath) => {
                    println(file.toString)
                    println(toPath)
                    val routesMatch = toPath match {
                        case routesStraightFilter(_,_) => true
                        case controllerRoutesFilter(_,_) => true
                        case routesRefFilter(_,_) => true
                        case refReverseFilter(_,_) => true
                        case controllerReverseFilter(_,_) => true
                        case otherRoutesFilter(_) => true
                        case _ => false }
                    !routesMatch && toPath != "routes" && toPath != "application.conf"
                }
            }
        }
     )


}
