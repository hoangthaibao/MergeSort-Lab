
import java.util.Random;

public class DataGenerator {

    public enum DataType {
        RANDOM,
        SORTED,
        REVERSED,
        NEARLY_SORTED,
        MANY_DUPLICATES
    }

    public static int[] generate(
            int size, DataType type, long seed) {

        if (size < 0) {
            throw new IllegalArgumentException(
                    "Size must be non-negative");
        }

        Random random = new Random(seed);
        int[] a = new int[size];

        switch (type) {

            case RANDOM:
                for (int i = 0; i < size; i++) {
                    a[i] = random.nextInt(1_000_000);
                }
                break;

            case SORTED:
                for (int i = 0; i < size; i++) {
                    a[i] = i;
                }
                break;

            case REVERSED:
                for (int i = 0; i < size; i++) {
                    a[i] = size - i;
                }
                break;

            case NEARLY_SORTED:
                for (int i = 0; i < size; i++) {
                    a[i] = i;
                }

                // Hoan doi khoang 1% so phan tu
                for (int j = 0; j < size / 100; j++) {
                    int x = random.nextInt(size);
                    int y = random.nextInt(size);

                    int temp = a[x];
                    a[x] = a[y];
                    a[y] = temp;
                }
                break;

            case MANY_DUPLICATES:
                for (int i = 0; i < size; i++) {
                    a[i] = random.nextInt(10);
                }
                break;
        }

        return a;
    }
}
