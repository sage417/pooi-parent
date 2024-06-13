package app.pooi.model.workflow.event;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Header {

    private @NonNull String eventId;

    private @NonNull String eventType;
    /**
     * timestamp
     */
    private long ts;

    public Header() {
        this.ts = System.currentTimeMillis();
    }
}
