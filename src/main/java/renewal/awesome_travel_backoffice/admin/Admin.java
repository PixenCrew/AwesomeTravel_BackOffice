package renewal.awesome_travel_backoffice.admin;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

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

  @Enumerated(EnumType.STRING)
  private final Role role;

  public enum Role {
    ADMIN, SERVICE
  }

}
