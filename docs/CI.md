# Continuous integration

GitHub Actions builds an unsigned APK for review, runs settings and installer
tests, checks source formatting and documentation links, and scans dependencies
and repository content. CI does not sign APKs or access the local release
keystore.

The application has no external runtime dependencies. Security scanning checks
source and workflow inputs; a clean scan does not replace device testing.
Coverage gates apply to isolated, project-owned logic. Android framework
callbacks and the GPU shader are additionally checked through package inspection
and emulator execution, since host line coverage does not establish their
rendered behavior.
