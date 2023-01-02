package ar.com.dinamicaonline.rapipago.service;

import org.springframework.http.ResponseEntity;

import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.dto.ReversaDto;

public interface ReceiveAndSendService {

    // Save operation
    ResponseEntity<?> saveReceiveAndSend(ConsultaDto consultaDto);

    // Save operation
    ResponseEntity<?> saveReceiveAndSend(PagoDto pagoDto);

    // Save operation
    ResponseEntity<?> saveReceiveAndSend(ReversaDto reversaDto);

}
