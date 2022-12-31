package ar.com.dinamicaonline.rapipago.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.dinamicaonline.rapipago.dto.ClienteDto;
import ar.com.dinamicaonline.rapipago.repositories.EntidadRepository;

@Service
public class EntidadServiceImpl implements EntidadService {

    @Autowired
    private EntidadRepository entidadRepository;

    @Override
    public Long fetchEntidadByDocumentId(String customerId) {
        return entidadRepository.findByDocumentId(customerId);
    }

    @Override
    public String ingresoDineroProc(Long qIdEntidad, Double qPago, String qOrigen) {
        return entidadRepository.ingresoDinero(qIdEntidad, qPago, qOrigen);
    }

    @Override
    public Double importePagarFunc(Long qIdEntidad) {
        return entidadRepository.dameImporteAPagar(qIdEntidad);
    }

    @Override
    public ClienteDto obtenerNombreApellido(String documento) {
        return entidadRepository.obtenerNombreApellido(documento);
    }

}