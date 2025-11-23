package schabaschka.chat.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import schabaschka.chat.dto.NewOfferMessageDto;
import schabaschka.chat.dto.OfferMessageDto;
import schabaschka.chat.service.OfferMessageServiceImpl;


import java.util.List;

@RestController
@RequestMapping("/api/offers/{offerId}/messages")
public class OfferMessageController {

private final OfferMessageServiceImpl offerMessageService;

    public OfferMessageController(OfferMessageServiceImpl offerMessageService) {
        this.offerMessageService = offerMessageService;
    }


    @GetMapping
    public List<OfferMessageDto> getMessages(@PathVariable("offerId") long offerId){
        return  offerMessageService.getMessageByOfferId(offerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferMessageDto createMessage(@PathVariable("offerId") long offerId, @RequestBody NewOfferMessageDto newOfferMessageDto){
        return offerMessageService.addMessage(offerId, newOfferMessageDto);
    }

}
