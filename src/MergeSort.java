
public class MergeSort {

    public static void sort(int[] a, int cutoff) {

        if (cutoff < 0) {
            throw new IllegalArgumentException(
                    "Cutoff must be non-negative");
        }

        int[] aux = new int[a.length];
        sort(a, aux, 0, a.length - 1, cutoff);
    }

    private static void sort(
            int[] a, int[] aux,
            int lo, int hi, int cutoff) {

        // Truong hop mang rong hoac 1 phan tu
        if (hi <= lo) return;

        // Cai tien CUTOFF trong slide 18
        if (hi - lo + 1 <= cutoff) {
            InsertionSort.sort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;

        // Sap xep hai nua
        sort(a, aux, lo, mid, cutoff);
        sort(a, aux, mid + 1, hi, cutoff);

        // Tron hai nua da sap xep
        merge(a, aux, lo, mid, hi);
    }

    private static void merge(
            int[] a, int[] aux,
            int lo, int mid, int hi) {

        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;

        for (int k = lo; k <= hi; k++) {

            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (aux[j] < aux[i]) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }
}
