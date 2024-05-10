package app.pooi.common.util;

import lombok.NonNull;

public interface IEnum<T> {

    T getValue();


    static <V, E extends Enum<E> & IEnum<V>> E fromValue(@NonNull Class<E> enumClazz, @NonNull V value) {
        for (E e : enumClazz.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
