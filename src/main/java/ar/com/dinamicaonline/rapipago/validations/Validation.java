package ar.com.dinamicaonline.rapipago.validations;

import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;

public class Validation {

    public static boolean validationAvisoDto(PagoDto pagoDto) {
        if (pagoDto.getId_numero() != null
                && pagoDto.getCod_trx() != null
                && pagoDto.getCanal() != null
                && pagoDto.getImporte() != null
                && pagoDto.getBarra() != null
                && pagoDto.getFecha_hora_operacion() != null) {
            return true;
        }
        return false;
    }

    public static boolean validationConsultaDto(ConsultaDto consultaDto) {
        if (consultaDto.getId_clave() != null
                && consultaDto.getCod_trx() != null
                && consultaDto.getCanal() != null
                && consultaDto.getFecha_hora_operacion() != null) {
            return true;
        }
        return false;
    }

}
