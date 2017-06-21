# average-coverage

## Description

A simple program run from the REPL for calculating average coverage of a gene from a list of genes and species

## Usage

From the command line use:

```java -jar target/uberjar/average-coverage-0.1.0-SNAPSHOT-standalone.jar "input_file.csv" "output_file.csv"```

Expects a csv in this format (no headers):

|gene_name |species_name |length_of_fragment |percentage_coverage_of_fragment |
| --- | --- | --- | --- |
|gene_A |Homo_sapiens |1218 | 92.34 |
|gene_A |Pan_troglodytes |426 |32.19 |
|gene_B |Homo_sapiens |1071 |100 |
|gene_B |Pan_troglodytes |762 |71.14 |
|gene_B |Rhinolophus_acuminatus|204 |19.04 |

Outputs to csv as a list of gene names with associated average percentage coverage.

## License

Copyright © 2016 Seb Bailey
