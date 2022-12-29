package ar.com.dinamicaonline.rapipago.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.dinamicaonline.rapipago.dto.PagoDto;
import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;
import ar.com.dinamicaonline.rapipago.repositories.ReceiveAndSendRepository;
import ar.com.dinamicaonline.rapipago.validations.PagoDtoValidation;

@Service
public class ReceiveAndSendServiceImpl implements ReceiveAndSendService {

    @Autowired
    private ReceiveAndSendRepository receiveAndSendRepository;

    @Autowired
    private EntidadService entidadServiceImpl;

    @Override
    public boolean saveReceiveAndSend(ReceiveAndSend receiveAndSend, PagoDto pagoDto) {
        int idOrigin = 7; // 7 por tabla api_ReceiveAndSendOrigin

        if (PagoDtoValidation.validationAvisoDto(pagoDto)) {
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
            // error
            return false;
        }
    }

}
