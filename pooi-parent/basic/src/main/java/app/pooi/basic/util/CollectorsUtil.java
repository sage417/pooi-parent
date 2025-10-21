package app.pooi.basic.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.BinaryOperator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectorsUtil {

    public static <T> BinaryOperator<T> useFirst() {
        return (first, second) -> first;
    }

    public static <T> BinaryOperator<T> useLast() {
        return (first, second) -> second;
    }


}
