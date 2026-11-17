
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class Experiment {

    static final int[] SIZES = {
            10_000, 100_000, 500_000
    };

    static final int[] CUTOFFS = {
            0, 2, 4, 7, 10, 12,
            16, 24, 32, 48, 64
    };

    static final int WARMUP = 5;
    static final int REPEATS = 20;
    static final long SEED = 2026L;

    public static void main(String[] args)
            throws IOException {

        Path output = Path.of(
                "results", "experiment_results.csv");

        Files.createDirectories(output.getParent());

        StringBuilder csv = new StringBuilder();

        csv.append(
                "type,size,cutoff,avg_ms,median_ms,stddev_ms\n");

        System.out.println("=== MERGESORT LAB ===");
        System.out.println("Java: "
                + System.getProperty("java.version"));
        System.out.println("Starting experiments...\n");

        for (DataGenerator.DataType type
                : DataGenerator.DataType.values()) {

            for (int size : SIZES) {

                System.out.printf(
                        "Testing %s | N = %,d%n",
                        type, size);

                runGroup(type, size, csv);
            }
        }

        Files.writeString(output, csv.toString());

        System.out.println("\n=== COMPLETED ===");
        System.out.println(
                "Results saved to: "
                        + output.toAbsolutePath());
    }

    private static void runGroup(
            DataGenerator.DataType type,
            int size,
            StringBuilder csv) {

        int[] original = DataGenerator.generate(
                size, type, SEED);

        int[] expected = original.clone();
        Arrays.sort(expected);

        double[][] timings =
                new double[CUTOFFS.length][REPEATS];

        // Warm-up tat ca CUTOFF
        for (int w = 0; w < WARMUP; w++) {
            for (int cutoff : CUTOFFS) {
                int[] a = original.clone();
                MergeSort.sort(a, cutoff);
                verify(a, expected);
            }
        }

        Random random = new Random(
                SEED + size + type.ordinal());

        // Do theo tung vong, dao thu tu CUTOFF
        for (int r = 0; r < REPEATS; r++) {

            int[] order = new int[CUTOFFS.length];

            for (int i = 0; i < order.length; i++) {
                order[i] = i;
            }

            // Fisher-Yates shuffle
            for (int i = order.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);

                int temp = order[i];
                order[i] = order[j];
                order[j] = temp;
            }

            for (int index : order) {

                int[] a = original.clone();

                long start = System.nanoTime();
                MergeSort.sort(a, CUTOFFS[index]);
                long end = System.nanoTime();

                verify(a, expected);

                timings[index][r] =
                        (end - start) / 1_000_000.0;
            }
        }

        double bestMedian = Double.MAX_VALUE;
        int bestCutoff = -1;

        for (int i = 0; i < CUTOFFS.length; i++) {

            double[] times = timings[i].clone();

            double avg = mean(times);
            double median = median(times);
            double stddev = stddev(times, avg);

            csv.append(String.format(
                    Locale.US,
                    "%s,%d,%d,%.6f,%.6f,%.6f%n",
                    type, size, CUTOFFS[i],
                    avg, median, stddev));

            if (median < bestMedian) {
                bestMedian = median;
                bestCutoff = CUTOFFS[i];
            }
        }

        System.out.printf(
                "  Best CUTOFF = %d | Median = %.3f ms%n",
                bestCutoff, bestMedian);
    }

    private static void verify(
            int[] actual, int[] expected) {

        if (!Arrays.equals(actual, expected)) {
            throw new IllegalStateException(
                    "Sorting result is incorrect!");
        }
    }

    private static double mean(double[] a) {

        double sum = 0;

        for (double x : a) {
            sum += x;
        }

        return sum / a.length;
    }

    private static double median(double[] a) {

        Arrays.sort(a);

        int n = a.length;

        if (n % 2 == 1) {
            return a[n / 2];
        }

        return (a[n / 2 - 1] + a[n / 2]) / 2.0;
    }

    private static double stddev(
            double[] a, double mean) {

        double sum = 0;

        for (double x : a) {
            double difference = x - mean;
            sum += difference * difference;
        }

        return Math.sqrt(sum / (a.length - 1));
    }
}
