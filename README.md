
# corporation-tax

This is a placeholder README.md for a new repository

Service Manager: `sm2 --start DASS_CTCORE_ALL`

To run all tests and coverage: `sbt scalafmtAll clean compile coverage test it/test coverageOff coverageReport`

In local and staging the service will be routing to [corporation-tax-stubs]("https://github.com/hmrc/corporation-tax-stubs") to run the service against [rds-datacache-proxy]("https://github.com/hmrc/rds-datacache-proxy/")
run service with: `sbt "run -Dmicroservice.services.rds-datacache-proxy.port=6992 -Dmicroservice.services.rds-datacache-proxy.path=/rds-datacache-proxy"`
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").