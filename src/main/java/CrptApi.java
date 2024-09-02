import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import kong.unirest.core.Unirest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrptApi {

    private final RateLimiter rateLimiter;
    private static final String URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";


    public CrptApi(TimeUnit timeUnit, int requestLimit) {

        if (requestLimit > 0)
            this.rateLimiter = RateLimiter.create(requestLimit / (timeUnit.toSeconds(1) / 1000.0));
        else
            throw new IllegalArgumentException("Передано неположительное число запросов в единцу времени");

    }

    public void  sendDocument(DocumentDto documentDto, String signature) {

        //не ясно, что делать с подписью

        rateLimiter.acquire();

        Unirest.post(URL)
                .header("content-type", "application/json")
                .body(DocumentDtoToJson(documentDto));

    }

    private String DocumentDtoToJson(DocumentDto documentDto)
    {
        try {
            return  new ObjectMapper().writeValueAsString(documentDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка преобразования в JSON");
        }

    };

//--------------------------------------------------

    @Getter
    @Builder
    public static class DocumentDto {
        //todo валидация полей при необходимости
        //todo обязательные поля должны быть помещены в конструктор. Но перечень обязательных полей не определен

        private final String doc_id;
        private final String doc_status;

        private final DocType doc_type;
        private final boolean importRequest;

        private final String owner_inn;
        private final String participant_inn;
        private final String producer_inn;

        private final Date production_date;

        private final String production_type;

        private final String reg_number;
        private final Date reg_date;

        private final List<ProductDto> products;
    }
        //--------------------------------------------------

        @Setter
        @Getter
        public static class DocumentDescriptionDto {
            //todo валидация полей при необходимости
            private final String participantInn;

            public DocumentDescriptionDto(String participantInn) {
                this.participantInn = participantInn;
            }
        }

        //--------------------------------------------------

        @Getter
        @Builder
        public static class ProductDto {

            //todo валидация полей при необходимости
            //todo обязательные поля должны быть помещены в конструктор. Но перечень обязательных полей не определен

            private  final String certificate_document;
            private  final Date certificate_document_date;
            private  final String certificate_document_number;

            private final String owner_inn;
            private final String producer_inn;

            private final Date production_date;

            private final String tnved_code;
            private final String uit_code;
            private final String uitu_code;



        }

        //--------------------------------------------------

        public enum DocType {

            LP_INTRODUCE_GOODS

        }


}