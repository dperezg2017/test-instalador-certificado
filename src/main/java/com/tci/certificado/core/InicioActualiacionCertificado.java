package com.tci.certificado.core;

import com.tci.certificado.task.DetenerServicioTask;
import com.tci.certificado.task.IniciarServicioTask;
import com.tci.certificado.util.Constantes;
import com.tci.certificado.util.Utilitario;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class InicioActualiacionCertificado implements Runnable {

    @Autowired
    private ResourceLoader resourceLoader;
    Utilitario utilitario = new Utilitario();
    private static final Logger logger = LoggerFactory.getLogger(InicioActualiacionCertificado.class);

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
//            File origen = ResourceUtils.getFile("classpath:almacen.jks");
            File destino = new File(certificadoAntiguo.getAbsolutePath());
            try {
                obtenerInformacionCertificado();
                InputStream in = this.getClass().getResourceAsStream("/almacen.jks");
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

    public void obtenerInformacionCertificado(){

        try {

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(this.getClass().getResourceAsStream("/almacen.jks"), "redhat".toCharArray());
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate("firma_koketa");
            DateTime expireCertDate = new DateTime(certificate.getNotAfter());
            DateTime now = DateTime.now();
            int difference = Days.daysBetween(expireCertDate, now).getDays();

            if(difference>0){
                logger.info("CERTIFICADO DIGITAL NUEVO   => Fecha de Caducidad: {} tiene {} dias vencidos.",expireCertDate.toString("dd-MM-yyyy"),difference);
            }else {
                logger.info("CERTIFICADO DIGITAL NUEVO   => Fecha de Caducidad: {} le quedan {} dias.", expireCertDate.toString("dd-MM-yyyy"), difference);
            }
        }catch (Exception e){
            logger.error("Ocurrio un error al obtener informacion del certificado a actualizar");
        }
    }
    public File obtenerCertificadoAntiguo() {

        try {
            String rutaInstalado = utilitario.obtenerRutaServicioInstaladoRaiz();
            File certificadoAntiguo = null;
            if(!rutaInstalado.equalsIgnoreCase(Constantes.NO_EXISTE)) {
                String rutaCertificado = rutaInstalado.concat("\\lib\\config\\firma");
                File file = new File(rutaCertificado);
                boolean inicio = true;
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
            }else{
                logger.warn("No se pudo encontrar la ruta del servicio instalado");
            }
            logger.info("CERTIFICADO DIGITAL ANTIGUO => Fecha de Modificacion: {} - Ubicacion: '{}' ", convertTime(certificadoAntiguo.lastModified()),certificadoAntiguo.getAbsolutePath());
            return certificadoAntiguo;
        } catch (Exception e) {
            logger.error("Ocurrio un error al obtener el certificado antiguo");
            return null;
        }
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void run() {
    }


}
