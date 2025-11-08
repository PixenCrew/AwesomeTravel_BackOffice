package renewal.awesome_travel_backoffice.admin;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Admin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Long adminId;

  @Column(unique = true)
  private final String id;

  private final String password;

  private final String name; // 이름
  private final String position; // 직급
  private final String email;
  private final String number;
  private final String fax;

  @Enumerated(EnumType.STRING)
  private final Role role;

  public enum Role {
    ADMIN, SERVICE
  }

}
