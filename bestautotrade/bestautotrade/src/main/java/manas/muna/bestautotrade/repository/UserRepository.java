package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.User;
import manas.muna.bestautotrade.service.UserKey;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, UserKey> {
    List<User> findByAge(int age);
}