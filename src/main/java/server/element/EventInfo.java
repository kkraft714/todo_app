package server.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event date or deadline with completion status.
 */
// ToDo: Support Date or DateTime?
public class EventInfo extends NoteElement<EventInfo> {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime eventDate;
    private CompletionStatus status;

    public enum CompletionStatus { COMPLETED, UNFINISHED, CANCELED, MISSED }

    // No-arg constructor required by Hibernate
    protected EventInfo() {
        super(null, null);
        this.eventDate = null;
    }

    public EventInfo(String title, String description, LocalDateTime date) {
        super(title, description);
        this.eventDate = date;
        this.status = CompletionStatus.UNFINISHED;
    }

    public EventInfo(String title, String description, String dateString) {
        this(title, description, LocalDateTime.parse(dateString, dateFormat));
    }

    public LocalDateTime getDate() { return eventDate; }
    public void setDate(LocalDateTime newDate) { this.eventDate = newDate; }

    public CompletionStatus getStatus() { return status; }
    public void setStatus(CompletionStatus newStatus) { this.status = newStatus; }

    @Override
    public String toString() {
        return "EventInfo:\n" + super.toString() + "\n" + dateFormat.format(eventDate) + "\n" + status;
    }
}
