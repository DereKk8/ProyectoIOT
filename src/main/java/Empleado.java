public class Empleado {
    private String id;
    private String nombre;
    private Horario horarioAdmitido;

    public Empleado(String id, String nombre, Horario horarioAdmitido) {
        this.id = id;
        this.nombre = nombre;
        this.horarioAdmitido = horarioAdmitido;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Horario getHorarioAdmitido() {
        return horarioAdmitido;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String toString(){
        return "ID: " + id + " Nombre: " + nombre + " Horario: " + horarioAdmitido.getHoraInicio() + "-" + horarioAdmitido.getHoraFin() + " " + horarioAdmitido.getHoraInicioTarde() + "-" + horarioAdmitido.getHoraFinTarde() + "\n";
    }


}
