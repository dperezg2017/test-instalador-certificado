package com.tci.certificado.util;

import org.apache.log4j.Logger;
import java.io.*;

public class Utilitario {

    Logger logger = Logger.getLogger(Utilitario.class);
    Constantes constante = new Constantes();

    public Utilitario() {
    }

    public String identificarRutaServicioInstalado(String rptaUbicacionesCMD) {
        rptaUbicacionesCMD = rptaUbicacionesCMD.replace("\"", "");
        if (rptaUbicacionesCMD.contains(Constantes.CMD_UPDATE_NOMBRE_RUTA_BINARIO)) {
            int indexInicio = rptaUbicacionesCMD.indexOf(Constantes.CMD_UPDATE_NOMBRE_RUTA_BINARIO) + Constantes.CMD_UPDATE_NOMBRE_RUTA_BINARIO.length() + 2;
            int indexFin = rptaUbicacionesCMD.indexOf(Constantes.CMD_UPDATE_POS_EXE) + Constantes.CMD_UPDATE_POS_EXE.length();
            return rptaUbicacionesCMD.substring(indexInicio, indexFin);
        } else {
            int indexInicio = rptaUbicacionesCMD.indexOf(Constantes.CMD_UPDATE_BINARY_PATH_NAME) + Constantes.CMD_UPDATE_BINARY_PATH_NAME.length() + 2;
            int indexFin = rptaUbicacionesCMD.indexOf(Constantes.CMD_UPDATE_POS_EXE) + Constantes.CMD_UPDATE_POS_EXE.length();
            return rptaUbicacionesCMD.substring(indexInicio, indexFin);
        }
    }

    public String obtenerEstadoServicio() throws IOException {
        String detalle = null;
        String estadoServicio = ejecutarComandoCMD(constante.CMD_STATUS_SERVICIO_POS_UPDATE_EPOS);
        if (estadoServicio != null) {
            if (estadoServicio.contains(constante.SERVICIO_POS_RUNNING)) {
                detalle = constante.SERVICIO_POS_INICIADO;
            } else if (estadoServicio.contains(constante.SERVICIO_POS_STOPPED)) {
                detalle = constante.SERVICIO_POS_DETENIDO;
            } else {
                detalle = constante.SERVICIO_POS_NO_EXISTE;
            }
        }
        return detalle;
    }

    public String obtenerRutaServicioInstaladoBin() throws IOException {
        String detalleServicioIntalado = ejecutarComandoCMD(constante.CMD_UBICACION_SERVICIO_UPDATE_POS_PC_SPANISH);
        if (detalleServicioIntalado != null) {
            return identificarRutaServicioInstalado(detalleServicioIntalado);
        } else if (ejecutarComandoCMD(constante.CMD_UBICACION_SERVICIO_UPDATE_POS_PC_ENGLISH) != null) {
            return identificarRutaServicioInstalado(ejecutarComandoCMD(constante.CMD_UBICACION_SERVICIO_UPDATE_POS_PC_ENGLISH));
        } else {
            logger.error("Ocurrio un error al obtener la ruta del servicio instalado: no se encuentra el servicio");
            return null;
        }
    }


    public String obtenerRutaServicioInstaladoRaiz() throws IOException {
        String rutaServicioInstalado = obtenerRutaServicioInstaladoBin();
        return (rutaServicioInstalado != null) ? rutaServicioInstalado.substring(0, rutaServicioInstalado.indexOf("\\bin")) : constante.NO_EXISTE;
    }

    public String ejecutarComandoCMD(String command) throws IOException {
        Process process = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuilder respuesta = new StringBuilder();
            logger.info("Se va a ejecutar el comando CMD :" + command);
            process = Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                respuesta.append(line);
            }
            if (!respuesta.toString().isEmpty()) {
                logger.info("Respuesta del comando CMD : " + respuesta.toString());
                process.destroy();
                bufferedReader.close();
                return respuesta.toString();
            } else {
                logger.warn("Respuesta del comando CMD : " + respuesta.toString());
                process.destroy();
                bufferedReader.close();
                return null;
            }
        } catch (Exception e) {
            logger.error("Ocurrio un error al ejecutar el comando CMD :", e);
            process.destroy();
            bufferedReader.close();
            return null;
        }
    }

}
