package manas.muna.bestautotrade.model;

import lombok.*;
import manas.muna.bestautotrade.service.UserKey;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
//    @Id
//    private String id;
////    @Id
//    private String name;
    @Id
    private UserKey key;
    private int age;

}
