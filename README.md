
# MergeSort Lab
### Finding the Optimal Cutoff for Merge Sort

A Java experimental project investigating the optimal
CUTOFF threshold for hybrid Merge Sort and Insertion Sort.

## 1. Project Overview

Merge Sort has O(N log N) time complexity, but recursive
overhead can be inefficient for small subarrays.

This project investigates whether switching to Insertion
Sort at a suitable CUTOFF improves execution time.

## 2. Objectives

- Implement Merge Sort based on course lectures.
- Integrate Insertion Sort using a configurable CUTOFF.
- Experiment with different CUTOFF values.
- Compare execution time across multiple datasets.
- Analyze and visualize the results.

## 3. Experimental Setup

Language: Java
JDK: Oracle OpenJDK 25.0.2
IDE: IntelliJ IDEA

Array sizes:
- 10,000
- 100,000
- 500,000

Dataset types:
- Random
- Sorted
- Reversed
- Nearly Sorted
- Many Duplicates

CUTOFF values:
0, 2, 4, 7, 10, 12, 16, 24, 32, 48, 64

Measurements:
- 5 warm-up rounds
- 20 measured rounds per configuration
- Mean, median and standard deviation

## 4. Project Structure

src/
- Main.java
- MergeSort.java
- InsertionSort.java
- DataGenerator.java
- Experiment.java
- ChartGenerator.java

results/
- experiment_results.csv
- dashboard.html

report/
- Experimental report (PDF)

## 5. How to Run

1. Open the project in IntelliJ IDEA.
2. Configure JDK 25 or another compatible JDK.
3. Run Main.java to verify the sorting algorithm.
4. Run Experiment.java to generate CSV results.
5. Run ChartGenerator.java to generate the dashboard.
6. Open results/dashboard.html in a browser.

The dashboard requires Internet access to load Chart.js.

## 6. Key Findings

For a random array of 100,000 elements, one experimental
run found CUTOFF = 48 to have the lowest median runtime
among the tested values.

The observed median was approximately 7.120 ms, giving
a 27.45% time reduction compared with pure Merge Sort.

The best CUTOFF may vary by dataset, machine and
measurement conditions.

## 7. References

Robert Sedgewick and Kevin Wayne,
Algorithms, Fourth Edition, Section 2.2: Mergesort.

Course lecture: 22Mergesort.pdf.

## 8. Author

Hoang Thai Bao
University of Engineering and Technology - VNU
## Live Dashboard

Explore the interactive experiment results:

https://hoangthaibao.github.io/MergeSort-Lab/