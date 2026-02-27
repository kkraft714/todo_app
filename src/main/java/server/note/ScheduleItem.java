package server.note;

import server.element.EventInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ToDo: Support recurring events?
public class ScheduleItem extends Note {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime date;
    private EventInfo.CompletionStatus status;

    public enum CompletionStatus { COMPLETED, UNFINISHED, CANCELED, MISSED }

    // ToDo: Support builder-type instantiation
    public ScheduleItem(String name, String description, LocalDateTime date) {
        super(name, description);
        this.date = date;
        this.status = EventInfo.CompletionStatus.UNFINISHED;
    }

    public ScheduleItem(String name, String description, String dateString) {
        this(name, description, LocalDateTime.parse(dateString, dateFormat));
    }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime newDate) { this.date = newDate; }

    public EventInfo.CompletionStatus getStatus() { return status; }
    public void setStatus(EventInfo.CompletionStatus newStatus) { this.status = newStatus; }

    @Override
    public String toString() {
        return "Scheduled Item:\n" + super.toString() + "\n" + dateFormat.format(date) + "\n" + status;
    }
}
