package ar.com.dinamicaonline.rapipago.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ar.com.dinamicaonline.rapipago.dto.ClienteDto;
import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.dto.ReversaDto;
import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;
import ar.com.dinamicaonline.rapipago.repositories.ReceiveAndSendRepository;
import ar.com.dinamicaonline.rapipago.validations.Validation;

@Service
public class ReceiveAndSendServiceImpl implements ReceiveAndSendService {

    @Autowired
    private ReceiveAndSendRepository receiveAndSendRepository;

    @Autowired
    private EntidadService entidadServiceImpl;

    @Override
    public ResponseEntity<?> saveReceiveAndSend(PagoDto pagoDto) {
        int idOrigin = 7; // 7 por tabla api_ReceiveAndSendOrigin
        Map<String, Object> responseBody = new HashMap<>();

        if (Validation.validationAvisoDto(pagoDto)) {
            // obtengo el idAccount necesario para api_ReceiveAndSend
            Long idAccount = entidadServiceImpl.fetchEntidadByDocumentId(pagoDto.getId_numero());
            // guardo consulta
            ReceiveAndSend rasConsulta = new ReceiveAndSend();
            rasConsulta.setCreatedOn(Calendar.getInstance().getTime());
            rasConsulta.setIdAccount(idAccount);
            rasConsulta.setIdOrigin(idOrigin);
            rasConsulta.setReceiveSend(1);
            rasConsulta.setMessagge(pagoDto.toString());
            receiveAndSendRepository.save(rasConsulta);
            if (idAccount != null) {
                // proc_cash_in_app
                double QPAGO = Double.parseDouble(pagoDto.getImporte());
                String QORIGEN = "pagofacil";
                Long QID_ENTIDAD = idAccount;
                // llamada a procedimiento para registrar el pago
                String resProcCashInApp = entidadServiceImpl.ingresoDineroProc(QID_ENTIDAD, QPAGO, QORIGEN);

                System.out.println("Soy procCashInAPP: " + resProcCashInApp);
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(resProcCashInApp.toString());
                } catch (ParseException e) {
                    System.out.println("Error al decodificar respuesta de BIND");
                    System.out.println(resProcCashInApp.toString());
                }

                // id del resultado de proc_cash_in_app
                int id = Integer.parseInt(json.get("id").toString());
                System.out.println("soy id: " + id);
                
                // empiezo a crear la respuesta a la llamada /pago
                responseBody.put("id_numero", pagoDto.getId_numero());
                responseBody.put("cod_trx", pagoDto.getCod_trx());
                responseBody.put("barra", pagoDto.getBarra());
                responseBody.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
                
                // empiezo a crear la respuesta a la llamada /pago
                String resBody;
                ReceiveAndSend rasRespuesta = new ReceiveAndSend();
                rasRespuesta.setCreatedOn(Calendar.getInstance().getTime());
                rasRespuesta.setIdAccount(QID_ENTIDAD);
                rasRespuesta.setIdOrigin(idOrigin);
                rasRespuesta.setReceiveSend(2);

                switch (id) {
                    case 1:
                    // mensaje: "Cobranza Exitosa"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "0");
                        responseBody.put("msg", "Cobranza Exitosa");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "0"
                                + "\", \"msg\":\"" + "Cobranza Exitosa"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case 2:
                        // aviso: "Pago aplicado por $ 1000"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "0");
                        responseBody.put("msg", "Cobranza Exitosa");
                        // guardo respuesta
                        rasRespuesta.setReceiveSend(2);
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "0"
                                + "\", \"msg\":\"" + "Cobranza Exitosa"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case 10:
                        // mensaje: "Cobranza Judicial Exitosa. Pago aplicado por $ 1000" -- cobranza abogados
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "0");
                        responseBody.put("msg", "Cobranza Exitosa");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "0"
                                + "\", \"msg\":\"" + "Cobranza Exitosa"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case -4:
                        // mensaje: "Medio no existe !!"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "5");
                        responseBody.put("msg", "Medio no existe !!");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "5"
                                + "\", \"msg\":\"" + "Medio no existe !!"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case -5:
                        // mensaje: "Depósito no habilitado"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "5");
                        responseBody.put("msg", "Depósito no habilitado");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "5"
                                + "\", \"msg\":\"" + "Depósito no habilitado"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case -6:
                        // mensaje: "Medio no disponible"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "5");
                        responseBody.put("msg", "Medio no disponible");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "5"
                                + "\", \"msg\":\"" + "Medio no disponible"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case -7:
                        // mensaje: "Importe de depósito supera el límite"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "9");
                        responseBody.put("msg", "Importe de depósito supera el límite");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "9"
                                + "\", \"msg\":\"" + "Importe de depósito supera el límite"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    case -8:
                        // mensaje: "Importe de depósito inferior al mínimo"
                        // termino la creación de la respuesta a la llamada /pago
                        responseBody.put("codigo_respuesta", "9");
                        responseBody.put("msg", "Importe de depósito inferior al mínimo");
                        // guardo respuesta
                        resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                                + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                                + "\", \"barra\":\"" + pagoDto.getBarra()
                                + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                                + "\", \"codigo_respuesta\":\"" + "9"
                                + "\", \"msg\":\"" + "Importe de depósito inferior al mínimo"
                                + "\"}";
                        rasRespuesta.setMessagge("{\"resultado\":" + resProcCashInApp
                                + ", \"respuesta\": " + resBody
                                + ", \"idNumero\": \"" + pagoDto.getId_numero()
                                + "\", \"importe\": \"" + pagoDto.getImporte()
                                + "\"}");
                        receiveAndSendRepository.save(rasRespuesta);
                        return new ResponseEntity<>(responseBody, HttpStatus.OK);
                    default:
                        return null;
                }
            } else {
                // No existe el id/dni - (simula este id) -> { "id": 0, "mensaje": "Entidad NO existe" }
                // creo la respuesta a la llamada /pago
                responseBody.put("id_numero", pagoDto.getId_numero());
                responseBody.put("cod_trx", pagoDto.getCod_trx());
                responseBody.put("barra", pagoDto.getBarra());
                responseBody.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
                responseBody.put("codigo_respuesta", "9");
                responseBody.put("msg", "Parámetros incorrectos o faltantes");
                // guardo respuesta
                ReceiveAndSend rasError = new ReceiveAndSend();
                rasError.setCreatedOn(Calendar.getInstance().getTime());
                rasError.setIdAccount(null);
                rasError.setIdOrigin(idOrigin);
                rasError.setReceiveSend(2);
                String resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                        + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                        + "\", \"barra\":\"" + pagoDto.getBarra()
                        + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                        + "\", \"codigo_respuesta\":\"" + "9"
                        + "\", \"msg\":\"" + "Parametros incorrectos o faltantes"
                        + "\"}";
                rasError.setMessagge(
                        "{\"resultado\":\"documento no encontrado\", \"respuesta\":" + resBody + "}");
                receiveAndSendRepository.save(rasError);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }

        } else {
            responseBody.put("codigo_respuesta", "9");
            responseBody.put("msg", "Parámetros incorrectos o faltantes");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> saveReceiveAndSend(ReversaDto reversaDto) {
        int idOrigin = 7; // 7 por tabla api_ReceiveAndSendOrigin
        Map<String, Object> responseBody = new HashMap<>();

        if (Validation.validationReversaDto(reversaDto)) {
            // obtengo el idAccount necesario para api_ReceiveAndSend
            Long idAccount = entidadServiceImpl.fetchEntidadByDocumentId(reversaDto.getId_numero());
            // guardo consulta
            ReceiveAndSend rasConsulta = new ReceiveAndSend();
            rasConsulta.setCreatedOn(Calendar.getInstance().getTime());
            rasConsulta.setIdAccount(idAccount);
            rasConsulta.setIdOrigin(idOrigin);
            rasConsulta.setReceiveSend(1);
            rasConsulta.setMessagge(reversaDto.toString());
            receiveAndSendRepository.save(rasConsulta);
            if (idAccount != null) {
                responseBody.put("id_numero", reversaDto.getId_numero());
                responseBody.put("cod_trx", reversaDto.getCod_trx());
                responseBody.put("barra", reversaDto.getBarra());
                responseBody.put("fecha_hora_operacion", reversaDto.getFecha_hora_operacion());
                responseBody.put("codigo_respuesta", "5");
                responseBody.put("msg", "Operación inválida");
                // guardo respuesta
                ReceiveAndSend rasRespuesta = new ReceiveAndSend();
                rasRespuesta.setCreatedOn(Calendar.getInstance().getTime());
                rasRespuesta.setIdAccount(idAccount);
                rasRespuesta.setIdOrigin(idOrigin);
                rasRespuesta.setReceiveSend(2);
                String resBody = "{\"id_numero\":\"" + reversaDto.getId_numero()
                        + "\", \"cod_trx\":\"" + reversaDto.getCod_trx()
                        + "\", \"barra\":\"" + reversaDto.getBarra()
                        + "\", \"fecha_hora_operacion\":\"" + reversaDto.getFecha_hora_operacion()
                        + "\", \"codigo_respuesta\":\"" + "5"
                        + "\", \"msg\":\"" + "Operacion invalida"
                        + "\"}";
                rasRespuesta.setMessagge("{\"respuesta\": " + resBody
                        + ", \"idNumero\": \"" + reversaDto.getId_numero()
                        + "\", \"importe\": \"" + reversaDto.getImporte()
                        + "\"}");
                receiveAndSendRepository.save(rasRespuesta);
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            } else {
                // No existe el id/dni
                // creo la respuesta a la llamada /reversa
                responseBody.put("id_numero", reversaDto.getId_numero());
                responseBody.put("cod_trx", reversaDto.getCod_trx());
                responseBody.put("barra", reversaDto.getBarra());
                responseBody.put("fecha_hora_operacion", reversaDto.getFecha_hora_operacion());
                responseBody.put("codigo_respuesta", "9");
                responseBody.put("msg", "Parámetros incorrectos o faltantes");
                // guardo respuesta
                ReceiveAndSend rasError = new ReceiveAndSend();
                rasError.setCreatedOn(Calendar.getInstance().getTime());
                rasError.setIdAccount(null);
                rasError.setIdOrigin(idOrigin);
                rasError.setReceiveSend(2);
                String resBody = "{\"id_numero\":\"" + reversaDto.getId_numero()
                        + "\", \"cod_trx\":\"" + reversaDto.getCod_trx()
                        + "\", \"barra\":\"" + reversaDto.getBarra()
                        + "\", \"fecha_hora_operacion\":\"" + reversaDto.getFecha_hora_operacion()
                        + "\", \"codigo_respuesta\":\"" + "9"
                        + "\", \"msg\":\"" + "Parametros incorrectos o faltantes"
                        + "\"}";
                rasError.setMessagge(
                        "{\"resultado\":\"documento no encontrado\", \"respuesta\":" + resBody + "}");
                receiveAndSendRepository.save(rasError);
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

        } else {
            responseBody.put("codigo_respuesta", "9");
            responseBody.put("msg", "Parámetros incorrectos o faltantes");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> saveReceiveAndSend(ConsultaDto consultaDto) {
        int idOrigin = 7; // 7 = rapipago por tabla api_ReceiveAndSendOrigin
        ReceiveAndSend receiveAndSend = new ReceiveAndSend();
        Map<String, Object> responseBody = new HashMap<>();

        if (Validation.validationConsultaDto(consultaDto)) {
            // obtengo el idAccount necesario para api_ReceiveAndSend
            Long idAccount = entidadServiceImpl.fetchEntidadByDocumentId(consultaDto.getId_clave());
            // guardo consulta
            receiveAndSend.setCreatedOn(Calendar.getInstance().getTime());
            receiveAndSend.setIdAccount(idAccount);
            receiveAndSend.setIdOrigin(idOrigin);
            receiveAndSend.setReceiveSend(1);
            receiveAndSend.setMessagge(consultaDto.toString());
            receiveAndSendRepository.save(receiveAndSend);
            Map<String, Object> factura = new HashMap<String, Object>();
            if (idAccount != null) {
                // obtengo nombre y apellido del cliente
                ClienteDto clienteDto = entidadServiceImpl.obtenerNombreApellido(consultaDto.getId_clave());
                // obtengo el importe a pagar
                String importe = entidadServiceImpl.importePagarFunc(idAccount).toString().replace(".0", ".00");
                String fechaHoy = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        .format(LocalDateTime.now());
                String codBarra = "931" + String.format("%0" + 12 + "d", Integer.parseInt(consultaDto.getId_clave()))
                        + String.format("%0" + 11 + "d", Integer.parseInt(importe.replace(".", "")));
                // creo la respuesta a la llamada /consulta
                responseBody.put("id_clave", consultaDto.getId_clave());
                responseBody.put("nombre", clienteDto.getNombre().trim());
                responseBody.put("apellido", clienteDto.getApellido().trim());
                responseBody.put("cod_trx", consultaDto.getCod_trx());
                responseBody.put("codigo_respuesta", "0");
                responseBody.put("msg", "Trx ok");
                responseBody.put("dato_adicional", "");
                factura.put("id_numero", consultaDto.getId_clave());
                factura.put("fecha_vencimiento", fechaHoy);
                factura.put("fecha_emision", fechaHoy);
                factura.put("importe", importe);
                factura.put("barra", codBarra);
                factura.put("texto1", "");
                responseBody.put("facturas", factura);
                //
                // guardo respuesta
                ReceiveAndSend rasRespuesta = new ReceiveAndSend();
                rasRespuesta.setCreatedOn(Calendar.getInstance().getTime());
                rasRespuesta.setIdAccount(idAccount);
                rasRespuesta.setIdOrigin(7);
                rasRespuesta.setReceiveSend(2);
                String facturaJson = "{"
                        + "\"id_numero\":\"" + consultaDto.getId_clave()
                        + "\", \"fecha_vencimiento\":\"" + fechaHoy
                        + "\", \"fecha_emision\":\"" + fechaHoy
                        + "\", \"importe\":\"" + importe
                        + "\", \"barra\":\"" + codBarra
                        + "\", \"texto1\":\"" + ""
                        + "\"}";
                String msgRespuesta = "{"
                        + "\"id_clave\":\"" + consultaDto.getId_clave()
                        + "\", \"nombre\":\"" + clienteDto.getNombre()
                        + "\", \"apellido\":\"" + clienteDto.getApellido()
                        + "\", \"cod_trx\":\"" + consultaDto.getCod_trx()
                        + "\", \"codigo_respuesta\":\"" + "0"
                        + "\", \"msg\":\"" + "Trx ok"
                        + "\", \"dato_adicional\":\"" + ""
                        + "\", \"factura\":[" + facturaJson
                        + "]}";
                rasRespuesta.setMessagge(msgRespuesta);
                receiveAndSendRepository.save(rasRespuesta);
                //
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                // creo la respuesta a la llamada /consulta
                responseBody.put("id_clave", consultaDto.getId_clave());
                responseBody.put("nombre", null);
                responseBody.put("apellido", null);
                responseBody.put("cod_trx", consultaDto.getCod_trx());
                responseBody.put("codigo_respuesta", "7");
                responseBody.put("msg", "Cliente inexistente");
                responseBody.put("dato_adicional", null);
                responseBody.put("facturas", "[]");
                //
                // guardo respuesta
                ReceiveAndSend rasRespuesta = new ReceiveAndSend();
                rasRespuesta.setCreatedOn(Calendar.getInstance().getTime());
                rasRespuesta.setIdAccount(idAccount);
                rasRespuesta.setIdOrigin(7);
                rasRespuesta.setReceiveSend(2);
                String msgRespuesta = "{"
                        + "\"id_clave\":\"" + consultaDto.getId_clave()
                        + "\", \"nombre\":\"" + null
                        + "\", \"apellido\":\"" + null
                        + "\", \"cod_trx\":\"" + consultaDto.getCod_trx()
                        + "\", \"codigo_respuesta\":\"" + "7"
                        + "\", \"msg\":\"" + "Cliente inexistente"
                        + "\", \"dato_adicional\":\"" + null
                        + "\", \"factura\":[]"
                        + "}";
                rasRespuesta.setMessagge(msgRespuesta);
                receiveAndSendRepository.save(rasRespuesta);
                //
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
        } else {
            responseBody.put("codigo_respuesta", "9");
            responseBody.put("msg", "Parametros incorrectos o faltantes");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
    }

}
