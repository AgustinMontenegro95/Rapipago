package ar.com.dinamicaonline.rapipago.service;

import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;

public interface ReceiveAndSendService {

    // Save operation
    boolean saveReceiveAndSend(ReceiveAndSend recieveAndSend, PagoDto pagoDto);
}
