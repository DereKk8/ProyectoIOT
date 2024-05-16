import java.time.LocalTime;
import java.util.ArrayList;

public class ManejadorAcceso {
    private ArrayList<Empleado> empleados;

    public ManejadorAcceso(ArrayList<Empleado> empleados) {
        this.empleados = empleados;
    }

    public int validarAcceso(String id) throws EmpleadoExc{
        if (id == null || id.isEmpty()){
            throw new EmpleadoExc("El id del empleado no puede ser nulo o vacío");
        }else if(empleados.isEmpty()){
            throw new EmpleadoExc("No hay empleados registrados");
        }
        else if(empleados.stream().noneMatch(empleado -> empleado.getId().equals(id))){
            return 4; //Empleado no registrado
        }
        else{
            for (Empleado empleado : empleados) {
                if (empleado.getId().equals(id) && empleado.getHorarioAdmitido().getHoraInicio().isBefore(LocalTime.now()) && empleado.getHorarioAdmitido().getHoraFin().isAfter(LocalTime.now())){
                    return 1; //Acceso permitido
                }else if(empleado.getId().equals(id) && empleado.getHorarioAdmitido().getHoraFin().isBefore(LocalTime.now()) && empleado.getHorarioAdmitido().getHoraInicioTarde().isBefore(LocalTime.now()) && empleado.getHorarioAdmitido().getHoraFinTarde().isAfter(LocalTime.now())){
                    return 2; //Acceso permitido Tarde
                }else
                    return 3; //Acceso denegado
            }
        }
        return 0;
    }

}
