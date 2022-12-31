package ar.com.dinamicaonline.rapipago.service;

import org.springframework.http.ResponseEntity;

import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;

public interface ReceiveAndSendService {

    // Save operation
    boolean saveReceiveAndSend(ReceiveAndSend receiveAndSend, PagoDto pagoDto);

    // Save operation
    ResponseEntity<?> saveReceiveAndSend(ConsultaDto consultaDto);

    // Save operation
    ResponseEntity<?> saveReceiveAndSend(PagoDto pagoDto);
}
