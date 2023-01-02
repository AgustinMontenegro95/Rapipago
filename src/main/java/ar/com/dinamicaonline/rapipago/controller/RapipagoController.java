package ar.com.dinamicaonline.rapipago.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.dto.ReversaDto;
import ar.com.dinamicaonline.rapipago.service.ReceiveAndSendService;

@RestController
@RequestMapping("/api/v1")
public class RapipagoController {

    @Autowired
    private ReceiveAndSendService receiveAndSendService;

    @RequestMapping(value = "/consulta", method = RequestMethod.POST)
    public ResponseEntity<?> obtenerConsulta(@RequestBody ConsultaDto consultaDto) {
        return receiveAndSendService.saveReceiveAndSend(consultaDto);
    }

    @RequestMapping(value = "/pago", method = RequestMethod.POST)
    public ResponseEntity<?> obtenerPago(@RequestBody PagoDto pagoDto) {
        return receiveAndSendService.saveReceiveAndSend(pagoDto);
    }

    @RequestMapping(value = "/reversa", method = RequestMethod.POST)
    public ResponseEntity<?> obtenerReversa(@RequestBody ReversaDto reversaDto) {
        return receiveAndSendService.saveReceiveAndSend(reversaDto);
    }

}
