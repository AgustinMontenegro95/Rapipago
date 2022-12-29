package ar.com.dinamicaonline.rapipago.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;
import ar.com.dinamicaonline.rapipago.service.ReceiveAndSendService;

@RestController
@RequestMapping("/api/v1")
public class RapipagoController {

    @Autowired
    private ReceiveAndSendService receiveAndSendService;

    @RequestMapping(value = "/pago", method = RequestMethod.POST)
    public Map<String, Object> getState(@RequestBody PagoDto pagoDto) {
        Map<String, Object> map = new HashMap<String, Object>();
        ReceiveAndSend receiveAndSend = new ReceiveAndSend();

        boolean result = receiveAndSendService.saveReceiveAndSend(receiveAndSend, pagoDto);
        if (result) {
            map.put("id_numero", pagoDto.getId_numero());
            map.put("cod_trx", pagoDto.getCod_trx());
            map.put("barra", pagoDto.getBarra());
            map.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
            // codigo_respuesta=0 -> Transacción aceptada
            map.put("codigo_respuesta", "0");
            map.put("msg", "Trx ok");
        } else {
            map.put("id_numero", pagoDto.getId_numero());
            map.put("cod_trx", pagoDto.getCod_trx());
            map.put("barra", pagoDto.getBarra());
            map.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
            // codigo_respuesta=0 -> Transacción aceptada
            map.put("codigo_respuesta", "9");
            map.put("msg", "Parámetros incorrectos o faltantes");
        }

        return map;
    }
}
