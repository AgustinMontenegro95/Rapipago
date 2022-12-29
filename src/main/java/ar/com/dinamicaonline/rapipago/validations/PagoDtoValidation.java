package ar.com.dinamicaonline.rapipago.validations;

import ar.com.dinamicaonline.rapipago.dto.PagoDto;

public class PagoDtoValidation {

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

}
