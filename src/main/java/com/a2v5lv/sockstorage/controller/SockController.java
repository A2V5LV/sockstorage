package com.a2v5lv.sockstorage.controller;

import com.a2v5lv.sockstorage.dto.SockResponseResult;
import com.a2v5lv.sockstorage.entity.Sock;
import com.a2v5lv.sockstorage.repository.SockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // указываем что контроллер будет работать в режиме REST
public class SockController {

    private SockRepository sockRepository; // использование репо с помощью конструктора (работает только в Spring)

    public SockController(SockRepository sockRepository) {  // инициализация конструктора
        this.sockRepository = sockRepository;   // Spring сам все дальше будет вызывать
        // (инверсия управления и внедрение зависимостей)
    }

    // создание POST запроса на запись информации на сервер:

    @PostMapping("/api/socks/income")   // при обращении по url работает этот метод
    public ResponseEntity<?> addSocksToSockStorage(@RequestBody Sock sock) {  // говорим что передаем объект типа Sock
        // с параметрами
        Sock retriveSock = sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart());

        if (retriveSock == null) {
            sockRepository.save(sock); // добавление объекта в БД
        } else {
            retriveSock.setQuantity(retriveSock.getQuantity() + sock.getQuantity());  // запись нового значения
            sockRepository.save(retriveSock);  // работает как update, т.к. id объекта известен
            return ResponseEntity.status(200).build(); // возвращаем результат
        }
        return ResponseEntity.status(200).build(); // возвращаем результат
    }

    @PostMapping("/api/socks/outcome")   // при обращении по url работает этот метод
    public ResponseEntity<?> subSocksFromSockStorage(@RequestBody Sock sock) {
        // говорим что передаем объект типа Sock с параметрами

        Sock retriveSock = sockRepository.findByColorAndCottonPart(sock.getColor(), sock.getCottonPart());

        if (retriveSock != null) {

            int newQuantity = retriveSock.getQuantity() - sock.getQuantity();

            if (newQuantity >= 0) {
                retriveSock.setQuantity(retriveSock.getQuantity() - sock.getQuantity());  // запись нового значения
                sockRepository.save(retriveSock);  // работает как update, т.к. id объекта известен
                return ResponseEntity.status(200).build(); // возвращаем результат
            }
        }
        return ResponseEntity.status(400).build(); // возврат ошибки
    }

    @GetMapping("/api/socks")   // реализация GET запроса с параметрами
    public ResponseEntity<SockResponseResult> getSockByColorAndCottonPart(@RequestParam String color,
                                                                          @RequestParam String operation,
                                                                          @RequestParam Integer cottonPart) {

        Integer quantity = null;
        switch (operation) {
            case "moreThan":
                quantity = sockRepository.sumByColorAndCottonPartMoreThan(color, cottonPart);
                break;
            case "lessThan":
                quantity = sockRepository.sumByColorAndCottonPartLessThan(color, cottonPart);
                break;
            case "equal":
                quantity = sockRepository.sumByColorAndCottonPartEqual(color, cottonPart);
        }

        if (quantity == null) {
            quantity = 0;
        }

        return ResponseEntity
                .status(200)
                .body(new SockResponseResult(200, Integer.toString(quantity)));
    }
}
