package com.tci.certificado.task;

import com.tci.certificado.util.Constantes;
import com.tci.certificado.util.Utilitario;
import org.apache.log4j.Logger;

public class DetenerServicioTask {

    Logger logger = Logger.getLogger(DetenerServicioTask.class);
    Utilitario utilitario = new Utilitario();
    Constantes constante = new Constantes();

    public int detenerServicio() {
        try {
            utilitario.ejecutarComandoCMD(constante.CMD_NET_STOP_POS_UPDATE_EPOS);
            String estado = utilitario.obtenerEstadoServicio();
            if (estado.contains(constante.SERVICIO_POS_DETENIDO)) {
                return 0;
            } else if (estado.contains(constante.SERVICIO_POS_INICIADO)) {
                return 1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            logger.error("Ocurrio un error al detener el servicio:", e);
            return -2;
        }
    }

}
