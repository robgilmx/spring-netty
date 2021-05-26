package lighthouse.assignment.repository;

import lighthouse.assignment.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByIdAndDisplayName(Long id, String displayName);

    List<Client> findAllByConnected(boolean isConnected);

    Client findFirstByDisplayName(String name);

}
