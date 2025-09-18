# Generating a Specific Number of Records in Synthea

Generating a specific number of records in CSV format with Synthea involves a couple of simple configuration steps.

You can achieve this either by modifying the `synthea.properties` file for persistent settings or by using command-line arguments for a single run.

## 1. Specifying the Number of Records

You can control the number of patient records Synthea generates.

### Using a Command-Line Argument (Recommended for one-time runs)

The easiest way is to use the `-p` flag when you run Synthea. For example, to generate 100 patient records:

```bash
./run_synthea -p 100
```

### Editing the Properties File (For a new default)

If you want to change the default number of patients for all subsequent runs, you can edit the `src/main/resources/synthea.properties` file and change the `generate.default_population` property.

## 2. Enabling CSV Export

By default, Synthea exports data in FHIR format. You need to explicitly enable CSV export.

### Using a Command-Line Argument

You can enable CSV export for a single run using a command-line override:

```bash
./run_synthea --exporter.csv.export=true
```

### Editing the Properties File

To make CSV export the default behavior, change the `exporter.csv.export` property in `src/main/resources/synthea.properties` to `true`.

## Putting It All Together

To generate 100 patient records in CSV format without permanently changing your configuration file, you can combine these flags in a single command:

```bash
./run_synthea -p 100 --exporter.csv.export=true
```

After the run is complete, you will find the generated CSV files in the `./output/csv/` directory, as specified by the `exporter.baseDirectory` property in your `synthea.properties` file.