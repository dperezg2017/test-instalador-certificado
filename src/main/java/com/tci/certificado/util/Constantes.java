package com.tci.certificado.util;

public class Constantes {

    public static final String CMD_UBICACION_SERVICIO_UPDATE_POS_PC_ENGLISH = "sc qc \"SrvWinFE_POS_Modulo_Servidor\" 5000 | find \"BINARY_PATH_NAME\"";
    public static final String CMD_UBICACION_SERVICIO_UPDATE_POS_PC_SPANISH = "sc qc \"SrvWinFE_POS_Modulo_Servidor\" 5000 | find \"NOMBRE_RUTA_BINARIO\"";

    public static final String CMD_UPDATE_NOMBRE_RUTA_BINARIO = "NOMBRE_RUTA_BINARIO";
    public static final String CMD_UPDATE_BINARY_PATH_NAME = "BINARY_PATH_NAME";
    public static final String CMD_UPDATE_POS_EXE = "posserver.exe";
    public static final String CMD_NET_STOP_POS_UPDATE_EPOS = "net stop SrvWinFE_POS_Modulo_Servidor";
    public static final String CMD_NET_START_POS_UPDATE_EPOS = "net start SrvWinFE_POS_Modulo_Servidor";
    public static final String CMD_STATUS_SERVICIO_POS_UPDATE_EPOS = "sc query \"SrvWinFE_POS_Modulo_Servidor\"";

    public static final String SERVICIO_POS_RUNNING = "RUNNING";
    public static final String SERVICIO_POS_STOPPED = "STOPPED";
    public static final String SERVICIO_POS_INICIADO = "Iniciado";
    public static final String SERVICIO_POS_DETENIDO = "Detenido";
    public static final String SERVICIO_POS_NO_EXISTE = "No Existe";

    public static final String NO_EXISTE = "No existe ";

}
