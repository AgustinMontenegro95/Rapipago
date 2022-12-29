package ar.com.dinamicaonline.rapipago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.dinamicaonline.rapipago.models.ReceiveAndSend;

@Repository
public interface ReceiveAndSendRepository extends JpaRepository<ReceiveAndSend, Integer> {

}