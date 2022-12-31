package ar.com.dinamicaonline.rapipago.service;

import ar.com.dinamicaonline.rapipago.dto.ClienteDto;

public interface EntidadService {

    // Read operation
    Long fetchEntidadByDocumentId(String customerId);

    String ingresoDineroProc(Long qIdEntidad, Double qPago, String qOrigen);

    Double importePagarFunc(Long qIdEntidad);

    ClienteDto obtenerNombreApellido(String documento);

}
