import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val excludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    ".*Routes.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*"
  )

  private val excludedFiles: Seq[String] = Seq(
    ".*Routes",
    ".*RoutesPrefix",
    ".*ReverseRoutes",
    ".*JavaScriptReverseRoutes",
    ".*AppConfig.*",
    ".*Routes.*"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageExcludedFiles := excludedFiles.mkString(","),
    // Set to 0 while setting up
    ScoverageKeys.coverageMinimumStmtTotal := 10,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
