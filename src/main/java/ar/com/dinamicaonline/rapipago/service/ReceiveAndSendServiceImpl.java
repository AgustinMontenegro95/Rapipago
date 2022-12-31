package ar.com.dinamicaonline.rapipago.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ar.com.dinamicaonline.rapipago.dto.ClienteDto;
import ar.com.dinamicaonline.rapipago.dto.ConsultaDto;
import ar.com.dinamicaonline.rapipago.dto.PagoDto;
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
        ReceiveAndSend receiveAndSend = new ReceiveAndSend();
        Map<String, Object> responseBody = new HashMap<>();

        if (Validation.validationAvisoDto(pagoDto)) {
            // obtengo el idAccount necesario para api_ReceiveAndSend
            Long idAccount = entidadServiceImpl.fetchEntidadByDocumentId(pagoDto.getId_numero());
            // guardo consulta
            receiveAndSend.setCreatedOn(Calendar.getInstance().getTime());
            receiveAndSend.setIdAccount(idAccount);
            receiveAndSend.setIdOrigin(idOrigin);
            receiveAndSend.setReceiveSend(1);
            receiveAndSend.setMessagge(pagoDto.toString());
            receiveAndSendRepository.save(receiveAndSend);
            if (idAccount != null) {
                // proc_cash_in_app
                double QPAGO = Double.parseDouble(pagoDto.getImporte());
                String QORIGEN = "pagofacil";
                Long QID_ENTIDAD = idAccount;
                // llamada a procedimiento para registrar el pago
                String resProcCashInApp = entidadServiceImpl.ingresoDineroProc(QID_ENTIDAD, QPAGO, QORIGEN);
                // creo la respuesta a la llamada /pago
                responseBody.put("id_numero", pagoDto.getId_numero());
                responseBody.put("cod_trx", pagoDto.getCod_trx());
                responseBody.put("barra", pagoDto.getBarra());
                responseBody.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
                responseBody.put("codigo_respuesta", "0");
                responseBody.put("msg", "Trx ok");
                // guardo respuesta
                ReceiveAndSend receiveAndSend2 = new ReceiveAndSend();
                receiveAndSend2.setCreatedOn(Calendar.getInstance().getTime());
                receiveAndSend2.setIdAccount(QID_ENTIDAD);
                receiveAndSend2.setIdOrigin(idOrigin);
                receiveAndSend2.setReceiveSend(2);
                String resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                        + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                        + "\", \"barra\":\"" + pagoDto.getBarra()
                        + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                        + "\", \"codigo_respuesta\":\"" + "0"
                        + "\", \"msg\":\"" + "Trx ok"
                        + "\"}";
                receiveAndSend2.setMessagge("{\"resultado\":" + resProcCashInApp
                        + ", \"respuesta\": " + resBody
                        + ", \"idNumero\": \"" + pagoDto.getId_numero()
                        + "\", \"importe\": \"" + pagoDto.getImporte()
                        + "\"}");
                receiveAndSendRepository.save(receiveAndSend2);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                // No existe el id/dni
                // creo la respuesta a la llamada /consulta
                responseBody.put("id_numero", pagoDto.getId_numero());
                responseBody.put("cod_trx", pagoDto.getCod_trx());
                responseBody.put("barra", pagoDto.getBarra());
                responseBody.put("fecha_hora_operacion", pagoDto.getFecha_hora_operacion());
                responseBody.put("codigo_respuesta", "9");
                responseBody.put("msg", "Parámetros incorrectos o faltantes");
                // guardo respuesta
                receiveAndSend.setCreatedOn(Calendar.getInstance().getTime());
                receiveAndSend.setIdAccount(null);
                receiveAndSend.setIdOrigin(idOrigin);
                receiveAndSend.setReceiveSend(2);
                String resBody = "{\"id_numero\":\"" + pagoDto.getId_numero()
                        + "\", \"cod_trx\":\"" + pagoDto.getCod_trx()
                        + "\", \"barra\":\"" + pagoDto.getBarra()
                        + "\", \"fecha_hora_operacion\":\"" + pagoDto.getFecha_hora_operacion()
                        + "\", \"codigo_respuesta\":\"" + "9"
                        + "\", \"msg\":\"" + "Parámetros incorrectos o faltantes"
                        + "\"}";
                receiveAndSend.setMessagge(
                        "{\"error\":\"documento no encontrado\", \"solicitud\":" + pagoDto.toString()
                                + ", \"respuesta\":" + resBody + "}");
                receiveAndSendRepository.save(receiveAndSend);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }

        } else {

        }

        return null;
    }

    @Override
    public boolean saveReceiveAndSend(ReceiveAndSend receiveAndSend, PagoDto pagoDto) {
        int idOrigin = 7; // 7 por tabla api_ReceiveAndSendOrigin

        if (Validation.validationAvisoDto(pagoDto)) {
            Long idAccount = entidadServiceImpl.fetchEntidadByDocumentId(pagoDto.getId_numero());
            if (idAccount != null) {
                // receiveAndSend.setId();
                receiveAndSend.setCreatedOn(Calendar.getInstance().getTime());
                receiveAndSend.setIdAccount(idAccount);
                receiveAndSend.setIdOrigin(idOrigin);
                receiveAndSend.setReceiveSend(1);
                receiveAndSend.setMessagge(pagoDto.toString());
                receiveAndSendRepository.save(receiveAndSend);
                //
                // proc_cash_in_app
                double QPAGO = Double.parseDouble(pagoDto.getImporte());
                String QORIGEN = "pagofacil";
                Long QID_ENTIDAD = idAccount;
                // llamada a procedimiento para registrar el pago
                String resProcCashInApp = entidadServiceImpl.ingresoDineroProc(QID_ENTIDAD, QPAGO, QORIGEN);
                ReceiveAndSend receiveAndSend2 = new ReceiveAndSend();
                receiveAndSend2.setCreatedOn(Calendar.getInstance().getTime());
                receiveAndSend2.setIdAccount(QID_ENTIDAD);
                receiveAndSend2.setIdOrigin(idOrigin);
                receiveAndSend2.setReceiveSend(2);
                receiveAndSend2.setMessagge("{\"result\":" + resProcCashInApp + ", \"idNumero\": \""
                        + pagoDto.getId_numero() + "\", \"importe\": \"" + pagoDto.getImporte() + "\"}");
                receiveAndSendRepository.save(receiveAndSend2);
            } else {
                // No existe el id/dni
                // receiveAndSend.setId();
                receiveAndSend.setCreatedOn(Calendar.getInstance().getTime());
                receiveAndSend.setIdAccount(null);
                receiveAndSend.setIdOrigin(idOrigin);
                receiveAndSend.setReceiveSend(2);
                receiveAndSend.setMessagge(
                        "{\"error\":\"Documento no encontrado\", \"request\":" + pagoDto.toString() + "}");
                receiveAndSendRepository.save(receiveAndSend);
            }
            return true;
        } else {
            // error guardar consulta
            return false;
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
            // error al validar los datos de la consulta
        }
        return null;
    }

}
