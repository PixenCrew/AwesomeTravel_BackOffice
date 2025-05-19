package renewal.awesome_travel_backoffice.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
public class Admin {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long admin_id;

  @Column(unique = true)
  private String id;

  @Column
  private String password;

  @Column
  private Role role;

}
