package andrearepellini.phonebook.dto.response;

import lombok.Data;

@Data
public class ContactResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Integer age;
    private Boolean deleted;
}
