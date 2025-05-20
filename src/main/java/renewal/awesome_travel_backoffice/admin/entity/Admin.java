package renewal.awesome_travel_backoffice.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Admin {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Long admin_id;

  @Column(unique = true)
  private final String id;

  private final String password;
  
  @Enumerated(EnumType.STRING)
  private final Role role;

}
