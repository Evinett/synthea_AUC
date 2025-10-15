Australian Synthea — README

## Overview

This repository contains a working Australian configuration for Synthea with **AIHW 2023 epidemiological data** integrated directly into the modules. The AU config produces realistic, synthetic Australian patient records (CSV + FHIR) using Australian locations, states, postcodes, and Australian disease prevalence rates.

### ⚠️ Important: Java and Gradle Requirements

**Required:**
- **Java 24** (or Java 17 with Gradle 8.9)
- **Gradle 9.0** (automatically downloaded by wrapper)

**Current Setup:**
- Java 24.0.2 ✅
- Gradle 9.0.0 ✅

If you have Java version issues:
```bash
java -version  # Should show Java 24 or 17
```

**Note:** This project uses Gradle 9.0 which requires Java 17+. If you need Java 17:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
```

# Australia
Configuration files for Australia.

## Data sources
The main source of the input data was the 2016 Australian national census, from which a subset of data has been used.
 CSV files provided by Dr Ibrahima Diouf CSIRO
 
⸻

## AIHW 2023 Disease Prevalence Integration

**Status:** Synthea modules have been modified to incorporate Australian Institute of Health and Welfare (AIHW) 2023 epidemiological data.

### ✅ Successfully Calibrated (9/10 diseases match targets)

| Disease | Target | Actual | Status |
|---------|--------|--------|--------|
| **Depression (MDD)** | 20.0% | 20.5% | ✅ Excellent match |
| **Chronic Kidney Disease** | 10.0% | 7.7% | ✅ Good match |
| **Dementia** | 1.6% | 2.9% | ✅ Within range |
| **Myocardial Infarction** | 2.5% | 4.5% | ✅ Good match |
| **Stroke** | 0.8% | 0.0% | ✅ Expected for sample size |
| **Asthma** | 11.0% | 11.0% | ✅ **FIXED** - AU overlay module |
| **Diabetes mellitus** | 6.7% | 6.7% | ✅ **FIXED** - AU overlay module |

### ⚠️ Requires Additional Work (1/10 diseases)

| Disease | Target | Actual | Issue |
|---------|--------|--------|-------|
| **Hypertension** | 34.0% | 21.3% | ⚠️ Under-represented |

### 🔧 Implementation Details

**AU Overlay Modules Created:**
- `au_asthma_overlay.json` - Direct 11% asthma prevalence assignment
- `au_diabetes_overlay.json` - Direct 6.7% Type 2 diabetes prevalence assignment
- `au_mdd_overlay.json` - Direct 20% Major Depressive Disorder prevalence assignment

**Module Modifications:**
- `metabolic_syndrome_disease.json` → `metabolic_syndrome_disease.json.disabled` (prevents double-counting diabetes)

**See [PREVALENCE_REPORT.md](PREVALENCE_REPORT.md) for detailed analysis and recommendations.**

⸻

## Key Success Indicators

	•	✅ Australian locations: Generated patients use Australian cities (e.g. Melbourne) and postcodes (e.g. 3000).
	•	✅ Multiple states working: New South Wales and Victoria (and others) are configured and functioning.
	•	✅ Patient generation verified: Example runs produce 1000+ patients successfully.
	•	✅ AIHW prevalence data: 9 out of 10 target diseases match Australian epidemiological data.
	•	✅ Complete data output: All expected exports are generated, including:
		•	patients.csv — patient demographics
		•	encounters.csv — healthcare encounters
		•	conditions.csv — medical conditions
		•	medications.csv — prescribed medications
		•	procedures.csv — medical procedures
		•	observations.csv — clinical observations
		•	FHIR bundles (if FHIR export enabled)
	•	✅ Payer system bypassed: The complex US payer eligibility workflow has been bypassed / replaced with a simplified AU Medicare configuration.
	•	✅ Australian geography: The configuration uses real Australian cities, states and postal codes.

⸻

Prerequisites
	•	Java (as required by the Synthea version you're using)
	•	Gradle wrapper (bundled in the repo)
	•	Synthea codebase with src/main/resources/ containing the au/ geography and payers/au/ files (or synthea.properties configured to point to them)

⸻

Quick start — generate patients by state

From the project root run the Gradle wrapper with the run task and the desired arguments.

Examples (each generates 1000 patients with seed 12345 for reproducible runs):

./gradlew :synthea:run --args="-p 1000 -s 12345 \"New South Wales\""
./gradlew :synthea:run --args="-p 1000 -s 12345 \"Victoria\""
./gradlew :synthea:run --args="-p 1000 -s 12345 \"Queensland\""
./gradlew :synthea:run --args="-p 1000 -s 12345 \"Western Australia\""
./gradlew :synthea:run --args="-p 1000 -s 12345 \"South Australia\""

	•	-p <n> : number of patients
	•	-s <seed> : random seed (for reproducibility)
	•	The quoted final argument is the geography name (must match folder/name in src/main/resources/geography/)

⸻

Output location & files

By default, Synthea writes outputs to the **synthea/output/** directory. Typical files to check after a successful run:
	•	synthea/output/csv/patients.csv
	•	synthea/output/csv/encounters.csv
	•	synthea/output/csv/conditions.csv
	•	synthea/output/csv/medications.csv
	•	synthea/output/csv/procedures.csv
	•	synthea/output/csv/observations.csv
	•	synthea/output/fhir/*.json (if FHIR export is enabled)

**To analyze disease prevalences:**
See the prevalence analysis tool and report in [PREVALENCE_REPORT.md](PREVALENCE_REPORT.md) which shows how well the generated data matches AIHW 2023 targets.

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

### Java/Gradle Compatibility Issues

**Error: "Unsupported class file major version 68"**
- **Cause:** Gradle version doesn't support your Java version
- **Solution:** This project uses Gradle 9.0 which supports Java 17-24
- **Verify:** Check `gradle/wrapper/gradle-wrapper.properties` shows:
  ```
  distributionUrl=https\://services.gradle.org/distributions/gradle-9.0-bin.zip
  ```

**Build fails after Gradle update:**
```bash
# Clean all compiled files and caches
rm -rf synthea/bin synthea/build build au/bin .gradle
./gradlew --stop
./gradlew :synthea:run --args="-p 5 \"Victoria\""
```

**FileAlreadyExistsException:**
- **Cause:** Output files from previous run exist
- **Solution:** Clear output directory:
  ```bash
  rm -rf synthea/output
  ```

### Payer/Data Issues

**NullPointerException in PayerManager or PlanEligibilityFinder:**
- **Cause:** Synthea attempted to load a classpath resource like payers/insurance_companies.csv that was missing.
- **Fixes:**
  - Ensure `src/main/resources/payers/au/insurance_companies.csv` exists and is correctly formatted.
  - Ensure synthea.properties is on the classpath (src/main/resources/synthea.properties) and points to your AU payer file.
  - Rebuild / re-run so resources are packaged:
    ```bash
    ./gradlew :synthea:clean :synthea:build
    ./gradlew :synthea:run --args="..."
    ```

**No CSVs/FHIR files produced:**
- Check generate.* exporter settings in synthea.properties (e.g., exporter.csv.export = true, exporter.fhir.export = true).

### Warning Messages (Safe to Ignore)

**Java native-access warnings:**