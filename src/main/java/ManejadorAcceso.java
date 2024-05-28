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
            Empleado empleado = empleados.stream().filter(e -> e.getId().equals(id)).findFirst().get();
            LocalTime horaActual = LocalTime.now();
            if(horaActual.isAfter(empleado.getHorarioAdmitido().getHoraInicio()) && horaActual.isBefore(empleado.getHorarioAdmitido().getHoraFin())){
                return 1; //Acceso permitido
            }else if(horaActual.isAfter(empleado.getHorarioAdmitido().getHoraInicioTarde()) && horaActual.isBefore(empleado.getHorarioAdmitido().getHoraFinTarde())){
                return 2; //Acceso permitido tarde
            }else{
                return 3; //Acceso denegado
            }
        }
    }

}
