package com.tci.certificado;

import com.tci.certificado.core.InicioActualiacionCertificado;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertificadoApplication {

	public static void main(String[] args) {
		InicioActualiacionCertificado inicioActualiacionCertificado = new InicioActualiacionCertificado();
//		SpringApplication.run(CertificadoApplication.class, args);
	}

}
