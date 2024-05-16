public class EmpleadoExc extends Exception {

    private String detalle;

    public EmpleadoExc(String detalle) {
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
