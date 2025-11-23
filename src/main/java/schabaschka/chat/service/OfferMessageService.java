package schabaschka.chat.service;

import schabaschka.chat.dto.NewOfferMessageDto;
import schabaschka.chat.dto.OfferMessageDto;

import java.util.List;

public interface   OfferMessageService {
    List<OfferMessageDto> getMessageByOfferId(Long offerId);

    OfferMessageDto addMessage(Long offerId, NewOfferMessageDto newOfferMessageDto);

}
