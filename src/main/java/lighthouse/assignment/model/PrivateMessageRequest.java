package lighthouse.assignment.model;

public class PrivateMessageRequest {

    private Client client;

    private String message;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
