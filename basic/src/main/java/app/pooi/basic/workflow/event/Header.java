package app.pooi.basic.workflow.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Header {

    private @NonNull String eventId;

    private @NonNull String eventType;
    /**
     * timestamp
     */
    private long ts;

    public Header(@NonNull String eventId, @NonNull String eventType) {
        this.ts = System.currentTimeMillis();
        this.eventId = eventId;
        this.eventType = eventType;
    }
}
