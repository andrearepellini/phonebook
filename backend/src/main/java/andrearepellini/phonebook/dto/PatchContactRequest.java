package andrearepellini.phonebook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PatchContactRequest {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("deleted")
    private Boolean deleted;
}
