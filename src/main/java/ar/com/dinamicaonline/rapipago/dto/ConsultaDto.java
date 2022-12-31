package ar.com.dinamicaonline.rapipago.dto;

public class ConsultaDto {
    private String id_clave;
    private String cod_trx;
    private String canal;
    private String fecha_hora_operacion;

    @Override
    public String toString() {
        return "{\"id_clave\":\"" + id_clave
                + "\", \"cod_trx\":\"" + cod_trx
                + "\", \"canal\":\"" + canal
                + "\", \"fecha_hora_operacion\":\"" + fecha_hora_operacion + "\"}";
    }

    public String getId_clave() {
        return id_clave;
    }

    public void setId_clave(String id_clave) {
        this.id_clave = id_clave;
    }

    public String getCod_trx() {
        return cod_trx;
    }

    public void setCod_trx(String cod_trx) {
        this.cod_trx = cod_trx;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getFecha_hora_operacion() {
        return fecha_hora_operacion;
    }

    public void setFecha_hora_operacion(String fecha_hora_operacion) {
        this.fecha_hora_operacion = fecha_hora_operacion;
    }

}
