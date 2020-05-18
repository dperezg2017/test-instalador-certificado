package com.tci.certificado;

import com.tci.certificado.core.InicioActualiacionCertificado;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class CertificadoApplication {

	public static void main(String[] args) {
		File test2 = new File("almacen-test.txt");
		InicioActualiacionCertificado inicioActualiacionCertificado = new InicioActualiacionCertificado();
//		SpringApplication.run(CertificadoApplication.class, args);
	}

}
