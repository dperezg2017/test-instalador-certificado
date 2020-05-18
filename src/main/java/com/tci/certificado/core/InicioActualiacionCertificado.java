package com.tci.certificado.core;

import com.tci.certificado.task.DetenerServicioTask;
import com.tci.certificado.task.IniciarServicioTask;
import com.tci.certificado.util.Utilitario;
import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InicioActualiacionCertificado implements Runnable {

    Utilitario utilitario = new Utilitario();
    private static Logger logger = Logger.getLogger(InicioActualiacionCertificado.class);

    public InicioActualiacionCertificado() {

        detenerServidorEpos();
        File certificadoAntiguo = obtenerCertificadoAntiguo();
        if (certificadoAntiguo != null) {
            actualizarCertificado(certificadoAntiguo);
            logger.info("Se actualizo el certificado satisfactoriamente.");
        } else {
            logger.error("Ocurrio un error al obtener el certificado antiguo");
        }
        iniciarServidorEpos();
    }

    public void detenerServidorEpos() {
        DetenerServicioTask detenerServicioTask = new DetenerServicioTask();
        switch (detenerServicioTask.detenerServicio()) {
            case 0:
                logger.info("El servicio se detuvo con exito");
                break;
            case 1:
                logger.error("No se detuvo el servicio");
                break;
            case -1:
                logger.error("No se detuvo el servicio, ya que el servicio no existe");
                break;
            case -2:
                logger.error("Ocurrio un error al detener el servicio");
                break;
        }
    }

    public void iniciarServidorEpos() {
        IniciarServicioTask iniciarServicioTask = new IniciarServicioTask();
        switch (iniciarServicioTask.iniciarServicio()) {
            case 0:
                logger.error("No se inicio el servicio, se encuentra detenido");
                break;
            case 1:
                logger.info("El servicio se inicio con exito");
                break;
            case -1:
                logger.error("No se inicio el servicio, ya que el servicio no existe ó esta procesando");
                break;
            case -2:
                logger.error("Ocurrio un error al detener el servicio");
                break;
        }
    }

    public void actualizarCertificado(File certificadoAntiguo) {
        try {
            File origen = ResourceUtils.getFile("classpath:almacen.jks");
            File destino = new File(certificadoAntiguo.getAbsolutePath());
            try {
                InputStream in = new FileInputStream(origen);
                OutputStream out = new FileOutputStream(destino);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException ioe) {
                logger.error("Ocurrio un error en el proceso de actualizar el certificado: ",ioe);
            }

        } catch (Exception e) {
            logger.error("Ocurrio un error al actualizar el certificado: ",e);
        }
    }

    public File obtenerCertificadoAntiguo() {

        try {
            String rutaInstalado = utilitario.obtenerRutaServicioInstaladoRaiz();
            String rutaCertificado = rutaInstalado.concat("\\lib\\config\\firma");
            File file = new File(rutaCertificado);
            boolean inicio = true;
            File certificadoAntiguo = null;
            if (file.listFiles().length > 0) {
                for (File unidad : file.listFiles()) {
                    if (inicio) {
                        certificadoAntiguo = unidad;
                        inicio = false;
                    } else if (certificadoAntiguo.lastModified() < unidad.lastModified()) {
                        certificadoAntiguo = unidad;
                    }
                }
            } else {
                logger.warn("No se encontraron certificados para actualizar");
            }
            logger.info("Certificado antiguo:" + certificadoAntiguo.getName() + " - fecha:" + convertTime(certificadoAntiguo.lastModified()));
            return certificadoAntiguo;
        } catch (Exception e) {
            logger.error("Ocurrio un error al obtener el certificado antiguo");
            return null;
        }
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void run() {
    }


}
