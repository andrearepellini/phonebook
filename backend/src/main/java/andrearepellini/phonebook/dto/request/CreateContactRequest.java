package andrearepellini.phonebook.dto.request;

import lombok.Data;

@Data
public class CreateContactRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Integer age;
}
