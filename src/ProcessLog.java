import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProcessLog {

    public LocalDate date;
    public LocalTime time  = LocalTime.now();
    String currentTime;

    public String action;
    public String result;
    public String barcode;



    public String toString()
    {
        return date + "-" + time + "-" + action + "-" + result + "-" + barcode;
    }
}
