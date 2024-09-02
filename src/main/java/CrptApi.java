import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
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

    @Setter
    @Getter
    public static class DocumentDto
    {
        //todo валидация полей при необходимости

        private String doc_id;
        private String doc_status;

        private DocType doc_type;
        private boolean importRequest;

        private String owner_inn;
        private String participant_inn;
        private String producer_inn;

        private Date production_date;

        private String production_type;

        private String reg_number;
        private Date reg_date;

        //--------------------------------------------------

        @Setter
        @Getter
        public static class DocumentDescription {
            //todo валидация полей при необходимости
            private String participantInn;
        }

        //--------------------------------------------------

        @Getter
        @Setter
        public static class ProductDto {

            //todo валидация полей при необходимости

            private String certificate_document;
            private Date certificate_document_date;
            private String certificate_document_number;

            private String owner_inn;
            private String producer_inn;

            private Date production_date;

            private String tnved_code;
            private String uit_code;
            private String uitu_code;



        }

        //--------------------------------------------------

        public enum DocType {

            LP_INTRODUCE_GOODS

        }


}