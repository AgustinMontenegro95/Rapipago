package ar.com.dinamicaonline.rapipago.service;

public interface EntidadService {

    // Read operation
    Long fetchEntidadByDocumentId(String customerId);

    String ingresoDineroProc(Long qIdEntidad, Double qPago, String qOrigen);
}
