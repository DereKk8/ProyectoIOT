
import java.time.LocalTime;

public class Horario {

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalTime horaInicioTarde;
    private LocalTime horaFinTarde;

    public Horario(LocalTime horaInicio, LocalTime horaFin, LocalTime horaInicioTarde, LocalTime horaFinTarde) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horaInicioTarde = horaInicioTarde;
        this.horaFinTarde = horaFinTarde;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public LocalTime getHoraInicioTarde() {
        return horaInicioTarde;
    }

    public void setHoraInicioTarde(LocalTime horaInicioTarde) {
        this.horaInicioTarde = horaInicioTarde;
    }

    public LocalTime getHoraFinTarde() {
        return horaFinTarde;
    }

    public void setHoraFinTarde(LocalTime horaFinTarde) {
        this.horaFinTarde = horaFinTarde;
    }
}
