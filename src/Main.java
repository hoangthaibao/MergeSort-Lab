
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        int[] original = {9, 3, 7, 1, 8, 2, 6, 4, 5};

        int[] cutoffs = {0, 4, 7, 10, 16};

        System.out.println("=== MERGESORT LAB ===");

        for (int cutoff : cutoffs) {

            int[] a = original.clone();

            MergeSort.sort(a, cutoff);

            System.out.println(
                    "CUTOFF = " + cutoff
                            + " -> " + Arrays.toString(a));
        }
    }
}
