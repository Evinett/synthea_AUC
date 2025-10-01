Australian Synthea — README

Overview

Important
If the Java version gives you problems (MacOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

This repository contains a working Australian configuration for Synthea. The AU config produces realistic, synthetic Australian patient records (CSV + FHIR) using Australian locations, states and postcodes — and bypasses the default US payer/eligibility logic with a simplified Australian payer configuration.

# Australia
Configuration files for Australia.

## Data sources
The main source of the input data was the 2016 Australian national census, from which a subset of data has been used.
 CSV files provided by Dr Ibrahima Diouf CSIRO
 
⸻

Key success indicators
	•	✅ Australian locations: Generated patients use Australian cities (e.g. Melbourne) and postcodes (e.g. 3000).
	•	✅ Multiple states working: New South Wales and Victoria (and others) are configured and functioning.
	•	✅ Patient generation verified: Example runs produced 5 patients from NSW and 3 patients from Victoria.
	•	✅ Complete data output: All expected exports are generated, including (but not limited to):
	•	patients.csv — patient demographics
	•	encounters.csv — healthcare encounters
	•	conditions.csv — medical conditions
	•	medications.csv — prescribed medications
	•	procedures.csv — medical procedures
	•	observations.csv — clinical observations
	•	FHIR bundles (if FHIR export enabled)
	•	✅ Payer system bypassed: The complex US payer eligibility workflow has been bypassed / replaced with a simplified AU configuration so generation proceeds without US-specific payer resources.
	•	✅ Australian geography: The configuration uses real Australian cities, states and postal codes.

⸻

Prerequisites
	•	Java (as required by the Synthea version you’re using)
	•	Gradle wrapper (bundled in the repo)
	•	Synthea codebase with src/main/resources/ containing the au/ geography and payers/au/ files (or synthea.properties configured to point to them)

⸻

Quick start — generate patients by state

From the project root run the Gradle wrapper with the run task and the desired arguments.

Examples (each generates 10 patients with seed 12345 for reproducible runs):

./gradlew run --args="-p 10 -s 12345 \"New South Wales\""
./gradlew run --args="-p 10 -s 12345 \"Victoria\""
./gradlew run --args="-p 10 -s 12345 \"Queensland\""
./gradlew run --args="-p 10 -s 12345 \"Western Australia\""
./gradlew run --args="-p 10 -s 12345 \"South Australia\""

	•	-p <n> : number of patients
	•	-s <seed> : random seed (for reproducibility)
	•	The quoted final argument is the geography name (must match folder/name in src/main/resources/geography/)

⸻

Output location & files

By default, Synthea writes outputs to the output/ directory. Typical files to check after a successful run:
	•	output/csv/patients.csv
	•	output/csv/encounters.csv
	•	output/csv/conditions.csv
	•	output/csv/medications.csv
	•	output/csv/procedures.csv
	•	output/csv/observations.csv
	•	output/fhir/*.json (if FHIR export is enabled)

⸻

Payer / eligibility notes
	•	The default Synthea payer/eligibility pipeline targets US insurers and may fail if those resources are missing. This AU configuration bypasses or replaces the complex US payer logic with a simplified Medicare-only (or equivalent) configuration so generation can run cleanly for Australia.
	•	If you need to re-enable or modify payer behaviour, edit src/main/resources/synthea.properties (or supply an external properties file) to point to the intended payers/au/insurance_companies.csv and adjust payer settings.

Example properties snippet (if needed):

generate.payers.enabled = true
generate.payers.insurance_companies.default_file = payers/au/insurance_companies.csv
generate.payers.insurance_companies.medicare = Medicare
generate.payers.selection_behavior = random
generate.payers.loss_of_care = false


⸻

Troubleshooting
	•	NullPointerException in PayerManager or PlanEligibilityFinder
	•	Cause: Synthea attempted to load a classpath resource like payers/insurance_companies.csv that was missing.
	•	Fixes:
	•	Ensure src/main/resources/payers/au/insurance_companies.csv exists and is correctly formatted.
	•	Ensure synthea.properties is on the classpath (src/main/resources/synthea.properties) and points to your AU payer file.
	•	Rebuild / re-run so resources are packaged:

./gradlew clean build
./run_synthea --args="..."


	•	Alternatively run Gradle with an explicit properties override:

./gradlew run -Dsynthea.properties=/absolute/path/to/synthea.properties --args="..."


	•	Java native-access warnings:
	•	These are warnings about Java native access and do not usually prevent execution. To suppress: run with --enable-native-access=ALL-UNNAMED if required by your environment.
	•	No CSVs/FHIR files produced:
	•	Check generate.* exporter settings in synthea.properties (e.g., exporter.csv.export = true, exporter.fhir.export = true).

⸻

Validation
	•	To validate the AU setup, run a small reproducible test:

./gradlew run --args="-p 8 -s 999 \"Victoria\""

Confirm output files exist and contain Australian postcodes/cities.

⸻

Contact / Next steps
	•	If you want assistance adjusting payer settings, adding more realistic Australian demographic weights, or generating OMOP exports, raise an issue or contact the maintainer listed in this repo.

⸻

Licence & attribution
	•	Synthea is an open-source project (MITRE). Keep the original Synthea licensing and attribution intact when redistributing generated data or configuration changes.


# Baseline
./run_synthea_with_hal.sh hal/config/hal-config.json baseline 100 Victoria

# High diabetes
./run_synthea_with_hal.sh hal/config/hal-config-high-diabetes.json high_diabetes 100 Victoria

# Pandemic impact
./run_synthea_with_ha

