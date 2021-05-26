package lighthouse.assignment.repository;

import lighthouse.assignment.model.Chat;
import lighthouse.assignment.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllBySender(Client sender);

}
